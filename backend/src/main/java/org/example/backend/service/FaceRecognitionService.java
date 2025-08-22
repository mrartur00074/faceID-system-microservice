package org.example.backend.service;

public interface FaceRecognitionService {
    float[] getEmbedding(String imagePath);
}
