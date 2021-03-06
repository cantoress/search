package com.epam.ekc.search.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class BookDocument {

    private String id;
    private String customerId;
    private String type;
    private String genre;
    private String title;
    private String language;
    private Instant publicationDate;
    private List<String> authors = new ArrayList<>();
    private String suggest;
}
