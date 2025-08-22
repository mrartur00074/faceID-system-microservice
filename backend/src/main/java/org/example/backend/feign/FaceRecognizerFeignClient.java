package org.example.backend.feign;

import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.FaceRecognitionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "face-recognizer", configuration = FeignConfig.class)
public interface FaceRecognizerFeignClient {
    @PostMapping("/get-embedding/")
    ResponseEntity<FaceRecognitionResponse> recognizeFaces(@RequestBody NumberReaderRequest request);
}
