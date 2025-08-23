package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.model.Applicant;
import org.example.backend.model.ApplicantTask;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.ApplicantTaskRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.ApplicantVerification;
import org.example.backend.service.ErrorApplicantService;
import org.example.backend.util.EmbeddingUtils;
import org.example.backend.util.ImageConvertor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantVerificationServiceImpl implements ApplicantVerification {
    private final ApplicantTaskRepository applicantTaskRepository;
    private final ApplicantRepository applicantRepository;
    private final NumberReaderServiceImpl numberReaderService;
    private final ApplicantServiceImpl applicantService;
    private final ErrorApplicantService errorApplicantService;
    private final BlackListRepository blackListRepository;
    private final FaceRecognitionServiceImpl faceRecognitionService;
    private final ImageConvertor imageConvertor;

    @Override
    public void addApplicantWithVerification(RecognitionMessage message) {
        ApplicantDTO applicantDTO = new ApplicantDTO();
        ApplicantTask task = applicantTaskRepository.findById(message.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found: " + message.getTaskId()));

        try {
            task.setStatus(ApplicantTask.ApplicationStatus.NUMBER_RECOGNITION_IN_PROGRESS);
            applicantTaskRepository.save(task);

            String base64 = imageConvertor.convertImageToBase64(task.getImagePath());

            Integer recognizedNumber = numberReaderService.recognizerNumber(base64)
                    .orElseThrow(() -> new RuntimeException("Number not recognized: " + task.getId()));

            applicantRepository.findByApplicantId(recognizedNumber).ifPresent(existing -> {
                throw new RuntimeException("❌ Абитуриент с applicant_id " + recognizedNumber + " уже существует. Имя: "
                        + existing.getName() + " " + existing.getSurname());
            });

            blackListRepository.findByApplicantId(recognizedNumber).ifPresent(bl -> {
                throw new RuntimeException("❌ Абитуриент находится в черном списке: applicant_id = " + recognizedNumber +
                        ", имя: " + bl.getName() + " " + bl.getSurname());
            });

            task.setStatus(ApplicantTask.ApplicationStatus.FACE_RECOGNITION_IN_PROGRESS);
            applicantTaskRepository.save(task);

            applicantDTO.setApplicantId(recognizedNumber);
            applicantDTO.setBase64(task.getImagePath());

            log.info("applicantIdRec: " + recognizedNumber);

            float[] embeddingArray = faceRecognitionService.getEmbedding(base64);

            if (embeddingArray.length == 0) {
                throw new RuntimeException("Ошибка при распозновании лица абитуриента: " + applicantDTO.getApplicantId());
            }

            task.setStatus(ApplicantTask.ApplicationStatus.FACE_RECOGNITION_COMPLETED);
            applicantTaskRepository.save(task);

            String embeddingStr = EmbeddingUtils.convertEmbeddingToString(embeddingArray);
            applicantDTO.setEmbedding(embeddingStr);

            for (Applicant existing : applicantRepository.findAll()) {
                if (existing.getEmbedding() != null && EmbeddingUtils.isSimilar(existing.getEmbedding(), applicantDTO.getEmbedding())) {
                    throw new RuntimeException("❌ Найден похожий абитуриент в базе: ID = " + existing.getApplicantId() +
                            ", имя: " + existing.getName() + " " + existing.getSurname());
                }
            }

            for (BlackList bl : blackListRepository.findAll()) {
                if (bl.getEmbedding() != null && EmbeddingUtils.isSimilar(bl.getEmbedding(), applicantDTO.getEmbedding())) {
                    throw new RuntimeException("❌ Найден похожий абитуриент в черном списке: ID = " + bl.getApplicantId() +
                            ", имя: " + bl.getName() + " " + bl.getSurname());
                }
            }

            task.setStatus(ApplicantTask.ApplicationStatus.COMPLETED);
            applicantTaskRepository.save(task);

            applicantService.save(applicantDTO);

            log.info("✅ Абитуриент успешно добавлен: " + applicantDTO.getApplicantId());
        } catch (RuntimeException e) {
            task.setStatus(ApplicantTask.ApplicationStatus.FAILED);
            applicantTaskRepository.save(task);
            errorApplicantService.save(applicantDTO, e.getMessage());
            throw e;
        } catch (Exception e) {
            task.setStatus(ApplicantTask.ApplicationStatus.FAILED);
            applicantTaskRepository.save(task);
            errorApplicantService.save(applicantDTO, e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка", e);
        }
    }
}
