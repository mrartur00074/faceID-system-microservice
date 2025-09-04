package org.example.backend.repository;

import org.example.backend.model.AdmissionCampaign;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdmissionCampaignRepository extends JpaRepository<AdmissionCampaign, Long> {
}
