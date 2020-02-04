package com.epam.ekc.storage.controller;

import static java.util.UUID.randomUUID;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import com.epam.ekc.storage.model.Author;
import com.epam.ekc.storage.repository.AuthorRepository;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
@RequestMapping("/api/authors")
public class AuthorController {

  private final AuthorRepository authorRepository;

  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  public Author save(@RequestBody Author author) {
    author.setId(randomUUID().toString());
    return authorRepository.save(author);
  }

  @PostMapping(value = "/batch", consumes = APPLICATION_JSON_VALUE)
  public List<Author> saveAll(@RequestBody List<Author> author) {
    author.forEach(a -> a.setId(randomUUID().toString()));
    return authorRepository.saveAll(author);
  }

  @GetMapping
  public Page<Author> findAll(@RequestParam int page, @RequestParam int size) {
    log.debug("Request to get all Authosr");
    return authorRepository.findAll(PageRequest.of(page, size));
  }

  @GetMapping("/{id}")
  public Optional<Author> findOne(@PathVariable String id) {
    log.debug("Request to get Authors : {}", id);
    return authorRepository.findById(id);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable String id) {
    log.debug("Request to delete Authors : {}", id);
    authorRepository.deleteById(id);
  }
}
