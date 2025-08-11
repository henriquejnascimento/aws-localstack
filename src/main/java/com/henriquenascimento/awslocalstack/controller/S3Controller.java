package com.henriquenascimento.awslocalstack.controller;

import com.henriquenascimento.awslocalstack.representation.S3BucketObjectRepresentation;
import com.henriquenascimento.awslocalstack.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.io.File;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/files")
@RequiredArgsConstructor
public class S3Controller {

    private final S3Service s3Service;

    @GetMapping("/test-connection")
    public String testConnection() {
        return s3Service.testConnection();
    }

    @PostMapping("/{bucketName}")
    public void createBucket(@PathVariable String bucketName) {
        s3Service.createBucket(bucketName);
    }

    @GetMapping
    public List<String> listBuckets() {
        return s3Service.listBuckets()
                .stream()
                .map(Bucket::name)
                .collect(toList());
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam String bucketName, @RequestParam("file") MultipartFile file) {
        try {
            s3Service.uploadFile(bucketName, file);
            return ResponseEntity.ok("File sent successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending file: " + e.getMessage());
        }
    }

    @DeleteMapping("/{bucketName}")
    public void deleteBucket(@PathVariable String bucketName) {
        s3Service.deleteBucket(bucketName); //TODO: Error deleting bucket: The bucket you tried to delete is not empty
    }

    @PostMapping("/{bucketName}/objects")
    public void putObject(@PathVariable String bucketName, @RequestBody S3BucketObjectRepresentation s3BucketObjectRepresentation) {
        s3Service.putObject(bucketName, s3BucketObjectRepresentation);
    }

    @GetMapping("/download/{bucketName}/{objectKey}")
    public ResponseEntity<?> downloadFile(
            @PathVariable String bucketName,
            @PathVariable String objectKey) {
        try {
            return s3Service.downloadFile(bucketName, objectKey);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error downloading file: " + e.getMessage());
        }
    }

    @GetMapping("/{bucketName}/objects/{objectName}")
    public File getObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3Service.getObject(bucketName, objectName);
        return new File("./" + objectName);
    }

    @PatchMapping("/{sourceBucketName}/objects/{objectName}/{targetBucketName}")
    public void moveObject(@PathVariable String sourceBucketName, @PathVariable String objectName, @PathVariable String targetBucketName) {
        s3Service.moveObject(sourceBucketName, objectName, targetBucketName);
    }

    @GetMapping("/{bucketName}/objects")
    public List<String> listObjects(@PathVariable String bucketName) {
        return s3Service.listObjects(bucketName)
                .stream()
                .map(S3Object::key)
                .collect(toList());
    }

    @DeleteMapping("/{bucketName}/objects/{objectName}")
    public void deleteObject(@PathVariable String bucketName, @PathVariable String objectName) {
        s3Service.deleteObject(bucketName, objectName);
    }

    @DeleteMapping("/{bucketName}/objects")
    public void deleteObjects(@PathVariable String bucketName, @RequestBody List<String> objects) {
        s3Service.deleteObjects(bucketName, objects);
    }

    @PostMapping("/{bucketName}/create-directory")
    public ResponseEntity<String> createDirectory(@PathVariable String bucketName, @RequestBody String directoryName) {
        try {
            s3Service.createDirectory(bucketName, directoryName);
            return ResponseEntity.ok("Directory created successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating directory: " + e.getMessage());
        }
    }
}
