package com.woobeee.sample.filebatch.support;

import com.woobeee.sample.filebatch.config.MinioConfig;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class FlatFileIndexer {
    private static final int BUFFER_SIZE = 1024 * 1024;

    public List<IndexInfo> extractIndex (
            MinioConfig.MinioProperties minioProperties,
            S3Client s3Client,
            String key,
            long chunkSize
    ) {
        List<IndexInfo> indexList = new ArrayList<>();

        try {
            log.info("Start indexing file: {}/{}", minioProperties.getBucket(), key);

            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(minioProperties.getBucket())
                    .key(key)
                    .build();

            try (ResponseInputStream<GetObjectResponse> in = s3Client.getObject(req);
                 BufferedInputStream bis = new BufferedInputStream(in, BUFFER_SIZE)) {

                long line = 0;
                long offset = 0;
                long nextCheckpoint = 0;

                // 파일 맨 처음 위치 (라인 0, offset 0)
                indexList.add(new IndexInfo(0, 0));

                int prev = -1;
                byte[] buf = new byte[BUFFER_SIZE];
                int n;

                while ((n = bis.read(buf)) != -1) {
                    for (int i = 0; i < n; i++) {
                        int b = buf[i] & 0xFF;
                        offset++;

                        boolean isNewline = false;
                        if (b == '\n') {
                            // CRLF 고려: 이전 바이트가 \r 이면 이미 1줄로 처리
                            isNewline = true;
                        }

                        if (isNewline) {
                            line++;

                            if (line >= nextCheckpoint + chunkSize) {
                                indexList.add(new IndexInfo(line, offset));
                                nextCheckpoint += chunkSize;
                            }
                        }
                        prev = b;
                    }
                }

                log.info("Indexing completed: totalLines={} checkpoints={}",
                        line, indexList.size());
                return indexList;

            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to build line-offset index from MinIO", e);
        }
    }


    @Data
    @AllArgsConstructor
    public static class IndexInfo {
        private long line;
        private long offset;
    }
}
