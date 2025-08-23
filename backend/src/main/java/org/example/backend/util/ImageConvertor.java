package org.example.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ImageConvertor {
    private final ResourceLoader resourceLoader;
    private static final String IMAGE_DIR = "/app/images";
    private static final String IMAGE_DB_DIR = "images";

    public String convertImageToBase64(String imagePath) throws IOException {
        Resource resource = resourceLoader.getResource("file:" + imagePath);

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    public String saveImageToFile(String base64Image) {
        try {
            String folderPath = IMAGE_DIR;
            Files.createDirectories(Paths.get(folderPath));

            String uniqueName = "student_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".png";
            String filePath = folderPath + File.separator + uniqueName;

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(filePath), imageBytes);

            filePath = IMAGE_DB_DIR + File.separator + uniqueName;

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
        }
    }

    public Resource loadImageAsResource(String filename) {
        try {
            Path path = Paths.get(IMAGE_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) return resource;
            else throw new RuntimeException("Файл не найден: " + filename);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении изображения", e);
        }
    }
}
