package com.henriquenascimento.awslocalstack.service;

import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class SqsService {

    private final SqsTemplate sqsTemplate;
    private static final String QUEUE_NAME = "test-queue";

    public void sendMessage(String message) {
        log.info("Sending message to SQS: " + message);
        sqsTemplate.send(to -> to.queue(QUEUE_NAME).payload(message));
    }

}
