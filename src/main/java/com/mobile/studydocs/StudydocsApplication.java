package com.mobile.studydocs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudydocsApplication {
    private static final Logger logger = LoggerFactory.getLogger(StudydocsApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(StudydocsApplication.class, args);
        logger.info("✅ StudydocsApplication started successfully!");
    }
}
