package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.ApplicantValidator;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApplicantValidatorImpl implements ApplicantValidator {
    private final ApplicantRepository applicantRepository;
    private final BlackListRepository blackListRepository;

    @Override
    public void validateApplicantId(Integer applicantId) {
        applicantRepository.findByApplicantId(applicantId).ifPresent(existing -> {
            throw new RuntimeException("❌ Абитуриент с applicant_id " + applicantId + " уже существует. Имя: "
                    + existing.getName() + " " + existing.getSurname());
        });

        blackListRepository.findByApplicantId(applicantId).ifPresent(bl -> {
            throw new RuntimeException("❌ Абитуриент находится в черном списке: applicant_id = " + applicantId +
                    ", имя: " + bl.getName() + " " + bl.getSurname());
        });
    }
}
