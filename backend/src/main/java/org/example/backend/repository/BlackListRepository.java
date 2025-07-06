package org.example.backend.repository;
import org.example.backend.model.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BlackListRepository extends JpaRepository<BlackList, Long> {
    Optional<BlackList> findByApplicantId(Integer applicantId);
    void deleteByApplicantId(Integer applicantId);
    boolean existsByApplicantId(Integer applicantId);
}
