package org.example.backend.mapper;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.springframework.stereotype.Component;

@Component
public class ApplicantMapper {

    public Applicant toEntity(ApplicantDTO dto) {
        if (dto == null) {
            return null;
        }

        Applicant entity = new Applicant();

        entity.setApplicantId(dto.getApplicantId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhoneNum(dto.getPhoneNum());
        entity.setSchool(dto.getSchool());
        entity.setAttempt(dto.getAttempt());
        entity.setStatus(dto.getStatus());
        entity.setBase64(dto.getBase64());
        entity.setEmbedding(dto.getEmbedding());

        return entity;
    }

    public ApplicantDTO toDto(Applicant entity) {
        if (entity == null) {
            return null;
        }

        ApplicantDTO dto = new ApplicantDTO();
        dto.setId(entity.getId());
        dto.setApplicantId(entity.getApplicantId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setPhoneNum(entity.getPhoneNum());
        dto.setSchool(entity.getSchool());
        dto.setAttempt(entity.getAttempt());
        dto.setStatus(entity.getStatus());
        dto.setBase64(entity.getBase64());
        dto.setEmbedding(entity.getEmbedding());

        return dto;
    }

    public void updateEntity(ApplicantDTO dto, Applicant entity) {
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

    public Applicant fromBlacklist(BlackList blacklist) {
        if (blacklist == null) {
            return null;
        }

        Applicant applicant = new Applicant();
        applicant.setId(blacklist.getId());
        applicant.setApplicantId(blacklist.getApplicantId());
        applicant.setName(blacklist.getName());
        applicant.setSurname(blacklist.getSurname());
        applicant.setPhoneNum(blacklist.getPhoneNum());
        applicant.setSchool(blacklist.getSchool());
        applicant.setBase64(blacklist.getBase64());
        applicant.setEmbedding(blacklist.getEmbedding());
        applicant.setCreatedAt(blacklist.getCreatedAt());

        return applicant;
    }
}