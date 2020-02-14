package com.epam.ekc.search.service;

import com.epam.ekc.search.dto.BookDocument;
import com.epam.ekc.search.dto.BookDocumentWithNotFoundWords;
import com.epam.ekc.search.model.Book;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.core.CountRequest;
import org.elasticsearch.client.core.CountResponse;
import org.elasticsearch.client.indices.AnalyzeRequest;
import org.elasticsearch.client.indices.AnalyzeResponse;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.SuggestionBuilder;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.phrase.PhraseSuggestion;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ElasticService {

    private final RestHighLevelClient client;
    private final ObjectMapper objectMapper;
    private final BookDocumentConverter converter;

    public static final String INDEX_NAME = "book_index";
    public static final String INDEX_MAPPING = "{\n" +
            "  \"properties\": {\n" +
            "    \"id\": {\n" +
            "      \"type\": \"text\"\n" +
            "    },\n" +
            "    \"customerId\": {\n" +
            "      \"type\": \"text\"\n" +
            "    },\n" +
            "    \"type\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    },\n" +
            "    \"genre\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    },\n" +
            "    \"title\": {\n" +
            "      \"type\": \"text\",\n" +
            "      \"fields\": {\n" +
            "        \"shingles\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"analyzer\": \"my_shingle_analyzer\"\n" +
            "        },\n" +
            "        \"en\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"analyzer\": \"english\"\n" +
            "        },\n" +
            "        \"ru\": {\n" +
            "          \"type\": \"text\",\n" +
            "          \"analyzer\": \"russian\"\n" +
            "        }\n" +
            "      }\n" +
            "    },\n" +
            "    \"language\": {\n" +
            "      \"type\": \"keyword\"\n" +
            "    },\n" +
            "    \"publicationDate\": {\n" +
            "      \"type\": \"date\",\n" +
            "      \"format\": \"date_optional_time\"\n" +
            "    },\n" +
            "    \"authors\": {\n" +
            "      \"type\": \"text\"\n" +
            "    },\n" +
            "    \"suggest\": {\n" +
            "      \"type\": \"completion\"\n" +
            "    }\n" +
            "  }\n" +
            "}";

    private static final String INDEX_SETTINGS = "{\n" +
            "        \"number_of_shards\": 1,  \n" +
            "        \"analysis\": {\n" +
            "            \"filter\": {\n" +
            "                \"my_shingle_filter\": {\n" +
            "                    \"type\":             \"shingle\",\n" +
            "                    \"min_shingle_size\": 2, \n" +
            "                    \"max_shingle_size\": 3, \n" +
            "                    \"output_unigrams\":  true   \n" +
            "                }\n" +
            "            },\n" +
            "            \"analyzer\": {\n" +
            "                \"my_shingle_analyzer\": {\n" +
            "                    \"type\":             \"custom\",\n" +
            "                    \"tokenizer\":        \"standard\",\n" +
            "                    \"filter\": [\n" +
            "                        \"lowercase\",\n" +
            "                        \"my_shingle_filter\" \n" +
            "                    ]\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    }";

    @PostConstruct
    public void createIndex() throws IOException {

//        deleteIndex(INDEX_NAME);
        if (!isIndexExist(INDEX_NAME)) {
            CreateIndexRequest request = new CreateIndexRequest(INDEX_NAME);
            request.mapping(INDEX_MAPPING, XContentType.JSON);
            request.settings(INDEX_SETTINGS, XContentType.JSON);
            client.indices().create(request, RequestOptions.DEFAULT);
        }
    }

    private boolean isIndexExist(String indexName) throws IOException {
        GetIndexRequest request = new GetIndexRequest(indexName);
        return client.indices().exists(request, RequestOptions.DEFAULT);
    }

    private void deleteIndex(String indexName) throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        client.indices().delete(request, RequestOptions.DEFAULT);
    }

    private long countDocumentsInIndex(String indexName) throws IOException {
        CountRequest countRequest = new CountRequest();
        countRequest.indices(indexName);
        CountResponse countResponse = client.count(countRequest, RequestOptions.DEFAULT);
        return countResponse.getCount();
    }

    public void saveBookToIndex(Book book) throws IOException {
        IndexRequest request = prepareIndexRequest(book, INDEX_NAME);
        client.index(request, RequestOptions.DEFAULT);
    }

    public void saveListOfBooksToIndex(List<Book> books) throws IOException {
        BulkRequest request = new BulkRequest();
        for (Book book : books) {
            request.add(prepareIndexRequest(book, INDEX_NAME));
        }
        client.bulk(request, RequestOptions.DEFAULT);
        System.out.println("Saved " + books.size() + " books");
    }

    private IndexRequest prepareIndexRequest(Book book, String indexName) {
        BookDocument bookDocument = converter.convertToDocument(book);
        Map<String, Object> bookMap = objectMapper.convertValue(bookDocument, Map.class);
        return new IndexRequest(indexName)
                .id(bookDocument.getId())
                .source(bookMap);
    }

    public List<BookDocumentWithNotFoundWords> findAll() throws IOException {
        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());
        request.source(searchSourceBuilder);
        request.indices(INDEX_NAME);
        searchSourceBuilder.from(0);
        searchSourceBuilder.size(20);
        return retrieveResultsFromSearchResponse(client.search(request, RequestOptions.DEFAULT), "");
    }

    public Map<String, Object> search(String titleValue, String authorsValue, int fromResult, int numberOfResults, Integer enoughWordsForTitle, boolean withShingles, String language) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        SearchResponse response = searchByField(titleValue, authorsValue, INDEX_NAME, fromResult, numberOfResults, enoughWordsForTitle, withShingles, language);
        result.put("Total number of found documents", getTotalNumberOfHits(response));
        result.put("Aggregated by genre field:", retrieveAggregationFromSearchResponse(response, "genre"));
        result.put("Aggregated by type field:", retrieveAggregationFromSearchResponse(response, "type"));
        result.put("Found documents on positions " + fromResult + "-" + (fromResult + numberOfResults),
                retrieveResultsFromSearchResponse(response, Optional.ofNullable(titleValue).orElse("") + " " + Optional.ofNullable(authorsValue).orElse("")));
        return result;
    }

    public SearchResponse searchByField(String titleValue, String authorsValue, String indexName, int from, int number, Integer enoughWordsForTitle, boolean withShingles, String language) throws IOException {

        SearchRequest request = new SearchRequest();
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        String[] titleValueWords = titleValue != null ? titleValue.split(" ") : null;
        String[] authorsValueWords = authorsValue != null ? authorsValue.split(" ") : null;
        BoolQueryBuilder queryBuilder = new BoolQueryBuilder();

        if (titleValueWords != null) {
            if (language != null && language.equalsIgnoreCase("russian")) {
                for (String searchWord : titleValueWords) {
                    queryBuilder.should(QueryBuilders.matchQuery("title.ru", searchWord).queryName(searchWord));
                }
            } else {
                for (String searchWord : titleValueWords) {
                    queryBuilder.should(QueryBuilders.matchQuery("title.en", searchWord).queryName(searchWord));
                }
            }

            if (withShingles) {
                queryBuilder.should(QueryBuilders.matchQuery("title.shingles", titleValue));
            }
            if (enoughWordsForTitle != null && titleValueWords.length >= enoughWordsForTitle) {
                queryBuilder.minimumShouldMatch(String.valueOf(enoughWordsForTitle));
            }
        }
        if (authorsValueWords != null) {
            for (String searchWord : authorsValueWords) {
                queryBuilder.should(QueryBuilders.matchQuery("authors", searchWord).queryName(searchWord));
            }
        }
        searchSourceBuilder.query(queryBuilder);

        TermsAggregationBuilder genreAggregation = AggregationBuilders.terms("by_genre")
                .field("genre");
        searchSourceBuilder.aggregation(genreAggregation);
        TermsAggregationBuilder typeAggregation = AggregationBuilders.terms("by_type")
                .field("type");
        searchSourceBuilder.aggregation(typeAggregation);

        request.source(searchSourceBuilder);
        request.indices(indexName);
        searchSourceBuilder.from(from);
        searchSourceBuilder.size(number);
        return client.search(request, RequestOptions.DEFAULT);
    }

    private List<BookDocumentWithNotFoundWords> retrieveResultsFromSearchResponse(SearchResponse response, String searchValue) throws IOException {
        List<BookDocumentWithNotFoundWords> books = new ArrayList<>();
        SearchHit[] hits = response.getHits().getHits();
        List<String> analyzedSearchValues = analyzeSearchString(searchValue);
        for (SearchHit hit : hits) {
            List<String> searchWords = searchValue != null ? new ArrayList<>(Arrays.asList(searchValue.split(" "))) : null;
            BookDocument document = objectMapper.convertValue(hit.getSourceAsMap(), BookDocument.class);
            List<String> foundWords = Arrays.asList(hit.getMatchedQueries());
            if (searchWords != null) {
                searchWords.removeAll(foundWords);
                searchWords.retainAll(analyzedSearchValues);
            }
            books.add(new BookDocumentWithNotFoundWords(document, searchWords));
        }
        return books;
    }

    private List<String> analyzeSearchString(String search) throws IOException {
        AnalyzeRequest request = AnalyzeRequest.buildCustomAnalyzer("standard").addTokenFilter("stop").build(search);

        AnalyzeResponse response = client.indices().analyze(request, RequestOptions.DEFAULT);
        List<AnalyzeResponse.AnalyzeToken> tokens = response.getTokens();
        List<String> result = new ArrayList<>();
        for (AnalyzeResponse.AnalyzeToken token : tokens) {
            result.add(token.getTerm());
        }
        return result;
    }

    public long getTotalNumberOfHits(SearchResponse response) {
        TotalHits totalHits = response.getHits().getTotalHits();
        return totalHits.value;
    }

    private Map<String, Long> retrieveAggregationFromSearchResponse(SearchResponse response, String fieldName) {

        Map<String, Long> result = new HashMap<>();
        Aggregations aggregations = response.getAggregations();
        Terms fieldAggregation = aggregations.get("by_" + fieldName);
        for (Terms.Bucket bucket : fieldAggregation.getBuckets()) {
            result.put(bucket.getKeyAsString(), bucket.getDocCount());
        }
        return result;
    }

    public List<String> autocompleteTitle(String start) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders.completionSuggestion("suggest").prefix(start);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("suggest_title", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        request.source(searchSourceBuilder);
        return retrieveSuggestionsFromSearchResponse(client.search(request, RequestOptions.DEFAULT));

    }

    private List<String> retrieveSuggestionsFromSearchResponse(SearchResponse response) {
        List<String> suggestion = new ArrayList<>();
        Suggest suggest = response.getSuggest();
        CompletionSuggestion termSuggestion = suggest.getSuggestion("suggest_title");
        for (CompletionSuggestion.Entry entry : termSuggestion.getEntries()) {
            for (CompletionSuggestion.Entry.Option option : entry) {
                suggestion.add(option.getText().string());
            }
        }
        return suggestion;
    }

    public List<String> didYouMean(String value) throws IOException {
        SearchRequest request = new SearchRequest();
        request.indices(INDEX_NAME);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        SuggestionBuilder termSuggestionBuilder =
                SuggestBuilders.phraseSuggestion("title.shingles")
                        .maxErrors(3f)
                        .size(5)
                        .gramSize(3)
                        .realWordErrorLikelihood(1.0f)
                        .highlight("<b>", "</b>")
                        .collateQuery(QueryBuilders.matchQuery("title", "{{suggestion}}").operator(Operator.AND).toString())
                        .text(value);
        SuggestBuilder suggestBuilder = new SuggestBuilder();
        suggestBuilder.addSuggestion("didyoumean_title", termSuggestionBuilder);
        searchSourceBuilder.suggest(suggestBuilder);
        request.source(searchSourceBuilder);
        return retrieveDidYouMeanFromSearchResponse(client.search(request, RequestOptions.DEFAULT));
    }

    private List<String> retrieveDidYouMeanFromSearchResponse(SearchResponse response) {
        List<String> suggestion = new ArrayList<>();
        Suggest suggest = response.getSuggest();
        PhraseSuggestion termSuggestion = suggest.getSuggestion("didyoumean_title");
        for (PhraseSuggestion.Entry entry : termSuggestion.getEntries()) {
            for (PhraseSuggestion.Entry.Option option : entry) {
                suggestion.add(option.getHighlighted().string());
            }
        }
        return suggestion;
    }
}
