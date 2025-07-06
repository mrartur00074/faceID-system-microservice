package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.DTO.SearchRequestDTO;
import org.example.backend.DTO.SearchResultDTO;
import org.example.backend.service.Impl.SearchServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/search")
public class SearchController {
    private final SearchServiceImpl searchService;

    @PostMapping
    public ResponseEntity<?> search(@RequestBody SearchRequestDTO request) {
        try {
            SearchResultDTO result = searchService.search(request);

            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Ошибка поиска: " + e.getMessage());
        }
    }
}
