package com.woobeee.sample.filebatch.support;

import com.woobeee.sample.filebatch.config.MinioConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class FlatFileHeaderExtractor {
    /**
     * 기본: 구분자 ',' / UTF-8
     */
    public List<String> extractHeader(
            MinioConfig.MinioProperties minioProperties,
            S3Client s3Client,
            String key
    ) {
        return extractHeader(minioProperties, s3Client, key, ',', StandardCharsets.UTF_8);
    }

    /**
     * 구분자/문자셋 지정 버전
     */
    public List<String> extractHeader(
            MinioConfig.MinioProperties minioProperties,
            S3Client s3Client,
            String key,
            char delimiter,
            Charset charset
    ) {
        GetObjectRequest req = GetObjectRequest.builder()
                .bucket(minioProperties.getBucket())
                .key(key)
                .build();

        try (ResponseInputStream<GetObjectResponse> in = s3Client.getObject(req)) {
            return readFirstHeaderLine(in, delimiter, charset);
        } catch (Exception e) {
            throw new RuntimeException("헤더 추출 실패: " + key, e);
        }
    }

    // ==== 내부 구현 ====
    private List<String> readFirstHeaderLine(InputStream in, char delimiter, Charset charset) throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(in, charset))) {
            String header = br.readLine(); // 첫 줄만 읽기
            if (header == null) {
                throw new IllegalArgumentException("빈 파일이거나 헤더가 없습니다.");
            }
            header = stripUtf8Bom(header);
            // CRLF 환경에서 남은 CR 제거
            if (header.endsWith("\r")) {
                header = header.substring(0, header.length() - 1);
            }
            return parseCsvLine(header, delimiter);
        }
    }

    /**
     * UTF-8 BOM 제거
     */
    private String stripUtf8Bom(String s) {
        if (s != null && s.length() > 0 && s.charAt(0) == '\uFEFF') {
            return s.substring(1);
        }
        return s;
    }

    /**
     * 간단하지만 안전한 CSV 파서(헤더 전용):
     * - 구분자(기본 ,)
     * - 큰따옴표 안의 구분자/따옴표 이스케이프("" -> ")
     */
    private List<String> parseCsvLine(String line, char delimiter) {
        List<String> out = new ArrayList<>();
        StringBuilder cur = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '\"') {
                    // 이스케이프된 따옴표 "" 처리
                    if (i + 1 < line.length() && line.charAt(i + 1) == '\"') {
                        cur.append('\"');
                        i++; // 다음 따옴표 스킵
                    } else {
                        inQuotes = false;
                    }
                } else {
                    cur.append(c);
                }
            } else {
                if (c == '\"') {
                    inQuotes = true;
                } else if (c == delimiter) {
                    out.add(cur.toString().trim());
                    cur.setLength(0);
                } else {
                    cur.append(c);
                }
            }
        }
        out.add(cur.toString().trim());
        return out;
    }
}
