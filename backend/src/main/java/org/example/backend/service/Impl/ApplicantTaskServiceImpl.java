package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.exception.BusinessException;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.kafka.producer.KafkaProducerService;
import org.example.backend.model.ApplicantTask;
import org.example.backend.repository.ApplicantTaskRepository;
import org.example.backend.service.ApplicantTaskService;
import org.example.backend.util.ImageConvertor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantTaskServiceImpl implements ApplicantTaskService {
    private final ApplicantTaskRepository applicantTaskRepository;
    private final KafkaProducerService kafkaProducerService;
    private final ImageConvertor imageConvertor;

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

            String imagePath = imageConvertor.saveImageToFile(base64Image);
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
}
