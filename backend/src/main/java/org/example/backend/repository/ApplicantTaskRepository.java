package org.example.backend.repository;

import org.example.backend.model.ApplicantTask;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicantTaskRepository extends JpaRepository<ApplicantTask, Long> {
}
