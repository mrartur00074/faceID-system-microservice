package org.example.backend.service.applicant.Impl;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.DTO.ErrorApplicantDTO;
import org.example.backend.exception.BusinessException;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.ErrorApplicantMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.ErrorApplicant;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.ErrorApplicantRepository;
import org.example.backend.service.applicant.ErrorApplicantService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ErrorApplicantServiceImpl implements ErrorApplicantService {

    private final ErrorApplicantRepository errorApplicantRepository;
    private final ErrorApplicantMapper errorApplicantMapper;
    private final ApplicantRepository applicantRepository;
    private final ApplicantMapper applicantMapper;

    public void save(ApplicantDTO dto, String error) {
        try {
            Objects.requireNonNull(dto, "ApplicantDTO не может быть null");
            Objects.requireNonNull(error, "Error сообщение не может быть null");

            ErrorApplicant e = errorApplicantMapper.fromDto(dto, error);

            ErrorApplicant saved = errorApplicantRepository.save(e);

        } catch (Exception e) {
            throw new RuntimeException("Ошибка в сохранении ошибочного абитуриента: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ErrorApplicantDTO> getAll() {
        return errorApplicantRepository.findAll()
                .stream()
                .map(errorApplicantMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ErrorApplicantDTO getById(Long id) {
        return errorApplicantRepository.findById(id)
                .map(errorApplicantMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Абитуриент не найден"));
    }

    @Override
    public ErrorApplicantDTO update(Long id, ErrorApplicantDTO dto) {
        ErrorApplicant entity = errorApplicantRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Абитуреннт не найдент с id = : " + id));

        errorApplicantMapper.updateEntity(dto, entity);
        ErrorApplicant updatedEntity = errorApplicantRepository.save(entity);

        return errorApplicantMapper.toDto(updatedEntity);
    }

    @Transactional
    @Override
    public void delete(Long id) {
        if (!errorApplicantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Абитуреннт не найдент с id = : " + id);
        }

        errorApplicantRepository.deleteById(id);
    }

    @Override
    @Transactional
    public ApplicantDTO fix(ErrorApplicantDTO dto, Long applicantId) {
        try {
            if (dto == null) {
                throw new IllegalArgumentException("ErrorApplicantDTO cannot be null");
            }

            if (dto.getApplicantId() == null) {
                throw new ValidationException("Applicant ID must not be null");
            }

            boolean existsInMainTable = applicantRepository.existsByApplicantId(dto.getApplicantId());
            if (existsInMainTable) {
                throw new DuplicateKeyException("Applicant with ID " + dto.getApplicantId() + " already exists in main table");
            }

            ApplicantDTO applicantDto = errorApplicantMapper.toApplicantDto(dto);

            Applicant savedApplicant = applicantRepository.save(applicantMapper.toEntity(applicantDto));

            errorApplicantRepository.deleteById(applicantId);
            return applicantMapper.toDto(savedApplicant);

        } catch (ResourceNotFoundException e) {
            throw new BusinessException("Failed to fix applicant: " + e.getMessage(), e);
        } catch (DuplicateKeyException | ValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException("Unexpected error occurred while fixing applicant", e);
        }
    }
}
