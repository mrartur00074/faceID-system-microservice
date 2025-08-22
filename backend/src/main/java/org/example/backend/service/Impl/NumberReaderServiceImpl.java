package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.NumberReaderResponse;
import org.example.backend.feign.NumberReaderFeignClient;
import org.example.backend.service.NumberReaderService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class NumberReaderServiceImpl implements NumberReaderService {
    private final NumberReaderFeignClient numberReaderFeignClient;

    @Override
    public Optional<Integer> recognizerNumber(String base64) {
        try {
            NumberReaderRequest numberReaderRequest = new NumberReaderRequest();
            numberReaderRequest.setBase64(base64);

            ResponseEntity<NumberReaderResponse> response = numberReaderFeignClient.recognizeNumber(numberReaderRequest);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                NumberReaderResponse body = response.getBody();

                if (body.getNumber() != null) {
                    Integer number = body.getNumber();
                    return Optional.of(number);
                } else {
                    log.warn("Number reader service returned empty or null recognized number");
                    return Optional.empty();
                }
            } else {
                log.warn("Invalid response from number reader service. Status: {}", response.getStatusCode());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error calling number-reader-service: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
