package org.example.backend.service;

public interface ExternalRecognitionService {
    Integer recognizeApplicantId(String base64);
    float[] getEmbedding(String base64);
}
