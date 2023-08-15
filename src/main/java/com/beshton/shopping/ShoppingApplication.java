package com.beshton.shopping;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShoppingApplication {
	private static final Logger logger = LogManager.getLogger(ShoppingApplication.class.getName());

	public static void main(String[] args) {
		SpringApplication.run(ShoppingApplication.class, args);

		logger.info("Application started.");
	}
}
