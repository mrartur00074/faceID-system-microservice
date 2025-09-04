package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.service.applicant.ApplicantService;
import org.example.backend.service.applicant.Impl.ApplicantTaskServiceImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applicants")
@RequiredArgsConstructor
public class ApplicantController {

    private final ApplicantService applicantService;
    private final ApplicantTaskServiceImpl applicantTaskService;

    @GetMapping("/{applicantId}")
    public ResponseEntity<ApplicantDTO> getByApplicantId(@PathVariable Integer applicantId) {
        return ResponseEntity.ok(applicantService.getByApplicantId(applicantId));
    }

    @GetMapping
    public ResponseEntity<Page<ApplicantDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(applicantService.getAll(pageable));
    }

    @PostMapping("/add")
    public ResponseEntity<String> addApplicant(@RequestBody ApplicantDTO dto) {
        applicantTaskService.addTaskForAddApplicant(dto);
        return ResponseEntity.ok("{\"status\":\"success\",\"message\":\"Абитуриент добавлен в очередь\"}");
    }

    @PutMapping("/{applicantId}")
    public ResponseEntity<ApplicantDTO> update(@PathVariable Integer applicantId, @RequestBody ApplicantDTO dto) {
        return ResponseEntity.ok(applicantService.update(applicantId, dto));
    }

    @DeleteMapping("/{applicantId}")
    public ResponseEntity<Void> delete(@PathVariable Integer applicantId) {
        applicantService.delete(applicantId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{applicantId}/to-blacklist")
    public ResponseEntity<Void> moveToBlacklist(@PathVariable Integer applicantId) {
        applicantService.moveToBlacklist(applicantId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/delete-all")
    public ResponseEntity<String> deleteAllApplicants() {
        applicantService.deleteAll();
        return ResponseEntity.ok("Все абитуриенты успешно удалены");
    }
}
