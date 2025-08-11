package com.henriquenascimento.awslocalstack.controller;

import com.henriquenascimento.awslocalstack.service.SnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/sns")
public class SnsController {

    @Autowired
    private SnsService snsService;

    @PostMapping("/send")
    public String sendMessage(@RequestParam final String message) {
        return snsService.sendMessage(message);
    }

    @PostMapping("/subscribe")
    public String subscribeEmail(@RequestParam String email) {
        snsService.subscribeEmail(email);
        return "Subscription successful!";
    }

    @GetMapping("/subscriptions/{topicName}")
    public List<String> listSubscriptions(@PathVariable final String topicName) {
        return snsService.listSubscriptions(topicName);
    }

    @PostMapping("/associate/{topicName}/{queueName}")
    public String associateTopicWithQueue(@PathVariable String topicName, @PathVariable String queueName) {
        snsService.associateSnsWithSqs(topicName, queueName);
        return "SNS topic " + topicName + " is now associated with SQS queue " + queueName;
    }

    @PostMapping("/disassociate/{topicName}/{queueName}")
    public String disassociateTopicFromQueue(@PathVariable String topicName, @PathVariable String queueName) {
        snsService.disassociateSnsFromSqs(topicName, queueName);
        return "SNS topic " + topicName + " is now disassociated from SQS queue " + queueName;
    }
}
