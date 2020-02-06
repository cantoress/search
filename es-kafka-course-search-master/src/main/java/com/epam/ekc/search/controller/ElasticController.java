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
    public Map<String, Object> findForField(String fieldname, String value, int fromResult, int numberOfResults) throws IOException {
        return elasticService.searchByField(fieldname, value, fromResult, numberOfResults);
    }

    @GetMapping(value = "/aggregate")
    public Map<String, Long> countByField(String fieldname) throws IOException {
        return elasticService.countByAggregation(fieldname);
    }
}
