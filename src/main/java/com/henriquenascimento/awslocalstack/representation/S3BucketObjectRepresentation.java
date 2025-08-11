package com.henriquenascimento.awslocalstack.representation;

import lombok.Data;

@Data
public class S3BucketObjectRepresentation {

    private String objectName;
    private String text;
}
