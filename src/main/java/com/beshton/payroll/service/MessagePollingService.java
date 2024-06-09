package com.beshton.payroll.service;

import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class MessagePollingService {

    private static final Logger logger = LoggerFactory.getLogger(MessagePollingService.class);
    private final SqsService sqsService;
    private volatile boolean running = true;

    @Autowired
    public MessagePollingService(SqsService sqsService) {
        this.sqsService = sqsService;
    }

    @Scheduled(fixedDelay = 5000, initialDelay = 1000)
    public void pollMessages() {
        if (!running) {
            return;
        }
        try {
            sqsService.consumeMessagesFromSQS();
        } catch (Exception e) {
            logger.error("Error while polling messages", e);
        }
    }

    public void startPolling() {
        this.running = true;
    }

    public void stopPolling() {
        this.running = false;
    }

    @PreDestroy
    public void onDestroy() {
        stopPolling(); // Signal the polling to stop
        logger.info("Polling stopped.");
    }
}
