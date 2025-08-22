package org.example.backend.feign;

import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.NumberReaderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "number-reader", configuration = FeignConfig.class)
public interface NumberReaderFeignClient {
    @PostMapping("/recognize-base64/")
    ResponseEntity<NumberReaderResponse> recognizeNumber(@RequestBody NumberReaderRequest request);
}
