package org.example.backend.service.applicant;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.model.ApplicantTask;

import java.util.Optional;

public interface ApplicantTaskService {
    RecognitionMessage addTaskForAddApplicant(ApplicantDTO dto);
    void updateStatus(Long taskId, ApplicantTask.ApplicationStatus status);
    Optional<ApplicantTask> getApplicantTaskById(Long taskId);
}
