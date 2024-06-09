package com.luv2code.springboot.cruddemo;

import com.luv2code.springboot.cruddemo.util.AWSSQSUtil;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
public class CruddemoApplication {

	private final AWSSQSUtil awsSqsUtil;

	public CruddemoApplication(AWSSQSUtil awsSqsUtil) {
		this.awsSqsUtil = awsSqsUtil;
	}
	public static void main(String[] args) {
		SpringApplication.run(CruddemoApplication.class, args);
	}

	@Scheduled(fixedRate = 1000)  // Poll every 1 seconds
	public void processMessages() {
		awsSqsUtil.consumeMessageFromSQS();
	}

}
