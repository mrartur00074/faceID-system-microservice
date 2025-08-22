package org.example.backend.service;

import java.util.Optional;

public interface NumberReaderService {
    Optional<Integer> recognizerNumber(String base64);
}
