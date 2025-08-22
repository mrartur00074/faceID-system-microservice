package org.example.backend.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import org.springframework.core.io.Resource;
import org.springframework.util.StreamUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class ImageConvertor {
    private final ResourceLoader resourceLoader;

    public String convertImageToBase64(String imagePath) throws IOException {
        Resource resource = resourceLoader.getResource("file:" + imagePath);

        try (InputStream inputStream = resource.getInputStream()) {
            byte[] imageBytes = StreamUtils.copyToByteArray(inputStream);
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }
}
