package com.epam.ekc.storage.service;

import com.epam.ekc.storage.model.Identifiable;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;


    public void sendMessage(Identifiable identifiable) throws JsonProcessingException {
//        var jsonValue = objectMapper.writeValueAsString(identifiable);
        var messageKey = identifiable.getId();
//        var messageKey = identifiable.getClass().getSimpleName() + "|" + identifiable.getId();
        kafkaTemplate.send("storage.entity", messageKey, messageKey);
    }

}
