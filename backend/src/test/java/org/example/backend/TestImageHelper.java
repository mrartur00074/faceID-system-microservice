package org.example.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class TestImageHelper {

    public static String getTestImageBase64(String filename) throws IOException {
        Path imagePath = Paths.get("src", "test", "resources", "test_images", filename);
        byte[] imageBytes = Files.readAllBytes(imagePath);
        return Base64.getEncoder().encodeToString(imageBytes);
    }

    public static String getSamePerson1Base64() throws IOException {
        return getTestImageBase64("same_person_1.jpg");
    }

    public static String getSamePerson2Base64() throws IOException {
        return getTestImageBase64("same_person_2.jpg");
    }
}
