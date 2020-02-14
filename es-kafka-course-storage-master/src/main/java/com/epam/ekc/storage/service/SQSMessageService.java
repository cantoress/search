package com.epam.ekc.storage.service;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.*;
import com.epam.ekc.storage.model.Identifiable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SQSMessageService {

    private final AmazonSQS sqsClient;

    private static String queueURL;
    private static final String QUEUE_NAME = "BookNotifications";

    @PostConstruct
    public void createQueue() {
        try {
            CreateQueueRequest createStandardQueueRequest = new CreateQueueRequest(QUEUE_NAME);
            queueURL = sqsClient.createQueue(createStandardQueueRequest).getQueueUrl();
        } catch (QueueNameExistsException e) {
            System.out.println("The queue with name " + QUEUE_NAME + " already exists");
        }

    }

    public void sendMessage(Identifiable identifiable) {
        SendMessageRequest sendMessageStandardQueue = new SendMessageRequest()
                .withQueueUrl(queueURL)
                .withMessageBody(identifiable.getId());

        sqsClient.sendMessage(sendMessageStandardQueue);
    }

    public void sendBatchOfMessages(List<Identifiable> identifiables) {
        List<SendMessageBatchRequestEntry> messageEntries = new ArrayList<>();
        for (Identifiable identifiable : identifiables) {
            messageEntries.add(new SendMessageBatchRequestEntry()
                    .withId(UUID.randomUUID().toString())
                    .withMessageBody(identifiable.getId()));
        }
        for (int i = 0; i < messageEntries.size(); i += 10) {
            SendMessageBatchRequest sendMessageBatchRequest
                    = new SendMessageBatchRequest(queueURL, messageEntries.subList(i, Math.min(i + 10, messageEntries.size())));
            sqsClient.sendMessageBatch(sendMessageBatchRequest);
            System.out.println("Send to queue some messages");
        }
    }


}
