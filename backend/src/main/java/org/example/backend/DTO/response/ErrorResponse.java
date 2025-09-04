package org.example.backend.DTO.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private String errorCode;
    private List<ValidationError> validationErrors;

    public ErrorResponse(HttpStatus status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status.value();
        this.error = status.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(HttpStatus status, String message, String path, String errorCode) {
        this(status, message, path);
        this.errorCode = errorCode;
    }

    public ErrorResponse(HttpStatus status, String message, String path, List<ValidationError> validationErrors) {
        this(status, message, path);
        this.validationErrors = validationErrors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError {
        private String field;
        private String message;
        private Object rejectedValue;
    }

    public static ErrorResponse of(HttpStatus status, String message, String path) {
        return new ErrorResponse(status, message, path);
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, String errorCode) {
        return new ErrorResponse(status, message, path, errorCode);
    }

    public static ErrorResponse of(HttpStatus status, String message, String path, List<ValidationError> validationErrors) {
        return new ErrorResponse(status, message, path, validationErrors);
    }
}
