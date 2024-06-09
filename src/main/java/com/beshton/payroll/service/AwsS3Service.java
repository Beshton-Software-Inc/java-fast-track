package com.beshton.payroll.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class AwsS3Service {

    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);
    private final AmazonS3 s3client;
    private final String bucketName;

    @Autowired
    public AwsS3Service(AmazonS3 s3client, @Value("${aws.s3.bucket.name}") String bucketName) {
        this.s3client = s3client;
        this.bucketName = bucketName;
    }

    public void appendToLog(String logFilePath, String logEntry) throws IOException {
        S3Object s3Object = s3client.getObject(new GetObjectRequest(bucketName, logFilePath));
        Scanner s = new Scanner(s3Object.getObjectContent()).useDelimiter("\\A");
        String existingContent = s.hasNext() ? s.next() : "";
        String newContent = existingContent + "\n" + logEntry;

        byte[] newContentBytes = newContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(newContentBytes);
        ObjectMetadata meta = new ObjectMetadata();
        meta.setContentLength(newContentBytes.length);

        s3client.putObject(bucketName, logFilePath, inputStream, meta);
        logger.info("Updated log file in bucket {}: {}", bucketName, logFilePath);
    }
}
