package org.example.backend.kafka.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.kafka.message.RecognitionMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, RecognitionMessage> kafkaTemplate;

    @Value("${kafka.topics.recognition-requests}")
    private String recognitionTopic;

    @Transactional
    public void sendRecognitionRequest(Long taskId) {
        RecognitionMessage message = new RecognitionMessage(taskId);

        kafkaTemplate.send(recognitionTopic, taskId.toString(), message)
                .whenComplete((result, ex) -> {
                    if (ex == null) {
                        log.info("Message sent successfully for task: {}. Offset: {}",
                                taskId, result.getRecordMetadata().offset());
                    } else {
                        log.error("Failed to send message for task: {}", taskId, ex);
                    }
                });
    }
}
