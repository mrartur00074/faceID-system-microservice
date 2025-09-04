package org.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "exam_subjects")
public class ExamSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "passing_score")
    private Integer passingScore;

    @Column(name = "max_score")
    private Integer maxScore = 100;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admission_campaign_id")
    private AdmissionCampaign admissionCampaign;
}
