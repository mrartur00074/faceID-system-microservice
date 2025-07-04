package org.example.backend.service;

import org.example.backend.DTO.ApplicantDTO;

public interface ErrorApplicantService {
    void save(ApplicantDTO dto, String error);
}
