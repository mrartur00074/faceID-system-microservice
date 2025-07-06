package org.example.backend.mapper;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.DTO.BlackListDTO;
import org.example.backend.DTO.ErrorApplicantDTO;
import org.example.backend.model.ErrorApplicant;
import org.springframework.stereotype.Component;

@Component
public class ErrorApplicantMapper {

    public ErrorApplicant fromDto(ApplicantDTO dto, String error) {
        if (dto == null) {
            return null;
        }

        ErrorApplicant errorApplicant = new ErrorApplicant();
        errorApplicant.setApplicantId(dto.getApplicantId());
        errorApplicant.setError(error);
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

    public ErrorApplicantDTO toDto(ErrorApplicant entity) {
        if (entity == null) {
            return null;
        }

        ErrorApplicantDTO dto = new ErrorApplicantDTO();
        dto.setId(entity.getId());
        dto.setApplicantId(entity.getApplicantId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setPhoneNum(entity.getPhoneNum());
        dto.setSchool(entity.getSchool());
        dto.setError(entity.getError());
        dto.setAttempt(entity.getAttempt());
        dto.setStatus(entity.getStatus());
        dto.setBase64(entity.getBase64());
        dto.setEmbedding(entity.getEmbedding());

        return dto;
    }

    public ApplicantDTO toApplicantDto(ErrorApplicantDTO errorApplicant) {
        ApplicantDTO applicantDTO = new ApplicantDTO();
        applicantDTO.setApplicantId(errorApplicant.getApplicantId());
        applicantDTO.setName(errorApplicant.getName());
        applicantDTO.setSurname(errorApplicant.getSurname());
        applicantDTO.setPhoneNum(errorApplicant.getPhoneNum());
        applicantDTO.setSchool(errorApplicant.getSchool());
        applicantDTO.setAttempt(errorApplicant.getAttempt());
        applicantDTO.setStatus(errorApplicant.getStatus());
        applicantDTO.setBase64(errorApplicant.getBase64());
        applicantDTO.setEmbedding(errorApplicant.getEmbedding());
        return applicantDTO;
    }

    public BlackListDTO toBlackListDto(ErrorApplicantDTO errorApplicant) {
        BlackListDTO blackListDTO = new BlackListDTO();
        blackListDTO.setApplicantId(errorApplicant.getApplicantId());
        blackListDTO.setName(errorApplicant.getName());
        blackListDTO.setSurname(errorApplicant.getSurname());
        blackListDTO.setPhoneNum(errorApplicant.getPhoneNum());
        blackListDTO.setSchool(errorApplicant.getSchool());
        blackListDTO.setAttempt(errorApplicant.getAttempt());
        blackListDTO.setStatus(errorApplicant.getStatus());
        blackListDTO.setBase64(errorApplicant.getBase64());
        blackListDTO.setEmbedding(errorApplicant.getEmbedding());
        return blackListDTO;
    }

    public void updateEntity(ErrorApplicantDTO dto, ErrorApplicant entity) {
        if (dto == null || entity == null) {
            return;
        }

        entity.setApplicantId(dto.getApplicantId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhoneNum(dto.getPhoneNum());
        entity.setSchool(dto.getSchool());
        entity.setAttempt(dto.getAttempt());
        entity.setStatus(dto.getStatus());
        entity.setBase64(dto.getBase64());
        entity.setEmbedding(dto.getEmbedding());
    }
}

