package com.epam.ekc.search.service;

import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class StorageService {

    private final RestTemplate restTemplate = new RestTemplate();
    private  String booksUrl = "http://localhost:10001/api/books/";

    public Book getBookById(String bookId) {
        return restTemplate.getForObject(booksUrl + bookId, Book.class);
    }
}
