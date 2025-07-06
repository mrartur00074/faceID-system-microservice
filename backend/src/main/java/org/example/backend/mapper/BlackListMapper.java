package org.example.backend.mapper;

import org.example.backend.DTO.BlackListDTO;
import org.example.backend.model.BlackList;
import org.example.backend.model.Applicant;
import org.springframework.stereotype.Component;

@Component
public class BlackListMapper {

    public BlackList toEntity(BlackListDTO dto) {
        if (dto == null) {
            return null;
        }

        BlackList entity = new BlackList();

        entity.setId(dto.getId());
        entity.setApplicantId(dto.getApplicantId());
        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhoneNum(dto.getPhoneNum());
        entity.setSchool(dto.getSchool());
        entity.setBase64(dto.getBase64());
        entity.setEmbedding(dto.getEmbedding());

        return entity;
    }

    public BlackListDTO toDto(BlackList entity) {
        if (entity == null) {
            return null;
        }

        BlackListDTO dto = new BlackListDTO();

        dto.setId(entity.getId());
        dto.setApplicantId(entity.getApplicantId());
        dto.setName(entity.getName());
        dto.setSurname(entity.getSurname());
        dto.setPhoneNum(entity.getPhoneNum());
        dto.setSchool(entity.getSchool());
        dto.setBase64(entity.getBase64());
        dto.setEmbedding(entity.getEmbedding());

        return dto;
    }

    public BlackList fromApplicant(Applicant applicant) {
        if (applicant == null) {
            return null;
        }

        BlackList blackList = new BlackList();

        blackList.setApplicantId(applicant.getApplicantId());
        blackList.setName(applicant.getName());
        blackList.setSurname(applicant.getSurname());
        blackList.setPhoneNum(applicant.getPhoneNum());
        blackList.setSchool(applicant.getSchool());
        blackList.setBase64(applicant.getBase64());
        blackList.setEmbedding(applicant.getEmbedding());
        blackList.setCreatedAt(applicant.getCreatedAt());

        return blackList;
    }
}