package com.epam.ekc.storage.model;

import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.epam.ekc.storage.service.AuthorsConverter;
import com.epam.ekc.storage.service.InstantConverter;
import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@DynamoDBTable(tableName = "Book")
public class Book implements Serializable, Identifiable {

    private static final long serialVersionUID = 1L;

    private String id;
    @NotNull
    @NotEmpty
    private String customerId;
    private String type;
    private String genre;
    private String title;
    private String language;
    private Instant publicationDate;
    private List<Author> authors = new ArrayList<>();

    @DynamoDBHashKey
    public String getId() {
        return id;
    }

    @DynamoDBAttribute
    public String getCustomerId() {
        return customerId;
    }

    @DynamoDBAttribute
    public String getType() {
        return type;
    }

    @DynamoDBAttribute
    public String getGenre() {
        return genre;
    }

    @DynamoDBAttribute
    public String getTitle() {
        return title;
    }

    @DynamoDBAttribute
    public String getLanguage() {
        return language;
    }

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = InstantConverter.class)
    public Instant getPublicationDate() {
        return publicationDate;
    }

    @DynamoDBAttribute
    @DynamoDBTypeConverted(converter = AuthorsConverter.class)
    public List<Author> getAuthors() {
        return authors;
    }
}
