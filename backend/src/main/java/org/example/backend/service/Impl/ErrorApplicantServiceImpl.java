package org.example.backend.service.Impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.mapper.ErrorApplicantMapper;
import org.example.backend.model.ErrorApplicant;
import org.example.backend.repository.ErrorApplicantRepository;
import org.example.backend.service.ErrorApplicantService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ErrorApplicantServiceImpl implements ErrorApplicantService {

        private final ErrorApplicantRepository repo;
        private final ErrorApplicantMapper mapper;

        public void save(ApplicantDTO dto, String error) {
            try {
                ErrorApplicant e = mapper.fromDto(dto);
                e.setError(error);
                e.setCreatedAt(LocalDateTime.now());
                repo.save(e);
                System.out.println("❗ Сохраняем ошибочного абитуриента: " + dto.getApplicantId() + " | Ошибка: " + error);
            } catch (Exception e) {
                throw new RuntimeException("Ошибка в сохранении абитуриента", e);
            }
        }
}
