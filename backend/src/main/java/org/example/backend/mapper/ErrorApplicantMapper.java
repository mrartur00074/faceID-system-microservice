package org.example.backend.mapper;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.model.ErrorApplicant;
import org.springframework.stereotype.Component;

@Component
public class ErrorApplicantMapper {

    public ErrorApplicant fromDto(ApplicantDTO dto) {
        if (dto == null) {
            return null;
        }

        ErrorApplicant errorApplicant = new ErrorApplicant();
        errorApplicant.setApplicantId(dto.getApplicantId());
        errorApplicant.setName(dto.getName());
        errorApplicant.setSurname(dto.getSurname());
        errorApplicant.setPhoneNum(dto.getPhoneNum());
        errorApplicant.setSchool(dto.getSchool());
        errorApplicant.setAttempt(dto.getAttempt());
        errorApplicant.setStatus(dto.getStatus());
        errorApplicant.setBase64(dto.getBase64());
        errorApplicant.setEmbedding(dto.getEmbedding());

        return errorApplicant;
    }
}

