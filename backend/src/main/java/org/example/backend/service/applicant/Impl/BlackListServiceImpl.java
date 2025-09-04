package org.example.backend.service.applicant.Impl;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.backend.DTO.BlackListDTO;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import lombok.RequiredArgsConstructor;
import org.example.backend.service.applicant.BlackListService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class BlackListServiceImpl implements BlackListService {
    private final BlackListRepository repository;
    private final BlackListMapper mapper;
    private final ApplicantRepository applicantRepository;
    private final ApplicantMapper applicantMapper;

    @Override
    public BlackListDTO getByApplicantId(Integer applicantId) {
        return mapper.toDto(
                repository.findByApplicantId(applicantId)
                        .orElseThrow(() -> new NoSuchElementException("Абитуриент не обнаружен в черном списке"))
        );
    }

    @Override
    public Page<BlackListDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional
    @Override
    public BlackListDTO update(Integer applicantId, BlackListDTO dto) {
        BlackList entity = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new NoSuchElementException("Абитуриент не обнаружен в черном списке"));

        entity.setName(dto.getName());
        entity.setSurname(dto.getSurname());
        entity.setPhoneNum(dto.getPhoneNum());
        entity.setSchool(dto.getSchool());
        entity.setBase64(dto.getBase64());

        return mapper.toDto(repository.save(entity));
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
    public void restoreToApplicants(Integer applicantId) {
        BlackList blacklist = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Не найден"));

        if (applicantRepository.findByApplicantId(applicantId).isPresent()) {
            throw new RuntimeException("Уже есть в абитуриентах");
        }

        Applicant applicant = applicantMapper.fromBlacklist(blacklist);
        applicant.setId(null);
        applicantRepository.save(applicant);
        repository.delete(blacklist);
    }
}
