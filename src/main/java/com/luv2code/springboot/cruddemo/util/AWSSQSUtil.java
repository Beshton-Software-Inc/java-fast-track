package com.luv2code.springboot.cruddemo.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class AWSSQSUtil {

    @Value( "${aws.access.key}" )
    private String AWS_ACCESS_KEY;
    @Value("${aws.secret.key}")
    private String AWS_SECRET_KEY;

    @Value("${aws.sqs.queue}")
    private String AWS_SQS_QUEUE;
    @Value("${aws.sqs.arn}")
    private String AWS_SQS_QUEUE_ARN;
    @Value("${aws.sqs.queue.url}")
    private String AWS_SQS_QUEUE_URL;
    @Value("${aws.s3.bucket}")
    private String AWS_S3_BUCKET_NAME;

    private static final String LOG_FILE_KEY = "logfile.txt";


    private AWSCredentials awsCredentials() {
        AWSCredentials credentials = new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
        return credentials;
    }

    private AmazonSQS sqsClientBuilder() {
        AmazonSQS sqs = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials())).withRegion(Regions.US_EAST_2)
                .build();
        return sqs;
    }

    private AmazonS3 s3ClientBuilder() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.US_EAST_2)
                .build();
    }


    public String produceMessageToSQS(String message) {
        AmazonSQS sqsClient = sqsClientBuilder();
        SendMessageRequest request = new SendMessageRequest().withQueueUrl(AWS_SQS_QUEUE_URL).withMessageBody(message)
                .withDelaySeconds(1);

        return sqsClient.sendMessage(request).getMessageId();
    }

    public List<Message> consumeMessageFromSQS() {
        AmazonSQS sqsClient = sqsClientBuilder();

        ReceiveMessageRequest request = new ReceiveMessageRequest(AWS_SQS_QUEUE_URL).withWaitTimeSeconds(1)
                .withMaxNumberOfMessages(10);

        List<Message> sqsMessages = sqsClient.receiveMessage(request).getMessages();
        for (Message message : sqsMessages) {
            //run process for message
            System.out.println(message.getBody());
            processMessage(message);
            //dequeue message after using it
            //also perfect step so check if message was successfully processed
            dequeuMessageFromSQS(message);
        }
        return sqsMessages;
    }

    private void processMessage(Message message) {
        AmazonS3 s3Client = s3ClientBuilder();
        String messageBody = message.getBody();
        String objectKey = "message_" + message.getMessageId() + ".json";

        /*
        // Store the message in S3
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("application/json");
        metadata.setContentLength(messageBody.length());
        InputStream messageBodyStream = new ByteArrayInputStream(messageBody.getBytes(StandardCharsets.UTF_8));
        PutObjectRequest putObjectRequest = new PutObjectRequest(AWS_S3_BUCKET_NAME, objectKey, messageBodyStream, metadata);
        s3Client.putObject(putObjectRequest);
       */


        // Append message to the log file
        appendToLogFile(messageBody);
        System.out.println("Append the message successfully! " + objectKey);
    }

    private void appendToLogFile(String messageContent) {
        AmazonS3 s3Client = s3ClientBuilder();
        StringBuilder logContent = new StringBuilder();

        // Check if log file exists
        try {
            S3Object s3Object = s3Client.getObject(new GetObjectRequest(AWS_S3_BUCKET_NAME, LOG_FILE_KEY));
            BufferedReader reader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
            String line;
            while ((line = reader.readLine()) != null) {
                logContent.append(line).append("\n");
            }
            reader.close();
        } catch (AmazonS3Exception e) {
            // If the object doesn't exist, we'll create a new one
            if (e.getStatusCode() != 404) {
                throw e;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Append the new message content
        logContent.append(messageContent).append("\n");

        // Upload the updated log file back to S3
        InputStream logContentStream = new ByteArrayInputStream(logContent.toString().getBytes(StandardCharsets.UTF_8));
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType("text/plain");
        metadata.setContentLength(logContent.length());
        s3Client.putObject(new PutObjectRequest(AWS_S3_BUCKET_NAME, LOG_FILE_KEY, logContentStream, metadata));
    }

    public void dequeuMessageFromSQS(Message message) {
        AmazonSQS sqsClient = sqsClientBuilder();

        sqsClient.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(AWS_SQS_QUEUE_URL)
                .withReceiptHandle(message.getReceiptHandle()));

        System.out.println("Messge deque from the sqs: " + message);
    }
}
