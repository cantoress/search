package com.epam.ekc.search.service;

import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageConsumer {

    private final StorageService storageService;
    private final ElasticService elasticService;

    @KafkaListener(id = "single-listener",
            containerFactory = "singleConsumerFactory",
            topics = "storage.entity",
            groupId = "search.batch")
    public void receiveMessage(List<ConsumerRecord<String, String>> messages) throws IOException {
        List<Book> books = new ArrayList<>();
        for (ConsumerRecord<String, String> message : messages) {
            var key = message.key();
            Book bookFromMessage = storageService.getBookById(key);
            books.add(bookFromMessage);
        }
        elasticService.saveListOfBooksToIndex(books, "book_index");
    }
}
