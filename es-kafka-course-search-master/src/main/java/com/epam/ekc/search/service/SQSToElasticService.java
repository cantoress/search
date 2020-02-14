package com.epam.ekc.search.service;

import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SQSToElasticService {

    private final SQSMessageConsumer sqsMessageConsumer;
    private final ElasticService elasticService;

    @Scheduled(fixedRate = 1000)
    public void sendDocumentsToElastic() throws IOException {
        if (!sqsMessageConsumer.messagesInQueue().equals("0")) {
            List<Book> books = sqsMessageConsumer.receiveBatchOfMessages();
            if (!books.isEmpty()) {
                elasticService.saveListOfBooksToIndex(books);
            }
        }
    }
}
