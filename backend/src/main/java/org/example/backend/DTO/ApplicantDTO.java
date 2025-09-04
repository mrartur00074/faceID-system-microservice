package org.example.backend.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ApplicantDTO implements Serializable {
    private Long id;
    private Integer applicantId;
    private String name;
    private String surname;
    private String phoneNum;
    private String school;
    private Integer attempt;
    private String status;
    private String base64;
    private LocalDateTime createdAt;
    private String embedding;
}
