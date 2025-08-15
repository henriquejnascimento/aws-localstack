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

### AWS CLI
1. Command to list current AWS CLI settings  
   ```aws configure list```

2. Command to change the AWS CLI profile  
```aws configure --profile <myprofile>```

Alternative: Using the configuration file directly, manually edit the files:

Credentials file: ~/.aws/credentials  
Example of how to add a profile to the file ~/.aws/credentials:
```
[myprofile]
aws_access_key_id = YOUR_ACCESS_KEY
aws_secret_access_key = YOUR_SECRET_KEY
aws_session_token = YOUR_SESSION_TOKEN (optional, for temporary authentication)
```

Configuration file: ~/.aws/config    
Example of how to add a profile to the file ~/.aws/config:
```
[profile myprofile]
region = us-east-1
output = json
```

3. Changing the default profile / Changing the AWS_PROFILE environment variable  
   ```export AWS_PROFILE=<myprofile>```

### AWS S3
Considering a bucket name: test-bucket-1

1. List buckets  
   ```aws --endpoint-url=http://localhost:4566 s3 ls```

2. Make bucket  
   ```aws --endpoint-url=http://localhost:4566 s3 mb s3://test-bucket-1```

3. Remove bucket  
   ```aws --endpoint-url=http://localhost:4566 s3 rb s3://test-bucket-1 --force```

4. Create directory in a bucket  
   ```
   touch .empty-file
   
   aws --endpoint-url=http://localhost:4566 s3 cp .empty-file s3://test-bucket-1/test-directory-1/.empty-file
   or
   aws --endpoint-url=http://localhost:4566 s3 cp /dev/null s3://test-bucket-1/test-directory-1/
    ```

5. List bucket contents  
   ```aws --endpoint-url=http://localhost:4566 s3 ls s3://test-bucket-1 --recursive```

6. Upload to a bucket  
   ```aws --endpoint-url=http://localhost:4566 s3 cp /home/user/Downloads/test-file-1.txt s3://test-bucket-1/test-directory-1/test-file-1.txt```

7. Move file  
   ```aws --endpoint-url=http://localhost:4566 s3 mv s3://test-bucket-1/test-directory-1/test-file-1.txt s3://test-bucket-1/test-directory-2/test-file-1.txt```

8. Remove file  
   ```aws --endpoint-url=http://localhost:4566 s3 rm s3://test-bucket-1/test-directory-2/test-file-1.txt```


NOTE: You can also specify the region in the commands above, using as an example:  
```aws --endpoint-url=http://localhost:4566 --region us-east-1 s3 ls```

### AWS SQS
Considering the queue name: test-queue.fifo

1. List Queues
```aws --endpoint-url=http://localhost:4566 sqs list-queues```

2. Create a FIFO Queue
   ```aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name test-queue.fifo --attributes FifoQueue=true,ContentBasedDeduplication=true```

3. Send Message to the FIFO Queue
   ```aws --endpoint-url=http://localhost:4566 sqs send-message --queue-url http://localhost:4566/000000000000/test-queue.fifo --message-body "Test mensagem 1" --message-group-id "1"```
4. Listen to a Queue
   ```
   while true; do
   result=$(aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/test-queue.fifo --max-number-of-messages 1 --wait-time-seconds 2)
   if [ ! -z "$result" ]; then
   echo "Message received: $result"
   else
   echo "No messages received."
   fi
   done
   ```
5. View Queue Messages
   ```aws --endpoint-url=http://localhost:4566 sqs receive-message --queue-url http://localhost:4566/000000000000/test-queue.fifo --max-number-of-messages 1```
6. Purge a Queue
   ```aws --endpoint-url=http://localhost:4566 sqs purge-queue --queue-url http://localhost:4566/000000000000/test-queue.fifo```
7. Delete a Queue
   ```aws --endpoint-url=http://localhost:4566 sqs delete-queue --queue-url http://localhost:4566/000000000000/test-queue.fifo```


### AWS SNS
Considering an topic name: topic-test

1. Create the SNS topic in LocalStack:  
   ```aws --endpoint-url=http://localhost:4566 sns create-topic --name topic-test```

2. Create the SQS queue in LocalStack:  
   ```aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name test-queue```

3. Get SNS Topic ARN:  
   ```aws --endpoint-url=http://localhost:4566 sns list-topics```

4. Get the SQS queue ARN:  
   ```aws --endpoint-url=http://localhost:4566 sqs get-queue-attributes --attribute-name QueueArn --queue-url http://localhost:4566/000000000000/test-queue```

5. Associate SNS with SQS:  
   ```aws --endpoint-url=http://localhost:4566 sns subscribe --topic-arn arn:aws:sns:us-east-1:000000000000:**topic-test** --protocol sqs --notification-endpoint arn:aws:sqs:us-east-1:000000000000:test-queue```
