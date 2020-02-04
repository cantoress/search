package com.epam.ekc.storage.controller;

import static java.util.UUID.randomUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.epam.ekc.storage.model.Author;
import com.epam.ekc.storage.model.Book;
import com.epam.ekc.storage.repository.AuthorRepository;
import com.epam.ekc.storage.repository.BookRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/books")
public class BookController {

  private final BookRepository bookRepository;
  private final AuthorRepository authorRepository;

  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public Book save(@RequestBody @Validated Book book) {
    book.setId(randomUUID().toString());
    savePassedAuthors(book.getAuthors());
    return bookRepository.save(book);
  }

  @PostMapping(value = "/batch", consumes = APPLICATION_JSON_VALUE)
  public List<Book> saveAll(@RequestBody List<Book> books) {
    books.forEach(a -> a.setId(randomUUID().toString()));
    return bookRepository.saveAll(books);
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
