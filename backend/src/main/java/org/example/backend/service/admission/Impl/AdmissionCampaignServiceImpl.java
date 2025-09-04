package org.example.backend.service.admission.Impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.response.AdmissionCampaignResponse;
import org.example.backend.model.AdmissionCampaign;
import org.example.backend.repository.AdmissionCampaignRepository;
import org.example.backend.service.admission.AdmissionCampaignService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdmissionCampaignServiceImpl implements AdmissionCampaignService {
    private final AdmissionCampaignRepository admissionCampaignRepository;

    public AdmissionCampaignResponse getAdmissionCampaignById(Long id) {
        AdmissionCampaign admissionCampaign = admissionCampaignRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ds"));

        return toAdmissionCampaignResponse(admissionCampaign);
    }

    private AdmissionCampaignResponse toAdmissionCampaignResponse(AdmissionCampaign admissionCampaign) {
        AdmissionCampaignResponse admissionCampaignResponse = new AdmissionCampaignResponse();
        admissionCampaignResponse.setAdmissionId(admissionCampaign.getId());
        admissionCampaignResponse.setYear(admissionCampaign.getYear());
        admissionCampaignResponse.setActive(admissionCampaign.isActive());

        return admissionCampaignResponse;
    }
}
