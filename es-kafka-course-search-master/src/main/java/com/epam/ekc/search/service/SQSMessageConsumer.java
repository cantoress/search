package com.epam.ekc.search.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.epam.ekc.search.model.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SQSMessageConsumer {

    private final AmazonSQS sqsClient;
    private final StorageService storageService;

    private static final String QUEUE_NAME = "BookNotifications";
    private static String queueUrl;

    @PostConstruct
    public void init() {
        queueUrl = sqsClient.getQueueUrl(QUEUE_NAME).getQueueUrl();
    }

    public String messagesInQueue() {
        GetQueueAttributesRequest getQueueAttributesRequest
                = new GetQueueAttributesRequest(queueUrl)
                .withAttributeNames("All");
        GetQueueAttributesResult getQueueAttributesResult
                = sqsClient.getQueueAttributes(getQueueAttributesRequest);

        return getQueueAttributesResult.getAttributes().get("ApproximateNumberOfMessages");

    }

    public Book receiveMessage() {
        try {
            ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(1)
                    .withWaitTimeSeconds(20);
            List<Message> messages = sqsClient.receiveMessage(request).getMessages();
            String bookId = messages.get(0).getBody();
            Book bookFromMessage = storageService.getBookById(bookId);
            sqsClient.deleteMessage(queueUrl, messages.get(0).getReceiptHandle());
            return bookFromMessage;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No messages in the queue");
            return null;
        }
    }

    public List<Book> receiveBatchOfMessages() {
        try {
            ReceiveMessageRequest request = new ReceiveMessageRequest(queueUrl)
                    .withMaxNumberOfMessages(10)
                    .withWaitTimeSeconds(20);
            List<Message> messages = sqsClient.receiveMessage(request).getMessages();
            Set<String> keys = new HashSet<>();
            List<DeleteMessageBatchRequestEntry> deleteMessageBatchRequestEntries = new ArrayList<>();
            for (Message message : messages) {
                keys.add(message.getBody());
                deleteMessageBatchRequestEntries.add(new DeleteMessageBatchRequestEntry(message.getMessageId(), message.getReceiptHandle()));
            }
            List<Book> books = new ArrayList<>();
            for (String key : keys) {
                Book bookFromMessage = storageService.getBookById(key);
                books.add(bookFromMessage);
            }
            if (!deleteMessageBatchRequestEntries.isEmpty()){
                sqsClient.deleteMessageBatch(queueUrl, deleteMessageBatchRequestEntries);
            }
            return books;
        } catch (IndexOutOfBoundsException e) {
            System.out.println("No messages in the queue");
            return null;
        }
    }
}
