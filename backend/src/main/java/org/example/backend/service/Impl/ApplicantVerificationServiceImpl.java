package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.kafka.message.RecognitionMessage;
import org.example.backend.model.ApplicantTask;
import org.example.backend.service.ApplicantVerification;
import org.example.backend.service.ErrorApplicantService;
import org.example.backend.util.EmbeddingUtils;
import org.example.backend.util.ImageConvertor;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApplicantVerificationServiceImpl implements ApplicantVerification {
    private final NumberReaderServiceImpl numberReaderService;
    private final ApplicantServiceImpl applicantService;
    private final ErrorApplicantService errorApplicantService;
    private final FaceRecognitionServiceImpl faceRecognitionService;
    private final ImageConvertor imageConvertor;
    private final ApplicantTaskServiceImpl applicantTaskService;
    private final ApplicantValidatorImpl applicantValidator;
    private final DuplicateFinderServiceImpl duplicateFinderService;

    @Override
    public void addApplicantWithVerification(RecognitionMessage message) {
        ApplicantDTO applicantDTO = new ApplicantDTO();
        ApplicantTask task = applicantTaskService.getApplicantTaskById(message.getTaskId())
                .orElseThrow(() -> new RuntimeException("Task not found: " + message.getTaskId()));

        try {
            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.NUMBER_RECOGNITION_IN_PROGRESS);

            String base64 = imageConvertor.convertImageToBase64(task.getImagePath());

            Integer recognizedNumber = numberReaderService.recognizerNumber(base64)
                    .orElseThrow(() -> new RuntimeException("Number not recognized: " + task.getId()));

            applicantValidator.validateApplicantId(recognizedNumber);

            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.FACE_RECOGNITION_IN_PROGRESS);

            applicantDTO.setApplicantId(recognizedNumber);
            applicantDTO.setBase64(task.getImagePath());

            log.info("applicantIdRec: " + recognizedNumber);

            float[] embeddingArray = faceRecognitionService.getEmbedding(base64);

            if (embeddingArray.length == 0) {
                throw new RuntimeException("Ошибка при распозновании лица абитуриента: " + applicantDTO.getApplicantId());
            }

            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.FACE_RECOGNITION_COMPLETED);

            String embeddingStr = EmbeddingUtils.convertEmbeddingToString(embeddingArray);
            applicantDTO.setEmbedding(embeddingStr);

            duplicateFinderService.checkForDuplicates(embeddingStr);

            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.COMPLETED);

            applicantService.save(applicantDTO);

            log.info("✅ Абитуриент успешно добавлен: " + applicantDTO.getApplicantId());
        } catch (RuntimeException e) {
            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.FAILED);
            errorApplicantService.save(applicantDTO, e.getMessage());
            throw e;
        } catch (Exception e) {
            applicantTaskService.updateStatus(task.getId(), ApplicantTask.ApplicationStatus.FAILED);
            errorApplicantService.save(applicantDTO, e.getMessage());
            throw new RuntimeException("Непредвиденная ошибка", e);
        }
    }
}
