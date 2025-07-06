package org.example.backend.service;

import org.example.backend.DTO.SearchRequestDTO;
import org.example.backend.DTO.SearchResultDTO;

public interface SearchService {
    SearchResultDTO search(SearchRequestDTO request);
}
