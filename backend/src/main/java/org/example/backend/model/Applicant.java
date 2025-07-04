package org.example.backend.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Applicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private Integer applicantId;

    private String name;
    private String surname;
    private String phoneNum;
    private String school;
    private Integer attempt;
    private String status;
    private String base64;

    @Column(columnDefinition = "TEXT")
    private String embedding;

    private LocalDateTime createdAt = LocalDateTime.now();
}
