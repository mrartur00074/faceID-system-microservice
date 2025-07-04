package org.example.backend.service.Impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.RequiredArgsConstructor;
import org.example.backend.service.ExternalRecognitionService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class ExternalRecognitionServiceImpl implements ExternalRecognitionService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final BlockingQueue<String> numberReaderResponse = new ArrayBlockingQueue<>(1);
    private final BlockingQueue<String> faceEmbeddingResponse = new ArrayBlockingQueue<>(1);

    @KafkaListener(topics = "number-reader-response", groupId = "recognizer")
    public void handleNumberReaderResponse(String message) {
        numberReaderResponse.offer(message);
    }

    @KafkaListener(topics = "face-recognizer-response", groupId = "recognizer")
    public void handleEmbeddingResponse(String message) {
        faceEmbeddingResponse.offer(message);
    }

    @Override
    public Integer recognizeApplicantId(String base64) {
        kafkaTemplate.send("number-reader-request", base64);
        try {
            String json = numberReaderResponse.poll(5, TimeUnit.SECONDS);
            if (json == null) throw new RuntimeException("Timeout from number-reader");

            JsonNode root = new ObjectMapper().readTree(json);
            if (root.has("error")) throw new RuntimeException("Recognition error: " + root.get("error").asText());
            return root.get("number").asInt();
        } catch (Exception e) {
            throw new RuntimeException("Failed to recognize number", e);
        }
    }

    @Override
    public float[] getEmbedding(String base64) {
        kafkaTemplate.send("face-recognizer-request", base64);
        try {
            String json = faceEmbeddingResponse.poll(5, TimeUnit.SECONDS);
            if (json == null) throw new RuntimeException("Timeout from face-recognizer");

            JsonNode root = new ObjectMapper().readTree(json);
            if (root.has("error")) throw new RuntimeException("Embedding error: " + root.get("error").asText());

            ArrayNode arr = (ArrayNode) root.get("embedding");
            float[] embedding = new float[arr.size()];
            for (int i = 0; i < arr.size(); i++) {
                embedding[i] = (float) arr.get(i).asDouble();
            }
            return embedding;
        } catch (Exception e) {
            throw new RuntimeException("Failed to get embedding", e);
        }
    }
}
