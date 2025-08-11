package com.henriquenascimento.awslocalstack.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "aws")
@Data
public class AwsProperties {

    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;

}
