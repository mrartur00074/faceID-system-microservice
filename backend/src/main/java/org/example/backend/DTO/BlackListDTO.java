package org.example.backend.DTO;

import lombok.*;

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
    private Integer attempt;
    private String status;
    private String base64;
    private String embedding;
}
