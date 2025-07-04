package org.example.backend.util;

public class EmbeddingUtils {
    private static final double SIMILARITY_THRESHOLD = 0.6;

    public static boolean isSimilar(String embStr1, String embStr2) {
        float[] arr1 = parseEmbeddingString(embStr1);
        float[] arr2 = parseEmbeddingString(embStr2);
        return cosineSimilarity(arr1, arr2) > SIMILARITY_THRESHOLD;
    }

    private static float[] parseEmbeddingString(String embStr) {
        String[] parts = embStr.split(",");
        float[] result = new float[parts.length];
        for (int i = 0; i < parts.length; i++) {
            result[i] = Float.parseFloat(parts[i].trim());
        }
        return result;
    }

    private static double cosineSimilarity(float[] v1, float[] v2) {
        double dot = 0.0, normV1 = 0.0, normV2 = 0.0;
        for (int i = 0; i < v1.length; i++) {
            dot += v1[i] * v2[i];
            normV1 += v1[i] * v1[i];
            normV2 += v2[i] * v2[i];
        }

        System.out.println("Схожесть: " + dot / (Math.sqrt(normV1) * Math.sqrt(normV2)));
        return dot / (Math.sqrt(normV1) * Math.sqrt(normV2));
    }
}
