package org.example.backend.controller;
import org.example.backend.DTO.BlackListDTO;
import org.example.backend.service.BlackListService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/blacklist")
@RequiredArgsConstructor
public class BlackListController {

    private final BlackListService service;

    @GetMapping("/{applicantId}")
    public BlackListDTO getById(@PathVariable Integer applicantId) {
        return service.getByApplicantId(applicantId);
    }

    @GetMapping
    public Page<BlackListDTO> getAll(Pageable pageable) {
        return service.getAll(pageable);
    }

    @PutMapping("/{applicantId}")
    public BlackListDTO update(@PathVariable Integer applicantId, @RequestBody BlackListDTO dto) {
        return service.update(applicantId, dto);
    }

    @DeleteMapping("/{applicantId}")
    public void delete(@PathVariable Integer applicantId) {
        service.delete(applicantId);
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restore(@PathVariable Integer id) {
        service.restoreToApplicants(id);
        return ResponseEntity.ok().build();
    }
}
