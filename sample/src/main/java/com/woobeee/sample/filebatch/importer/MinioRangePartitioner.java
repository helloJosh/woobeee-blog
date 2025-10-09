package com.woobeee.sample.filebatch.importer;

import com.woobeee.sample.filebatch.config.MinioConfig;
import com.woobeee.sample.filebatch.support.FlatFileIndexer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@StepScope
@RequiredArgsConstructor
public class MinioRangePartitioner implements Partitioner {

    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minio;
    private final FlatFileIndexer indexer;

    @Value("#{jobParameters['key']}")
    private String key;
    @Value("#{jobParameters['chunkSize']}")
    private long chunkSize;


    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        if (key == null) {
            throw new IllegalArgumentException("Job parameter 'key' must be provided.");
        }

        log.info("Building partitions for file '{}' with approx {} lines per partition", key, chunkSize);

        List<FlatFileIndexer.IndexInfo> indexList =
                indexer.extractIndex(minio, s3Client, key, chunkSize);

        Map<String, ExecutionContext> partitions = new HashMap<>();

        for (int i = 0; i < indexList.size(); i++) {
            FlatFileIndexer.IndexInfo startInfo = indexList.get(i);
            FlatFileIndexer.IndexInfo endInfo = (i + 1 < indexList.size()) ? indexList.get(i + 1) : null;

            long startOffset = startInfo.getOffset();
            Long endOffset = (endInfo == null) ? null : (endInfo.getOffset() - 1); // 다음 구간 바로 전까지

            ExecutionContext ctx = new ExecutionContext();
            ctx.putString("key", key);
            ctx.putLong("startOffset", startOffset);
            if (endOffset != null) ctx.putLong("endOffset", endOffset);

            partitions.put("partition-" + i, ctx);

            log.info("Partition-{} => bytes [{} - {}] (line {}~{})",
                    i, startOffset, (endOffset == null ? "EOF" : endOffset),
                    startInfo.getLine(),
                    (endInfo == null ? "EOF" : endInfo.getLine()));
        }

        return partitions;
    }
}