package com.epam.ekc.storage.controller;

import com.epam.ekc.storage.model.Author;
import com.epam.ekc.storage.model.Book;
import com.epam.ekc.storage.repository.AuthorRepository;
import com.epam.ekc.storage.repository.BookRepository;
import com.epam.ekc.storage.service.MessageProducer;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static java.util.UUID.randomUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/books")
public class BookController {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final MessageProducer messageProducer;

    @PostMapping(consumes = APPLICATION_JSON_VALUE)
    public Book save(@RequestBody @Validated Book book) throws JsonProcessingException {
        book.setId(randomUUID().toString());
        System.out.println(book.getAuthors());
        savePassedAuthors(book.getAuthors());
        var savedBook = bookRepository.save(book);
        messageProducer.sendMessage(savedBook);
        return savedBook;
    }

    @PostMapping(value = "/batch", consumes = APPLICATION_JSON_VALUE)
    public List<Book> saveAll(@RequestBody List<Book> books) throws JsonProcessingException {
        books.forEach(a -> a.setId(randomUUID().toString()));
        books.forEach(a -> savePassedAuthors(a.getAuthors()));
        List<Book> savedBooks = bookRepository.saveAll(books);
        for (Book book : savedBooks) {
            messageProducer.sendMessage(book);
        }
        return savedBooks;
    }

    @GetMapping
    public Page<Book> findAll(@RequestParam int page, @RequestParam int size) {
        log.debug("Request to get all Books");
        return bookRepository.findAll(PageRequest.of(page, size));
    }

    @GetMapping("/{id}")
    public Optional<Book> findOne(@PathVariable String id) {
        log.debug("Request to get Book : {}", id);
        return bookRepository.findById(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        log.debug("Request to delete Book : {}", id);
        bookRepository.deleteById(id);
    }

    private void savePassedAuthors(Collection<Author> authors) {
        authors.stream()
                .filter(author -> author.getId() == null)
                .forEach(author -> author.setId(randomUUID().toString()));
        authorRepository.saveAll(authors);
    }
}