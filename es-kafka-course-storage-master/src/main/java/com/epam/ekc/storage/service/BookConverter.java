package com.epam.ekc.storage.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.epam.ekc.storage.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
public class BookConverter implements DynamoDBTypeConverter<String, Book> {

    @SneakyThrows
    @Override
    public String convert(Book book) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(book);

    }

    @Override
    @SneakyThrows
    public Book unconvert(String s) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(s, Book.class);
    }
}
