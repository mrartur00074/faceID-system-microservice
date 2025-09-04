package org.example.backend.exception;

public enum ErrorCode {
    INTERNAL_SERVER_ERROR("ERR-0001", "Internal server error"),
    VALIDATION_ERROR("ERR-0002", "Validation error"),
    NOT_FOUND("ERR-0003", "Resource not found"),
    UNAUTHORIZED("ERR-0004", "Unauthorized access"),
    FORBIDDEN("ERR-0005", "Access forbidden"),

    APPLICANT_NOT_FOUND("ERR-1001", "Applicant not found"),
    APPLICANT_ALREADY_EXISTS("ERR-1002", "Applicant already exists"),
    APPLICANT_INVALID_DATA("ERR-1003", "Invalid applicant data"),

    ADMISSION_CAMPAIGN_NOT_FOUND("ERR-2001", "Admission campaign not found"),
    EXAM_SESSION_NOT_FOUND("ERR-2002", "Exam session not found"),
    EXAM_SUBJECT_NOT_FOUND("ERR-2003", "Exam subject not found"),
    EXAM_RESULT_NOT_FOUND("ERR-2004", "Exam result not found"),
    EXAM_SESSION_FULL("ERR-2005", "Exam session is full"),

    EXTERNAL_SERVICE_UNAVAILABLE("ERR-3001", "External service unavailable"),
    IMAGE_PROCESSING_ERROR("ERR-3002", "Image processing error"),
    FACE_RECOGNITION_ERROR("ERR-3003", "Face recognition error"),
    NUMBER_RECOGNITION_ERROR("ERR-3004", "Number recognition error");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
