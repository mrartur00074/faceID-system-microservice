package org.example.backend.service.Impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.*;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.ExternalRecognitionService;
import org.example.backend.service.SearchService;
import org.example.backend.util.EmbeddingUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {
    private final ApplicantRepository applicantRepository;
    private final BlackListRepository blackListRepository;
    private final ExternalRecognitionService recognitionService;
    private final BlackListMapper blackListMapper;
    private final ApplicantMapper applicantMapper;

    @Override
    public SearchResultDTO search(SearchRequestDTO request) {
        if (request.getBase64Image() != null) {
            return searchByImage(request.getBase64Image());
        }

        if (request.getApplicantId() != null) {
            return searchById(request.getApplicantId());
        }

        throw new RuntimeException("Необходимо указать либо ID абитуриента, либо фото");
    }

    private SearchResultDTO searchByImage(String base64Image) {
        try {
            float[] targetEmbedding = recognitionService.getEmbedding(base64Image);
            String targetEmbeddingStr = EmbeddingUtils.convertEmbeddingToString(targetEmbedding);

            List<ApplicantDTO> applicants = applicantRepository.findAll().stream()
                    .filter(applicant -> applicant.getEmbedding() != null &&
                            EmbeddingUtils.isSimilar(applicant.getEmbedding(), targetEmbeddingStr))
                    .map(applicantMapper::toDto)
                    .collect(Collectors.toList());

            List<BlackListDTO> bannedApplicants = blackListRepository.findAll().stream()
                    .filter(banned -> banned.getEmbedding() != null &&
                            EmbeddingUtils.isSimilar(banned.getEmbedding(), targetEmbeddingStr))
                    .map(blackListMapper::toDto)
                    .collect(Collectors.toList());

            return new SearchResultDTO(applicants, bannedApplicants, applicants.size() + bannedApplicants.size());
        } catch (Exception e) {
            throw new RuntimeException("Ошибка поиска по фото: " + e.getMessage(), e);
        }
    }

    private SearchResultDTO searchById(Integer applicantId) {
        Optional<Applicant> applicant = applicantRepository.findByApplicantId(applicantId);

        Optional<BlackList> bannedApplicant = blackListRepository.findByApplicantId(applicantId);

        List<ApplicantDTO> applicants = applicant.map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(applicantMapper::toDto)
                .collect(Collectors.toList());

        List<BlackListDTO> bannedApplicants = bannedApplicant.map(List::of)
                .orElseGet(Collections::emptyList)
                .stream()
                .map(blackListMapper::toDto)
                .collect(Collectors.toList());

        return new SearchResultDTO(applicants, bannedApplicants, applicants.size() + bannedApplicants.size());
    }
}
