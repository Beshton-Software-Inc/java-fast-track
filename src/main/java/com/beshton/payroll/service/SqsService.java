package com.beshton.payroll.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@Service
public class SqsService {

    private final AmazonSQS sqsClient;
    private final String queueUrl;
    private final AwsS3Service awsS3Service;
    static final Logger logger = LoggerFactory.getLogger(SqsService.class);

    @Autowired
    public SqsService(AmazonSQS sqsClient, @Value("${aws.sqs.queue.url}") String queueUrl, AwsS3Service awsS3Service) {
        this.sqsClient = sqsClient;
        this.queueUrl = queueUrl;
        this.awsS3Service = awsS3Service;
    }

    public void produceMessageToSQS(String message) {
        SendMessageRequest request = new SendMessageRequest()
                .withQueueUrl(this.queueUrl)
                .withMessageBody(message)
                .withDelaySeconds(5);
        this.sqsClient.sendMessage(request);
    }

    public void consumeMessagesFromSQS() {
        ReceiveMessageRequest request = new ReceiveMessageRequest(this.queueUrl)
                .withWaitTimeSeconds(20)  // Use long polling
                .withMaxNumberOfMessages(10);
        List<Message> messages = this.sqsClient.receiveMessage(request).getMessages();
        messages.forEach(this::processAndDequeueMessage);
    }

    private void processAndDequeueMessage(Message message) {
        try {
            awsS3Service.appendToLog("readme.txt", "Processed message: " + message.getBody());
            dequeueMessageFromSQS(message);
        } catch (Exception e) {
            logger.error("Failed to process message: {}", message.getBody(), e);
        }
    }

    private void dequeueMessageFromSQS(Message message) {
        this.sqsClient.deleteMessage(new DeleteMessageRequest()
                .withQueueUrl(this.queueUrl)
                .withReceiptHandle(message.getReceiptHandle()));
    }
}
