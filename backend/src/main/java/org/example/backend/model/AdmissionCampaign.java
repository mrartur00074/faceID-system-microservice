package org.example.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "admission_campaigns")
public class AdmissionCampaign {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "year", unique = true, nullable = false)
    private Year year;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "admissionCampaign", cascade = CascadeType.ALL)
    private List<ExamSubject> subjects = new ArrayList<>();

    @OneToMany(mappedBy = "admissionCampaign", cascade = CascadeType.ALL)
    private List<ExamSession> examSessions = new ArrayList<>();
}
