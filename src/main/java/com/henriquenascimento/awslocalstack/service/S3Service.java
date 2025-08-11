package com.henriquenascimento.awslocalstack.service;

import com.henriquenascimento.awslocalstack.representation.S3BucketObjectRepresentation;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    private final S3Client s3Client;

    public void uploadFile(final String bucketName, final MultipartFile file) throws IOException {
        Path tempFile = Files.createTempFile("upload-", file.getOriginalFilename());
        file.transferTo(tempFile);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(file.getOriginalFilename())
                .contentType("text/plain")
                .build();

        PutObjectResponse response = s3Client.putObject(putObjectRequest, tempFile);
        log.info("File sent successfully! " + response);

        Files.delete(tempFile);
    }

    public void createBucket(String bucketName) {
        CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.createBucket(createBucketRequest);
            log.info("Bucket created: {}", createBucketRequest.bucket());
        } catch (S3Exception e) {
            log.error("Error creating bucket: {}", e.awsErrorDetails().errorMessage());
        }
    }

    public List<Bucket> listBuckets() {
        return s3Client.listBuckets().buckets();
    }

    public void deleteBucket(String bucketName) {
        DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                .bucket(bucketName)
                .build();

        try {
            s3Client.deleteBucket(deleteBucketRequest);
            log.info("Bucket deleted: {}", deleteBucketRequest.bucket());
        } catch (S3Exception e) {
            log.error("Error deleting bucket: {}", e.awsErrorDetails().errorMessage());
        }
    }

    @SneakyThrows
    public void putObject(String bucketName, S3BucketObjectRepresentation s3BucketObjectRepresentation) {
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3BucketObjectRepresentation.getObjectName())
                .build();

        try {
            Path file = Files.write(
                    Paths.get(".", s3BucketObjectRepresentation.getObjectName()),
                    s3BucketObjectRepresentation.getText().getBytes());
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
            log.info("Object created: {}", putObjectRequest.key());
        } catch (S3Exception e) {
            log.error("Error uploading object: {}", e.awsErrorDetails().errorMessage());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    public List<S3Object> listObjects(String bucketName) {
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(bucketName)
                .build();

        try {
            ListObjectsResponse objectListing = s3Client.listObjects(listObjects);
            return objectListing.contents();
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }

        return Collections.emptyList();
    }

    // TODO review
    public void getObject(String bucketName, String objectName) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        try {
            ResponseInputStream<GetObjectResponse> responseInputStream = s3Client.getObject(getObjectRequest);
            FileUtils.copyInputStreamToFile(responseInputStream, new File("." + File.separator + objectName));
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    public void deleteObject(String bucketName, String objectName) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectName)
                .build();

        try {
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Object deleted: {}", deleteObjectRequest.key());
        } catch (S3Exception e) {
            log.error("Error deleting object: {}", e.awsErrorDetails().errorMessage());
        }
    }

    public void deleteObjects(String bucketName, List<String> objects) {
        ArrayList<ObjectIdentifier> objectIdentifiers = new ArrayList<>();

        objects.forEach(object -> {
            ObjectIdentifier objectIdentifier = ObjectIdentifier.builder()
                    .key(object)
                    .build();

            objectIdentifiers.add(objectIdentifier);
        });

        Delete delete = Delete.builder()
                .objects(objectIdentifiers)
                .build();

        DeleteObjectsRequest deleteObjectsRequest = DeleteObjectsRequest.builder()
                .bucket(bucketName)
                .delete(delete)
                .build();

        try {
            s3Client.deleteObjects(deleteObjectsRequest);
            log.info("Objects deleted: {}", objectIdentifiers.stream()
                    .map(ObjectIdentifier::key)
                    .collect(Collectors.joining()));
        } catch (S3Exception e) {
            log.error("Error deleting objects: {}", e.awsErrorDetails().errorMessage());
        }
    }

    public void moveObject(String sourceBucketName, String objectName, String targetBucketName) {
        CopyObjectRequest copyObjectRequest = CopyObjectRequest.builder()
                .sourceBucket(sourceBucketName)
                .sourceKey(objectName)
                .destinationBucket(targetBucketName)
                .destinationKey(objectName)
                .build();

        try {
            CopyObjectResponse copyObjectResponse = s3Client.copyObject(copyObjectRequest);
            if (copyObjectResponse != null) {
                deleteObject(sourceBucketName, objectName);
                log.info("Object moved and deleted: {}", copyObjectRequest.sourceKey());
            }
        } catch (S3Exception e) {
            log.error(e.awsErrorDetails().errorMessage());
        }
    }

    public ResponseEntity<InputStreamResource> downloadFile(String bucketName, String objectKey) {
        ResponseInputStream responseInputStream = s3Client.getObject(GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM) // Generic type for binary file
                .header("Content-Disposition", "attachment; filename=\"" + objectKey + "\"")
                .body(new InputStreamResource(responseInputStream));
    }

    public String testConnection() {
        try {
            ListBucketsRequest listBucketsRequest = ListBucketsRequest.builder().build();
            ListBucketsResponse response = s3Client.listBuckets(listBucketsRequest);

            return "Connection successful! Buckets: " + response.buckets().size();
        } catch (Exception e) {
            return "Error connecting to LocalStack: " + e.getMessage();
        }
    }

    public void createDirectory(String bucketName, String directoryName) {
        String objectKey = directoryName.endsWith("/") ? directoryName : directoryName + "/";

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.putObject(putObjectRequest, Paths.get("/dev/null"));
    }
}