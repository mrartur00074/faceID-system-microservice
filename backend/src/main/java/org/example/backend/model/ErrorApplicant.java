package org.example.backend.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorApplicant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer applicantId;

    private String name;
    private String surname;
    private String phoneNum;
    private String school;
    private Integer attempt;
    private String status;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String base64;

    @Lob
    private byte[] arrayData;

    private String error;

    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT")
    private String embedding;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
