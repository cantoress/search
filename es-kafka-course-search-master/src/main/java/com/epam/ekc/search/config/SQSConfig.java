package com.epam.ekc.search.config;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SQSConfig {

    @Bean
    public AmazonSQS sqsClient() {
        return AmazonSQSClientBuilder.defaultClient();
    }
}