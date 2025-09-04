package org.example.backend.service.applicant;
import org.example.backend.DTO.ApplicantDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicantService {
    ApplicantDTO getByApplicantId(Integer applicantId);
    Page<ApplicantDTO> getAll(Pageable pageable);
    ApplicantDTO update(Integer applicantId, ApplicantDTO dto);
    void delete(Integer applicantId);
    void save(ApplicantDTO dto);
    void moveToBlacklist(Integer applicantId);
    void deleteAll();
}
