package org.example.backend.kafka.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.service.Impl.ApplicantVerificationServiceImpl;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final ApplicantVerificationServiceImpl applicantVerificationService;

    @KafkaListener(
            topics = "${kafka.topics.recognition-requests}",
            groupId = "${kafka.consumer-group.recognition}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void consumeRecognitionRequest(RecognitionMessage message) {
        try {
            log.info("Received recognition request for task: {}", message.getTaskId());

            applicantVerificationService.addApplicantWithVerification(message);

            log.info("Successfully processed task: {}", message.getTaskId());
        } catch (Exception e) {
            log.error("Error processing recognition request for task: {}", message.getTaskId(), e);
        }
    }
}
