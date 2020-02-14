package com.epam.ekc.search.service;

import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${booksUrl}")
    private String booksUrl;

    public Book getBookById(String bookId) {
        return restTemplate.getForObject(booksUrl + bookId, Book.class);
    }
}
