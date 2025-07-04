package org.example.backend.repository;

import org.example.backend.model.ErrorApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ErrorApplicantRepository extends JpaRepository<ErrorApplicant, Long> {
}
