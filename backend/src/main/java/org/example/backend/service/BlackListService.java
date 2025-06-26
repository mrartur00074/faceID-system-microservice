package org.example.backend.service;
import org.example.backend.DTO.BlackListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlackListService {
    BlackListDTO getByApplicantId(Integer applicantId);
    Page<BlackListDTO> getAll(Pageable pageable);
    BlackListDTO update(Integer applicantId, BlackListDTO dto);
    void delete(Integer applicantId);
    void restoreToApplicants(Integer applicantId);
}
