package org.example.backend.DTO;

import lombok.Data;

@Data
public class SearchRequestDTO {
    private Integer applicantId;
    private String base64Image;
}
