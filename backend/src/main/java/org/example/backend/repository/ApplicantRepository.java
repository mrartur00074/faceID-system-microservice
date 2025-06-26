package org.example.backend.repository;

import org.example.backend.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantRepository extends JpaRepository<Applicant, Long> {
    Optional<Applicant> findByApplicantId(Integer applicantId);
    void deleteByApplicantId(Integer applicantId);
    boolean existsByApplicantId(Integer applicantId);
}
