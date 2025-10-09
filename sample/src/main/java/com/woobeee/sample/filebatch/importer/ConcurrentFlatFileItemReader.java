package com.woobeee.sample.filebatch.importer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.woobeee.sample.filebatch.config.MinioConfig;
import com.woobeee.sample.filebatch.support.FlatFileHeaderExtractor;
import com.woobeee.sample.filebatch.support.FlatFileIndexer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class ConcurrentFlatFileItemReader implements ItemReader<JsonNode>, ItemStream, StepExecutionListener {
    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minio;
    private final FlatFileHeaderExtractor flatFileHeaderExtractor;

    private BufferedReader reader;
    private List<String> headers;

    private String key;
    private Long startOffset;
    private Long endOffset;

    public ConcurrentFlatFileItemReader withPartitionRange(String key, Long startOffset, Long endOffset) {
        this.key = key;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
        return this;
    }

    @Override
    public JsonNode read() throws Exception {

        String next = reader.readLine();
        if (next == null) return null;

        String[] values = next.split(",");
        ObjectNode json = objectMapper.createObjectNode();
        for (int i = 0; i < headers.size() && i < values.length; i++) {
            json.put(headers.get(i).trim(), values[i].trim());
        }
        return json;
    }

    @Override
    public void open(ExecutionContext ctx) throws ItemStreamException {
        try {

            String range = "bytes=" + startOffset + "-";
            if (endOffset != null && endOffset > startOffset) {
                range = "bytes=" + startOffset + "-" + endOffset;
            }

            var req = GetObjectRequest.builder()
                    .bucket(minio.getBucket())
                    .key(key)
                    .range(range)
                    .build();

            log.info("Opening partial range from {} to {} for {}", startOffset, endOffset, key);

            ResponseInputStream<GetObjectResponse> ris = s3Client.getObject(req);
            this.reader = new BufferedReader(new InputStreamReader(ris, StandardCharsets.UTF_8));

        } catch (Exception e) {
            throw new ItemStreamException("Failed to open byte range reader", e);
        }
    }

    @Override
    public void update(ExecutionContext ctx) throws ItemStreamException {
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            if (reader != null) reader.close();
        } catch (Exception e) {
            log.warn("CSV Reader 닫기 실패", e);
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.key = stepExecution.getJobParameters().getString("key", "test.csv");
        this.headers = flatFileHeaderExtractor.extractHeader(minio, s3Client, key);
    }
}