package com.epam.ekc.storage.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.epam.ekc.storage.model.Author;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthorsConverter implements DynamoDBTypeConverter<String, List<Author>> {

    @SneakyThrows
    @Override
    public String convert(List<Author> authors) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(authors);

    }

    @Override
    @SneakyThrows
    public List<Author> unconvert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(s, new TypeReference<List<Author>>() {});
    }
}
