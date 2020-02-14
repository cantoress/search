package com.epam.ekc.storage.controller;

import com.epam.ekc.storage.model.Author;
import com.epam.ekc.storage.model.Book;
import com.epam.ekc.storage.model.Identifiable;
import com.epam.ekc.storage.service.AuthorDynamoService;
import com.epam.ekc.storage.service.BookDynamoService;
import com.epam.ekc.storage.service.SQSMessageService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/books")
public class BookController {

    private final BookDynamoService bookDynamoService;
    private final AuthorDynamoService authorDynamoService;
    private final SQSMessageService sqsMessageService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Book save(@RequestBody @Validated Book book) throws JsonProcessingException {
        book.setId(randomUUID().toString());
        savePassedAuthors(book.getAuthors());
        var savedBook = bookDynamoService.save(book);
        sqsMessageService.sendMessage(savedBook);
        return savedBook;
    }

    @PostMapping(value = "/batch", consumes = APPLICATION_JSON_VALUE)
    public List<Book> saveAll(@RequestBody List<Book> books) throws JsonProcessingException {
        books.forEach(a -> a.setId(randomUUID().toString()));
        books.forEach(a -> savePassedAuthors(a.getAuthors()));
        List<Book> savedBooks = bookDynamoService.saveAll(books);
        List<Identifiable> list = new ArrayList<>(savedBooks);
        sqsMessageService.sendBatchOfMessages(list);
        return savedBooks;
    }


    @GetMapping("/{id}")
    public Book findOne(@PathVariable String id) {
        log.debug("Request to get Book : {}", id);
        return bookDynamoService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        log.debug("Request to delete Book : {}", id);
        bookDynamoService.deleteById(id);
    }

    private void savePassedAuthors(List<Author> authors) {
        authors.stream()
                .filter(author -> author.getId() == null)
                .forEach(author -> author.setId(randomUUID().toString()));
        authorDynamoService.saveAll(authors);
    }
}