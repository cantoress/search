package com.epam.ekc.search.controller;

import com.epam.ekc.search.dto.BookDocumentWithNotFoundWords;
import com.epam.ekc.search.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/search")
public class ElasticController {

    private final ElasticService elasticService;

    @GetMapping
    public List<BookDocumentWithNotFoundWords> findall() throws IOException {
        return elasticService.findAll();
    }

    @GetMapping(value = "/find")
    public Map<String, Object> findForField(String titleValue, String authorsValue, int fromResult, int numberOfResults, Integer enoughWordsForTitle, boolean withShingles, String language) throws IOException {
        return elasticService.search(titleValue, authorsValue, fromResult, numberOfResults, enoughWordsForTitle, withShingles, language);
    }

    @GetMapping(value = "/suggest")
    public List<String> suggestTitle(String start) throws IOException {
        return elasticService.autocompleteTitle(start);
    }

    @GetMapping(value = "/didyoumean")
    public List<String> didYouMeanTitle(String title) throws IOException {
        return elasticService.didYouMean(title);
    }
}
