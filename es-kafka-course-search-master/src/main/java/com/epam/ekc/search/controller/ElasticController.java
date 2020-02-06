package com.epam.ekc.search.controller;

import com.epam.ekc.search.model.Book;
import com.epam.ekc.search.service.ElasticService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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
    public List<Book> findALL() throws IOException {
        return elasticService.findAll();
    }

    @PostMapping
    public Map<String, Object> findForField(String fieldname, String value, int fromResult, int numberOfResults) throws IOException {
        return elasticService.searchByField(fieldname, value, fromResult, numberOfResults);
    }

    @PostMapping(value="/aggregate")
    public Map<String, Long> countByField(String fieldname) throws IOException {
        return elasticService.countByAggregation(fieldname);
    }
}
