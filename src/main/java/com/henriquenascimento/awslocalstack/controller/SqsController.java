package com.henriquenascimento.awslocalstack.controller;

import com.henriquenascimento.awslocalstack.service.SqsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sqs")
public class SqsController {

    @Autowired
    private SqsService service;

    @PostMapping("/send")
    public ResponseEntity<String> createProduct(@RequestBody final String message) {
        service.sendMessage(message);
        return ResponseEntity.ok("Message sent successfully: " + message);
    }
}
