package org.example.backend.service;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.mapper.BlackListMapper;
import org.example.backend.model.Applicant;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.Impl.ApplicantServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicantServiceTest {
    @Mock
    private ApplicantRepository repository;

    @Mock
    private ApplicantMapper mapper;

    @Mock
    private BlackListRepository blacklistRepository;

    @Mock
    private BlackListMapper blacklistMapper;

    @InjectMocks
    private ApplicantServiceImpl applicantService;

    private Integer existingApplicantId;
    private Integer nonExistingApplicantId;
    private Applicant applicant;
    private ApplicantDTO applicantDTO;

    @Before
    public void setUp() {
        existingApplicantId = 1;
        nonExistingApplicantId = 999;

        applicant = createTestApplicant(existingApplicantId);
        applicantDTO = createTestApplicantDTO(existingApplicantId);
    }

    private Applicant createTestApplicant(Integer id) {
        Applicant applicant = new Applicant();
        applicant.setApplicantId(id);
        return applicant;
    }

    private ApplicantDTO createTestApplicantDTO(Integer id) {
        ApplicantDTO dto = new ApplicantDTO();
        dto.setApplicantId(id);
        return dto;
    }

    @Test
    public void testGetByApplicantId_WhenApplicantExists_ShouldReturnApplicantDTO() {
        when(repository.findByApplicantId(existingApplicantId)).thenReturn(Optional.of(applicant));
        when(mapper.toDto(applicant)).thenReturn(applicantDTO);

        ApplicantDTO result = applicantService.getByApplicantId(existingApplicantId);

        assertNotNull(result);
        assertEquals(applicantDTO, result);
        assertEquals(existingApplicantId, result.getApplicantId());

        verify(repository).findByApplicantId(existingApplicantId);
        verify(mapper).toDto(applicant);
    }

    @Test(expected = RuntimeException.class)
    public void testGetByApplicantId_WhenApplicantNotFound_ShouldThrowException() {
        when(repository.findByApplicantId(nonExistingApplicantId)).thenReturn(Optional.empty());

        applicantService.getByApplicantId(nonExistingApplicantId);
    }

    @Test
    public void testGetByApplicantId_WhenApplicantNotFound_ShouldThrowExceptionWithCorrectMessage() {
        when(repository.findByApplicantId(nonExistingApplicantId)).thenReturn(Optional.empty());

        try {
            applicantService.getByApplicantId(nonExistingApplicantId);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            assertEquals("Поступающий не найден", e.getMessage());
        }
    }

    @Test
    public void testGetByApplicantId_ShouldCallRepositoryWithCorrectId() {
        Integer specificId = 123;
        Applicant specificApplicant = createTestApplicant(specificId);
        ApplicantDTO specificDto = createTestApplicantDTO(specificId);

        when(repository.findByApplicantId(specificId)).thenReturn(Optional.of(specificApplicant));
        when(mapper.toDto(specificApplicant)).thenReturn(specificDto);

        ApplicantDTO result = applicantService.getByApplicantId(specificId);

        assertEquals(specificDto.getId(), result.getId());
    }

    @Test
    public void testGetByApplicantId_WithNullId_ShouldThrowException() {
        when(repository.findByApplicantId(null)).thenReturn(Optional.empty());

        try {
            applicantService.getByApplicantId(null);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            assertEquals("Поступающий не найден", e.getMessage());
        }
    }

    @Test
    public void testGetByApplicantId_ShouldNotCallMapperWhenApplicantNotFound() {
        when(repository.findByApplicantId(nonExistingApplicantId)).thenReturn(Optional.empty());

        try {
            applicantService.getByApplicantId(nonExistingApplicantId);
            fail("Expected RuntimeException was not thrown");
        } catch (RuntimeException e) {
            verify(mapper, never()).toDto(any(Applicant.class));
        }
    }
}
