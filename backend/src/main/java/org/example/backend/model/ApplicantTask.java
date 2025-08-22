package org.example.backend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "tasks_applicant")
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private ApplicationStatus status;
    private String imagePath;
    private String recognizedNumber;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public enum ApplicationStatus {
        PENDING,                    // Ожидает обработки
        COMPLETED,                       // Оба этапа завершены
        NUMBER_RECOGNITION_IN_PROGRESS,  // Распознавание номера в процессе
        NUMBER_RECOGNITION_COMPLETED,    // Номер распознан успешно
        NUMBER_RECOGNITION_FAILED,       // Ошибка распознавания номера
        FACE_RECOGNITION_IN_PROGRESS,    // Распознавание лица в процессе
        FACE_RECOGNITION_COMPLETED,      // Лицо распознано успешно
        FACE_RECOGNITION_FAILED,         // Ошибка распознавания лица
        FAILED                           // Общая ошибка
    }
}



