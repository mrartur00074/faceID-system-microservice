package org.example.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SearchResultDTO {
    private List<ApplicantDTO> applicants;
    private List<BlackListDTO> bannedApplicants;
    private int totalApplicants;

    public boolean isEmpty() {
        return applicants.isEmpty() && bannedApplicants.isEmpty();
    }
}
