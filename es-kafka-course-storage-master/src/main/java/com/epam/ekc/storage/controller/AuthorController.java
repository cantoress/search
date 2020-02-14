package com.epam.ekc.storage.controller;

import com.epam.ekc.storage.model.Author;
import com.epam.ekc.storage.model.Identifiable;
import com.epam.ekc.storage.service.AuthorDynamoService;
import com.epam.ekc.storage.service.SQSMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static java.util.UUID.randomUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorDynamoService authorDynamoService;
    private final SQSMessageService sqsMessageService;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Author save(@RequestBody Author author) {
        if (author.getId().isBlank()) {
            author.setId(randomUUID().toString());
            return authorDynamoService.save(author);
        } else {
            Author savedAuthor = authorDynamoService.save(author);
            System.out.println(savedAuthor);
            sqsMessageService.sendMessage(savedAuthor.getBook());
            return savedAuthor;
        }
    }

    @PostMapping(value = "/batch", consumes = APPLICATION_JSON_VALUE)
    public List<Author> saveAll(@RequestBody List<Author> author) {
        author.forEach(a -> a.setId(randomUUID().toString()));
        List<Author> authors = authorDynamoService.saveAll(author);
        List<Identifiable> list = new ArrayList<>(authors);
        sqsMessageService.sendBatchOfMessages(list);
        return authors;
    }

    @GetMapping("/{id}")
    public Author findOne(@PathVariable String id) {
        log.debug("Request to get Authors : {}", id);
        return authorDynamoService.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        log.debug("Request to delete Authors : {}", id);
        authorDynamoService.deleteById(id);
    }
}
