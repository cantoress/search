package com.epam.ekc.storage.service;

import com.epam.ekc.storage.model.Identifiable;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendMessage(Identifiable identifiable) {
        var key = identifiable.getId();
        var partition = Math.abs(key.hashCode() % 4);
        var value = identifiable.getClass().getSimpleName();
        kafkaTemplate.send("storage.entity", partition, key, value);
    }
}
