package org.example.backend.service;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.kafka.message.RecognitionMessage;

public interface ApplicantTaskService {
    RecognitionMessage addTaskForAddApplicant(ApplicantDTO dto);
}
