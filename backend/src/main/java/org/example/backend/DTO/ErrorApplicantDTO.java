package org.example.backend.DTO;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ErrorApplicantDTO {
    private Long id;
    private Integer applicantId;
    private String name;
    private String surname;
    private String phoneNum;
    private String school;
    private Integer attempt;
    private String status;
    private String base64;
    private String error;
    private LocalDateTime createdAt;
    private String embedding;
}
