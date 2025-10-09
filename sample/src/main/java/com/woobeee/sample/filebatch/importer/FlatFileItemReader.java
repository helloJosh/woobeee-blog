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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;
import java.util.regex.Pattern;

@Slf4j
@RequiredArgsConstructor
public class FlatFileItemReader implements ItemReader<JsonNode>, ItemStream, StepExecutionListener {
    private static final String LINE_COUNT_KEY = "csv.line.count";

    private final ObjectMapper objectMapper;
    private final S3Client s3Client;
    private final MinioConfig.MinioProperties minio;
    private final FlatFileHeaderExtractor flatFileHeaderExtractor;
    private final FlatFileIndexer flatFileIndexer;
    private final String delimiter;
    private final int chunkSize;

    private BufferedReader reader;
    private List<String> headers;
    private long currentLine;
    private String key;
    private List<FlatFileIndexer.IndexInfo> index;


    @Override
    public JsonNode read() throws Exception {
        String nextLine;
        if ((nextLine = reader.readLine()) != null) {
            String[] values = nextLine.split(delimiter, -1);
            ObjectNode json = objectMapper.createObjectNode();
            for (int i = 0; i < headers.size() && i < values.length; i++) {
                json.put(headers.get(i).trim(), values[i].trim());
            }
            currentLine++; // ★ 한 줄 처리했으니 증가
            return json;
        }
        return null;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        try {
            log.info("CSV Reader open()");

            String ctxKey = LINE_COUNT_KEY + key;

            this.currentLine = executionContext.getLong(ctxKey, 0L);
            long targetFileLine = 1L + currentLine; // 파일 라인 = 헤더 1줄 포함

            FlatFileIndexer.IndexInfo best = index.stream()
                    .filter(cp -> cp.getLine() <= targetFileLine)
                    .max(Comparator.comparingLong(FlatFileIndexer.IndexInfo::getLine))
                    .orElse(new FlatFileIndexer.IndexInfo(0L, 0L));

            long skipLines = targetFileLine - best.getLine();

            GetObjectRequest req = GetObjectRequest.builder()
                    .bucket(minio.getBucket())
                    .key(key)
                    .range("bytes=" + best.getOffset() + "-")
                    .build();

            ResponseInputStream<GetObjectResponse> is = s3Client.getObject(req);
            this.reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));

            // 5) 체크포인트~타깃 사이만 라인 스킵
            for (long i = 0; i < skipLines; i++) {
                if (reader.readLine() == null) break;
            }

            log.info("Aligned to fileLine={} (dataLine={}), checkpoint(line={}, offset={})",
                    targetFileLine, currentLine, best.getLine(), best.getOffset());

        } catch (Exception e) {
            throw new ItemStreamException("CSV Reader open 실패", e);
        }
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        String ctxKey = LINE_COUNT_KEY + key;
        executionContext.putLong(ctxKey, currentLine);
    }

    @Override
    public void close() throws ItemStreamException {
        try {
            reader.close();
        } catch (Exception e) {
            log.warn("CSV Reader 닫기 실패", e);
        }
    }

    @Override
    public void beforeStep(StepExecution stepExecution) {
        this.key = stepExecution.getJobParameters().getString("key", "test.csv");

        this.headers = flatFileHeaderExtractor.extractHeader(minio, s3Client, key);
        this.index = flatFileIndexer.extractIndex(minio, s3Client, key, chunkSize);

        log.info("Prepared header(size={}) and index(checkpoints={}) for key={}",
                headers.size(), index.size(), key);

    }
}
