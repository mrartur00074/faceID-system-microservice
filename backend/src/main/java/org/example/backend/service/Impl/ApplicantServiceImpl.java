package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.exception.BusinessException;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.kafka.producer.KafkaProducerService;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.ApplicantTask;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.ApplicantTaskRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.ApplicantService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicantServiceImpl implements ApplicantService {
    private final ApplicantRepository repository;
    private final ApplicantMapper mapper;
    private final BlackListRepository blacklistRepository;
    private final BlackListMapper blacklistMapper;
    private final ApplicantTaskRepository applicantTaskRepository;
    private final KafkaProducerService kafkaProducerService;

    private static final String IMAGE_DIR = "/app/images";
    private static final String IMAGE_DB_DIR = "images";

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

    @Override
    @Transactional
    public RecognitionMessage addTaskForAddApplicant(ApplicantDTO dto) {
        try {
            if (dto == null) {
                throw new IllegalArgumentException("ApplicantDTO не может быть пустым");
            }

            String base64Image = dto.getBase64();

            if (base64Image == null || base64Image.isEmpty()) {
                throw new IllegalArgumentException("Изображение не пришло");
            }

            String imagePath = saveImageToFile(base64Image);
            dto.setBase64(imagePath);

            ApplicantTask task = new ApplicantTask();
            task.setStatus(ApplicantTask.ApplicationStatus.PENDING);
            task.setImagePath(imagePath);
            task.setCreatedAt(LocalDateTime.now());
            applicantTaskRepository.save(task);

            kafkaProducerService.sendRecognitionRequest(task.getId());

            return new RecognitionMessage(task.getId());
        } catch (Exception e) {
            throw new BusinessException("Произошла ошибка при добавлении абитуриента", e);
        }
    }

    private String saveImageToFile(String base64Image) {
        try {
            String folderPath = IMAGE_DIR;
            Files.createDirectories(Paths.get(folderPath));

            String uniqueName = "student_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".png";
            String filePath = folderPath + File.separator + uniqueName;

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(filePath), imageBytes);

            filePath = IMAGE_DB_DIR + File.separator + uniqueName;

            return filePath;
        } catch (IOException e) {
            throw new RuntimeException("Ошибка при сохранении изображения: " + e.getMessage(), e);
        }
    }

    public Resource loadImageAsResource(String filename) {
        try {
            Path path = Paths.get(IMAGE_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(path.toUri());
            if (resource.exists()) return resource;
            else throw new RuntimeException("Файл не найден: " + filename);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении изображения", e);
        }
    }
}
