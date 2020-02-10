package com.epam.ekc.search.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookDocumentWithNotFoundWords {
    private BookDocument bookDocument;
    private List<String> notFoundWords;
}
