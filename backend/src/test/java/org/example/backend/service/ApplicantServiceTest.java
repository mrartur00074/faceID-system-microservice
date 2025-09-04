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
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@EnableCaching
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
    private Integer existingApplicantId2;
    private Integer nonExistingApplicantId;
    private Applicant applicant;
    private ApplicantDTO applicantDTO;
    private Applicant applicant2;
    private ApplicantDTO applicantDTO2;

    @Before
    public void setUp() {
        existingApplicantId = 1;
        nonExistingApplicantId = 999;

        existingApplicantId2 = 2;

        applicant = createTestApplicant(existingApplicantId);
        applicantDTO = createTestApplicantDTO(existingApplicantId);
        applicant2 = createTestApplicant(existingApplicantId2);
        applicantDTO2 = createTestApplicantDTO(existingApplicantId2);
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

    @Test
    public void getAll_ShouldReturnPageOfApplicants() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("id"));

        List<Applicant> applicants = Arrays.asList(applicant, applicant2);
        Page<Applicant> applicantPage = new PageImpl<>(
                applicants,
                pageable,
                applicants.size()
        );

        List<ApplicantDTO> applicantDTOs = Arrays.asList(applicantDTO, applicantDTO2);
        Page<ApplicantDTO> expectedPage = new PageImpl<>(
                applicantDTOs,
                pageable,
                applicants.size()
        );

        when(repository.findAll(pageable)).thenReturn(applicantPage);
        when(mapper.toDto(applicant)).thenReturn(applicantDTO);
        when(mapper.toDto(applicant2)).thenReturn(applicantDTO2);

        Page<ApplicantDTO> result = applicantService.getAll(pageable);

        assertNotNull(result);
        assertEquals(expectedPage.getContent(), result.getContent());
        assertEquals(expectedPage.getTotalElements(), result.getTotalElements());
        assertEquals(expectedPage.getNumber(), result.getNumber());
        assertEquals(expectedPage.getSize(), result.getSize());

        verify(repository).findAll(pageable);
        verify(mapper).toDto(applicant);
        verify(mapper).toDto(applicant2);
    }

    @Test
    public void getAll_WithEmptyRepository_ShouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Applicant> emptyPage = Page.empty(pageable);

        when(repository.findAll(pageable)).thenReturn(emptyPage);

        Page<ApplicantDTO> result = applicantService.getAll(pageable);

        assertTrue(result.getContent().isEmpty());
        assertEquals(0, result.getTotalElements());
        assertEquals(0, result.getNumber());

        verify(repository).findAll(pageable);
        verify(mapper, never()).toDto(any());
    }

    @Test
    public void getAll_WhenRepositoryThrowsException_ShouldPropagateException() {
        Pageable pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenThrow(new DataAccessException("Database error") {});

        assertThrows(DataAccessException.class, () -> applicantService.getAll(pageable));

        verify(repository).findAll(pageable);
        verify(mapper, never()).toDto(any());
    }

    @Test
    public void update_WithExistingIdAndValidDto_ShouldUpdateAndReturnDto() {
        ApplicantDTO updateDto = createTestApplicantDTO(existingApplicantId);

        when(repository.findByApplicantId(existingApplicantId)).thenReturn(Optional.of(applicant));
        when(repository.save(any(Applicant.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(mapper.toDto(any(Applicant.class))).thenReturn(updateDto);

        doAnswer(invocation -> {
            ApplicantDTO dto = invocation.getArgument(0);
            Applicant entity = invocation.getArgument(1);
            return null;
        }).when(mapper).updateEntity(any(ApplicantDTO.class), any(Applicant.class));

        ApplicantDTO result = applicantService.update(existingApplicantId, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getId(), result.getId());

        verify(repository).findByApplicantId(existingApplicantId);
        verify(mapper).updateEntity(updateDto, applicant);
        verify(repository).save(applicant);
        verify(mapper).toDto(applicant);
    }

    @Test
    public void update_WithNonExistingId_ShouldThrowException() {
        when(repository.findByApplicantId(nonExistingApplicantId)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> applicantService.update(nonExistingApplicantId, applicantDTO));

        assertEquals("Поступающий не найден", exception.getMessage());

        verify(repository).findByApplicantId(nonExistingApplicantId);
        verify(mapper, never()).updateEntity(any(), any());
        verify(repository, never()).save(any());
        verify(mapper, never()).toDto(any());
    }

    @Test
    public void update_WithNullDto_ShouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> applicantService.update(existingApplicantId, null));
    }

    @Test
    public void update_WithNullId_ShouldThrowException() {
        assertThrows(RuntimeException.class,
                () -> applicantService.update(null, applicantDTO));
    }

    @Test
    public void testGetByApplicantId_ShouldNotCache_WhenResultIsNull() {
        when(repository.findByApplicantId(nonExistingApplicantId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            applicantService.getByApplicantId(nonExistingApplicantId);
        });

        assertThrows(RuntimeException.class, () -> {
            applicantService.getByApplicantId(nonExistingApplicantId);
        });

        verify(repository, times(2)).findByApplicantId(nonExistingApplicantId);
    }
}
