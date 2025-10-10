package com.woobeee.back.event;

import com.woobeee.back.batch.ExportScheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@Slf4j
@RequiredArgsConstructor
public class MessageEventListener {
    private final ExportScheduler exportScheduler;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Async
    public void handleDatasetSavedEvent(MessageEvent event) throws Exception{
        exportScheduler.runExportJob();
    }
}
