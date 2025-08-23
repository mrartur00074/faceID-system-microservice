package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.ApplicantService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository repository;
    private final ApplicantMapper mapper;
    private final BlackListRepository blacklistRepository;
    private final BlackListMapper blacklistMapper;

    @Override
    public ApplicantDTO getByApplicantId(Integer applicantId) {
        return repository.findByApplicantId(applicantId)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Поступающий не найден"));
    }

    @Override
    public Page<ApplicantDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ApplicantDTO update(Integer applicantId, ApplicantDTO dto) {
        Applicant applicant = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Поступающий не найден"));
        mapper.updateEntity(dto, applicant);
        return mapper.toDto(repository.save(applicant));
    }

    @Transactional
    @Override
    public void delete(Integer applicantId) {
        if (!repository.existsByApplicantId(applicantId)) {
            throw new ResourceNotFoundException("Поступающий с id = " + applicantId + " не найден");
        }
        repository.deleteByApplicantId(applicantId);
    }

    @Override
    @Transactional
    public void save(ApplicantDTO dto) {
        if (repository.existsByApplicantId(dto.getApplicantId())) {
            log.error("Возникла ошибка при сохранении поступающего: " + dto.getApplicantId());
            throw new RuntimeException("Поступающий с таким ID уже существует");
        }
        repository.save(mapper.toEntity(dto));
    }

    @Override
    public void moveToBlacklist(Integer applicantId) {
        Applicant applicant = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Поступающий не найден"));

        if (blacklistRepository.findByApplicantId(applicantId).isPresent()) {
            throw new RuntimeException("Поступающий уже в черном списке");
        }

        BlackList blacklist = blacklistMapper.fromApplicant(applicant);
        blacklistRepository.save(blacklist);

        repository.delete(applicant);
    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }
}
