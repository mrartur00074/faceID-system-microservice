package org.example.backend.service;

import org.example.backend.kafka.message.RecognitionMessage;

public interface ApplicantVerification {
    void addApplicantWithVerification(RecognitionMessage applicant);
}
