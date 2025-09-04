package org.example.backend.service.applicant;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.DTO.ErrorApplicantDTO;

import java.util.List;

public interface ErrorApplicantService {
    void save(ApplicantDTO dto, String error);
    List<ErrorApplicantDTO> getAll();
    ErrorApplicantDTO getById(Long getById);
    ErrorApplicantDTO update(Long applicantId, ErrorApplicantDTO dto);
    void delete(Long applicantId);
    ApplicantDTO fix(ErrorApplicantDTO dto, Long applicantId);
}
