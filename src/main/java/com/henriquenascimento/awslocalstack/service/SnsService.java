package com.henriquenascimento.awslocalstack.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SnsService {

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SqsClient sqsClient;

    private final String topicArn = "arn:aws:sns:us-east-1:000000000000:topic-test"; // ARN do tópico SNS criado no LocalStack

    public String sendMessage(String message) {
        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        PublishResponse publishResponse = snsClient.publish(publishRequest);

        return publishResponse.messageId();
    }

    public void subscribeEmail(String emailAddress) {
        SubscribeRequest subscribeRequest = SubscribeRequest.builder()
                .protocol("email")
                .endpoint(emailAddress)
                .topicArn(topicArn)
                .build();

        SubscribeResponse subscribeResponse = snsClient.subscribe(subscribeRequest);
        System.out.println("Subscription ARN: " + subscribeResponse.subscriptionArn());
    }

    public List<String> listSubscriptions(String topicName) {
        String topicArn = "arn:aws:sns:us-east-1:000000000000:" + topicName;
        ListSubscriptionsByTopicRequest request = ListSubscriptionsByTopicRequest.builder()
                .topicArn(topicArn)
                .build();

        ListSubscriptionsByTopicResponse response = snsClient.listSubscriptionsByTopic(request);

        return response.subscriptions().stream()
                .map(Subscription::endpoint)
                .collect(Collectors.toList());
    }

    public void associateSnsWithSqs(String topicName, String queueName) {
        String topicArn = getTopicArn(topicName);
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        String queueArn = "arn:aws:sqs:us-east-1:000000000000:" + queueName;

        snsClient.subscribe(SubscribeRequest.builder()
                .protocol("sqs")
                .endpoint(queueArn)
                .topicArn(topicArn)
                .build());
    }

    public void disassociateSnsFromSqs(String topicName, String queueName) {
        String topicArn = getTopicArn(topicName);
        String queueUrl = sqsClient.getQueueUrl(GetQueueUrlRequest.builder().queueName(queueName).build()).queueUrl();
        String queueArn = "arn:aws:sqs:us-east-1:000000000000:" + queueName;

        ListSubscriptionsByTopicRequest listSubscriptionsRequest = ListSubscriptionsByTopicRequest.builder()
                .topicArn(topicArn)
                .build();

        ListSubscriptionsByTopicResponse subscriptionsResponse = snsClient.listSubscriptionsByTopic(listSubscriptionsRequest);

        subscriptionsResponse.subscriptions().stream()
                .filter(subscription -> subscription.endpoint().equals(queueArn))
                .forEach(subscription -> snsClient.unsubscribe(UnsubscribeRequest.builder().subscriptionArn(subscription.subscriptionArn()).build()));
    }

    private String getTopicArn(String topicName) {
        ListTopicsRequest listTopicsRequest = ListTopicsRequest.builder().build();
        ListTopicsResponse listTopicsResponse = snsClient.listTopics(listTopicsRequest);

        return listTopicsResponse.topics().stream()
                .filter(topic -> topic.topicArn().endsWith(topicName))
                .map(Topic::topicArn)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Topic not found: " + topicName));
    }
}
