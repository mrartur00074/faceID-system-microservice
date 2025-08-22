package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.FaceRecognitionResponse;
import org.example.backend.feign.FaceRecognizerFeignClient;
import org.example.backend.service.FaceRecognitionService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class FaceRecognitionServiceImpl implements FaceRecognitionService {
    private final FaceRecognizerFeignClient faceRecognizerFeignClient;

    @Override
    public float[] getEmbedding(String base64) {
        try {
            NumberReaderRequest numberReaderRequest = new NumberReaderRequest();
            numberReaderRequest.setBase64(base64);

            ResponseEntity<FaceRecognitionResponse> response = faceRecognizerFeignClient.recognizeFaces(numberReaderRequest);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                FaceRecognitionResponse body = response.getBody();

                if (body.getEmbedding() != null && body.getEmbedding().length > 0) {
                    return body.getEmbedding();
                } else {
                    log.warn("Face recognitor service returned empty or null recognized embedding");
                    return new float[0];
                }
            } else {
                log.warn("Invalid response from Face recognitor service. Status: {}", response.getStatusCode());
                return new float[0];
            }
        } catch (Exception e) {
            log.warn("Invalid response from Face recognitor service.");
            return new float[0];
        }
    }
}
