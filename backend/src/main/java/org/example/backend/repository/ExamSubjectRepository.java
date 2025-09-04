package org.example.backend.repository;

import org.example.backend.model.ExamSubject;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExamSubjectRepository extends JpaRepository<ExamSubject, Long> {
}
