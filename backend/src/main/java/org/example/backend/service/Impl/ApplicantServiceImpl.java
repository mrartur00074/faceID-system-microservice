package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.mapper.ErrorApplicantMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.model.ErrorApplicant;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.repository.ErrorApplicantRepository;
import org.example.backend.service.ApplicantService;
import org.example.backend.service.ErrorApplicantService;
import org.example.backend.util.EmbeddingUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ApplicantServiceImpl implements ApplicantService {

    private final ApplicantRepository repository;
    private final ApplicantMapper mapper;
    private final BlackListRepository blacklistRepository;
    private final BlackListMapper blacklistMapper;
    private final ExternalRecognitionServiceImpl externalRecognitionService;
    private final ErrorApplicantServiceImpl errorApplicantService;

    private static final String IMAGE_DIR = "images";

    @Override
    public ApplicantDTO getByApplicantId(Integer applicantId) {
        return repository.findByApplicantId(applicantId)
                .map(mapper::toDto)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
    }

    @Override
    public Page<ApplicantDTO> getAll(Pageable pageable) {
        return repository.findAll(pageable).map(mapper::toDto);
    }

    @Override
    @Transactional
    public ApplicantDTO update(Integer applicantId, ApplicantDTO dto) {
        Applicant applicant = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));
        mapper.updateEntity(dto, applicant);
        return mapper.toDto(repository.save(applicant));
    }

    @Override
    public void delete(Integer applicantId) {
        repository.deleteByApplicantId(applicantId);
    }

    @Override
    public void save(ApplicantDTO dto) {
        if (repository.existsByApplicantId(dto.getApplicantId())) {
            throw new RuntimeException("Applicant with given ID already exists");
        }
        repository.save(mapper.toEntity(dto));
    }

    @Override
    public void moveToBlacklist(Integer applicantId) {
        Applicant applicant = repository.findByApplicantId(applicantId)
                .orElseThrow(() -> new RuntimeException("Applicant not found"));

        if (blacklistRepository.findByApplicantId(applicantId).isPresent()) {
            throw new RuntimeException("Already in blacklist");
        }

        BlackList blacklist = blacklistMapper.fromApplicant(applicant);
        blacklistRepository.save(blacklist);

        repository.delete(applicant);
    }

    @Override
    public void addApplicantWithVerification(ApplicantDTO dto) {
        try {
            String base64Image = dto.getBase64();
            Integer applicantId = dto.getApplicantId();

            System.out.println("===> Начало добавления абитуриента. Входной applicant_id: " + applicantId);

            String imagePath = saveImageToFile(base64Image);
            dto.setBase64(imagePath);

            Integer applicantIdRec = externalRecognitionService.recognizeApplicantId(base64Image);

            if (applicantId == null && applicantIdRec == null) {
                throw new RuntimeException("❌ Не удалось распознать номер с изображения. Данные DTO: " + dto);
            }

            if (applicantIdRec != null) {
                applicantId = applicantIdRec;
                dto.setApplicantId(applicantId);
            }

            if (applicantId == null || applicantId <= 0) {
                throw new RuntimeException("❌ Некорректный applicant_id: " + applicantId + ". DTO: " + dto);
            }

            final int finalApplicantId = applicantId;

            repository.findByApplicantId(applicantId).ifPresent(existing -> {
                throw new RuntimeException("❌ Абитуриент с applicant_id " + finalApplicantId + " уже существует. Имя: "
                        + existing.getName() + " " + existing.getSurname());
            });

            blacklistRepository.findByApplicantId(applicantId).ifPresent(bl -> {
                throw new RuntimeException("❌ Абитуриент находится в черном списке: applicant_id = " + finalApplicantId +
                        ", имя: " + bl.getName() + " " + bl.getSurname());
            });

            float[] embeddingArray = externalRecognitionService.getEmbedding(base64Image);
            String embeddingStr = IntStream.range(0, embeddingArray.length)
                    .mapToObj(i -> Float.toString(embeddingArray[i]))
                    .collect(Collectors.joining(","));
            dto.setEmbedding(embeddingStr);

            for (Applicant existing : repository.findAll()) {
                if (existing.getEmbedding() != null && EmbeddingUtils.isSimilar(existing.getEmbedding(), dto.getEmbedding())) {
                    throw new RuntimeException("❌ Найден похожий абитуриент в базе: ID = " + existing.getApplicantId() +
                            ", имя: " + existing.getName() + " " + existing.getSurname());
                }
            }

            for (BlackList bl : blacklistRepository.findAll()) {
                if (bl.getEmbedding() != null && EmbeddingUtils.isSimilar(bl.getEmbedding(), dto.getEmbedding())) {
                    throw new RuntimeException("❌ Найден похожий абитуриент в черном списке: ID = " + bl.getApplicantId() +
                            ", имя: " + bl.getName() + " " + bl.getSurname());
                }
            }

            Applicant entity = mapper.toEntity(dto);
            entity.setCreatedAt(LocalDateTime.now());
            repository.save(entity);

            System.out.println("✅ Абитуриент успешно добавлен: " + applicantId);
        } catch (RuntimeException e) {
            errorApplicantService.save(dto, e.getMessage());
            throw e;
        } catch (Exception e) {
            errorApplicantService.save(dto, e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка", e);
        }

    }

    @Override
    public void deleteAll() {
        repository.deleteAll();
    }

    private String saveImageToFile(String base64Image) {
        try {
            String folderPath = IMAGE_DIR;
            Files.createDirectories(Paths.get(folderPath));

            String uniqueName = "student_" + System.currentTimeMillis() + "_" + UUID.randomUUID() + ".png";
            String filePath = folderPath + File.separator + uniqueName;

            byte[] imageBytes = Base64.getDecoder().decode(base64Image);
            Files.write(Paths.get(filePath), imageBytes);

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
