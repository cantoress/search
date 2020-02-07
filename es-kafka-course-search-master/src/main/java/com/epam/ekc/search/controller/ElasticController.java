package com.epam.ekc.search.controller;

import com.epam.ekc.search.dto.BookDocument;
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
    public List<BookDocument> findALL() throws IOException {
        return elasticService.findAll();
    }

    @GetMapping(value = "/find")
    public Map<String, Object> findForField(String fieldName, String value, int fromResult, int numberOfResults, Integer enoughWords, boolean withShingles) throws IOException {
        return elasticService.searchByField(fieldName, value, fromResult, numberOfResults, enoughWords, withShingles);
    }

    @GetMapping(value = "/aggregate")
    public Map<String, Long> countByField(String fieldName) throws IOException {
        return elasticService.countByAggregation(fieldName);
    }

    @GetMapping(value = "/suggest")
    public List<String> suggestTitle(String start) throws IOException {
        return elasticService.autocompleteTitle(start);
    }
}
