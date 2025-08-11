# aws-localstack
Spring Boot Demo using AWS SDK 2.x with LocalStack

### Tech Stack
 - Spring Boot 3.x
 - AWS SDK 2.x
 - AWS SQS
 - AWS SNS
 - AWS S3
 - AWS DynamoDB (not yet available)

## Requirements (tested versions)
 - Java 21
 - LocalStack Desktop Community v1.0.7
 - Docker Desktop v4.42.1
 - Docker v28.3.3
 - Docker Compose v2.36.2
 - Docker image localstack v4.7.0

### SNS
1. Create the SNS topic in LocalStack:  
   aws --endpoint-url=http://localhost:4566 sns create-topic --name **topic-test**

2. Create the SQS queue in LocalStack:  
   aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name **test-queue**

3. Get SNS Topic ARN:  
   aws --endpoint-url=http://localhost:4566 sns list-topics

4. Get the SQS queue ARN:  
   aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --attribute-name QueueArn --queue-url **http://localhost:4566/000000000000/test-queue**

5. Associate SNS with SQS:  
   aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:**topic-test** --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:**test-queue**
