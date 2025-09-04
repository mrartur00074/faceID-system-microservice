package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.DTO.ErrorApplicantDTO;
import org.example.backend.service.applicant.ErrorApplicantService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/error-applicants")
@RequiredArgsConstructor
public class ErrorApplicantController {
    private final ErrorApplicantService errorApplicantService;

    @GetMapping
    public ResponseEntity<List<ErrorApplicantDTO>> getAllErrorApplicants() {
        List<ErrorApplicantDTO> errorApplicants = errorApplicantService.getAll();
        return ResponseEntity.ok(errorApplicants);
    }

    @GetMapping("/{applicantId}")
    public ResponseEntity<ErrorApplicantDTO> getErrorApplicantById(@PathVariable Long applicantId) {
        ErrorApplicantDTO errorApplicant = errorApplicantService.getById(applicantId);
        return ResponseEntity.ok(errorApplicant);
    }

    @PutMapping("/{applicantId}")
    public ResponseEntity<ErrorApplicantDTO> update(
            @PathVariable Long applicantId,
            @RequestBody ErrorApplicantDTO dto) {
        ErrorApplicantDTO updatedDto = errorApplicantService.update(applicantId, dto);
        return ResponseEntity.ok(updatedDto);
    }

    @DeleteMapping("/{applicantId}")
    public ResponseEntity<Void> delete(@PathVariable Long applicantId) {
        errorApplicantService.delete(applicantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{applicantId}/fix")
    public ResponseEntity<ApplicantDTO> fix(
            @PathVariable Long applicantId,
            @RequestBody ErrorApplicantDTO dto) {
        ApplicantDTO applicantDTO = errorApplicantService.fix(dto, applicantId);
        return ResponseEntity.ok(applicantDTO);
    }
}
