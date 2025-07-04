package org.example.backend.DTO;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlackListDTO {
    private Long id;
    private Integer applicantId;
    private String name;
    private String surname;
    private String phoneNum;
    private String school;
    private String base64;
    private String embedding;
}
