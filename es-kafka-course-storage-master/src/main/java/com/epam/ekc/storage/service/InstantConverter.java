package com.epam.ekc.storage.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Service
public class InstantConverter implements DynamoDBTypeConverter<String, Instant> {
    @Override
    public String convert(Instant instant) {
        return instant.toString();
    }

    @Override
    public Instant unconvert(String s) {
        return LocalDateTime.parse(s, DateTimeFormatter.ISO_DATE_TIME)
                .atZone(ZoneId.of("America/Toronto"))
                .toInstant();
    }
}
