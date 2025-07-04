package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.model.ErrorApplicant;
import org.example.backend.repository.ErrorApplicantRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/error-applicants")
@RequiredArgsConstructor
public class ErrorApplicantController {

    private final ErrorApplicantRepository repository;

    @GetMapping
    public List<ErrorApplicant> getAllErrorApplicants() {
        System.out.println("getAllErrorApplicants");
        return repository.findAll();
    }
}
