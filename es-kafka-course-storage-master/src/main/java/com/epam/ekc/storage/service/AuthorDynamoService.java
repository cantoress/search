package com.epam.ekc.storage.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.epam.ekc.storage.model.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorDynamoService {

    private static final String TABLE_NAME = "Author";
    private final DynamoDB dynamoDB;
    private final DynamoDBMapper dynamoDBMapper;

    @PostConstruct
    public void init() {
        try {
            CreateTableRequest req = dynamoDBMapper.generateCreateTableRequest(Author.class);
            req.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
            dynamoDB.createTable(req);

        } catch (ResourceInUseException e) {
            System.out.println("The table " + TABLE_NAME + " already exists");
        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public Author save(Author author) {
        dynamoDBMapper.save(author);
        return author;
    }

    public Author findById(String id) {
        return dynamoDBMapper.load(Author.class, id);
    }

    public List<Author> saveAll(List<Author> authors) {
        for (Author author : authors) {
            save(author);
        }
        return authors;
    }

    public void delete(Author author) {
        dynamoDBMapper.delete(author);
    }

    public void deleteById(String id) {
        delete(findById(id));
    }
}
