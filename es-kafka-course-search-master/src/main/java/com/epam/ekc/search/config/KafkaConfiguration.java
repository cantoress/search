package com.epam.ekc.search.config;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.LinkedHashMap;
import java.util.UUID;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class KafkaConfiguration {

    public final KafkaProperties kafkaProperties;

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, String> singleConsumerFactory() {
//        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
//        var properties = new LinkedHashMap<String, Object>();
//        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
//        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "search.one-by-one-consumer");
//        properties.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
//        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
//
//        var consumerFactory = new DefaultKafkaConsumerFactory<String, String>(properties);
//        factory.setConsumerFactory(consumerFactory);
//        return factory;
//    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> singleConsumerFactory(){
        var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
        var settings = new LinkedHashMap<String, Object>();
        settings.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaProperties.getBootstrapServers());
        settings.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        settings.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        settings.put(ConsumerConfig.GROUP_ID_CONFIG, "search.batch");
        settings.put(ConsumerConfig.CLIENT_ID_CONFIG, UUID.randomUUID().toString());
        settings.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        settings.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 5000);

        var consumerFactory = new DefaultKafkaConsumerFactory<String, String>(settings);
        factory.setConsumerFactory(consumerFactory);
        factory.setBatchListener(true);
        return  factory;
    }

}
