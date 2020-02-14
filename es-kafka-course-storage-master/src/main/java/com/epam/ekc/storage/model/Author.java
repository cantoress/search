package com.epam.ekc.storage.model;


import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.epam.ekc.storage.service.AuthorsConverter;
import com.epam.ekc.storage.service.BookConverter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@DynamoDBTable(tableName = "Author")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Author implements Serializable, Identifiable {

    private static final long serialVersionUID = 1L;

    private String id;
    private String firstName;
    private String lastName;
    @JsonIgnoreProperties("authors")
    private Book book;

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    @DynamoDBAttribute
    public String getFirstName() {
        return firstName;
    }

    @DynamoDBAttribute
    public String getLastName() {
        return lastName;
    }

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = BookConverter.class)
    public Book getBook() {
        return book;
    }
}
