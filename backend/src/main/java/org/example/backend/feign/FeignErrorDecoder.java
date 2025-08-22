package org.example.backend.feign;

import feign.Response;
import feign.codec.ErrorDecoder;
import org.example.backend.DTO.ErrorApplicantDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

public class FeignErrorDecoder implements ErrorDecoder {

    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() >= 400 && response.status() <= 499) {
            return new ResponseStatusException(
                    HttpStatus.valueOf(response.status()),
                    "Number Reader Service Error: " + response.reason()
            );
        }
        if (response.status() >= 500) {
            return new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Number Reader Service Unavailable"
            );
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }

    public static ErrorApplicantDTO createErrorApplicantDTO(Long id, String base64, String error) {
        ErrorApplicantDTO errorDto = new ErrorApplicantDTO();
        errorDto.setId(id);
        errorDto.setBase64(base64);
        errorDto.setError(error);
        errorDto.setStatus("ERROR");
        errorDto.setCreatedAt(LocalDateTime.now());
        return errorDto;
    }
}
