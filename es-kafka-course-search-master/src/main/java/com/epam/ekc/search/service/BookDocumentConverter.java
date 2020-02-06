package com.epam.ekc.search.service;

import com.epam.ekc.search.dto.BookDocument;
import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookDocumentConverter {

    public BookDocument convertToDocument(Book book) {
        BookDocument bookDocument = new BookDocument();
        bookDocument.setId(book.getId());
        bookDocument.setCustomerId(book.getCustomerId());
        bookDocument.setGenre(book.getGenre());
        bookDocument.setLanguage(book.getLanguage());
        bookDocument.setPublicationDate(book.getPublicationDate());
        bookDocument.setTitle(book.getTitle());
        bookDocument.setType(book.getType());
        bookDocument.setAuthors(book.getAuthors().stream()
                .map(author -> author.getFirstName() + " " + author.getLastName())
                .collect(Collectors.toList()));
        return bookDocument;
    }
}
