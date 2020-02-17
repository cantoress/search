package com.epam.ekc.storage.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.ResourceInUseException;
import com.epam.ekc.storage.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookDynamoService {

    private static final String TABLE_NAME = "Book";
    private final DynamoDB dynamoDB;
    private final DynamoDBMapper dynamoDBMapper;

    @PostConstruct
    public void init() {
        try {
            CreateTableRequest req = dynamoDBMapper.generateCreateTableRequest(Book.class);
            req.setProvisionedThroughput(new ProvisionedThroughput(5L, 5L));
            dynamoDB.createTable(req);

        } catch (ResourceInUseException e) {
            System.out.println("The table " + TABLE_NAME + " already exists");
        } catch (Exception e) {
            System.err.println("Unable to create table: ");
            System.err.println(e.getMessage());
        }
    }

    public Book save(Book book) {
        dynamoDBMapper.save(book);
        return book;
    }

    public Book findById(String id) {
        return dynamoDBMapper.load(Book.class, id);
    }

    public List<Book> saveAll(List<Book> books) {
        dynamoDBMapper.batchSave(books);
        return books;
    }

    public void delete(Book book) {
        dynamoDBMapper.delete(book);
    }

    public void deleteById(String id) {
        delete(findById(id));
    }
}
