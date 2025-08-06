package com.henriquenascimento.awslocalstack.aws.listener;

import io.awspring.cloud.sqs.annotation.SqsListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SqsMessageListener {

    private static final String TEST_QUEUE = "test-queue";

    @SqsListener(TEST_QUEUE)
    public void TEST_QUEUE(final String message) {
        log.info("Message from queue {} received with value: {} ", TEST_QUEUE, message);
    }
}
