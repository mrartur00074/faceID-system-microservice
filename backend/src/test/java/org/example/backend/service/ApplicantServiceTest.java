package org.example.backend.service;

import org.example.backend.DTO.ApplicantDTO;
import org.example.backend.TestImageHelper;
import org.example.backend.mapper.ApplicantMapper;
import org.example.backend.model.Applicant;
import org.example.backend.model.BlackList;
import org.example.backend.repository.ApplicantRepository;
import org.example.backend.repository.BlackListRepository;
import org.example.backend.service.Impl.ApplicantServiceImpl;
import org.example.backend.service.Impl.ErrorApplicantServiceImpl;
import org.example.backend.service.Impl.ExternalRecognitionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ApplicantServiceTest {
    @Mock
    private ExternalRecognitionServiceImpl recognitionService;
    @Mock
    private ApplicantRepository applicantRepository;
    @Mock
    private BlackListRepository blackListRepository;
    @Mock
    private ErrorApplicantServiceImpl errorApplicantService;
    @Mock
    private ApplicantMapper mapper;

    @InjectMocks
    private ApplicantServiceImpl applicantService;

    private String testImage1;
    private String testImage2;
    private float[] testEmbedding;

    @Before
    public void setUp() throws Exception {
        testImage1 = TestImageHelper.getSamePerson1Base64();
        testImage2 = TestImageHelper.getSamePerson2Base64();

        when(mapper.toEntity(any(ApplicantDTO.class))).thenAnswer(invocation -> {
            ApplicantDTO dto = invocation.getArgument(0);
            Applicant entity = new Applicant();
            entity.setApplicantId(dto.getApplicantId());
            entity.setName(dto.getName());
            entity.setSurname(dto.getSurname());
            entity.setPhoneNum(dto.getPhoneNum());
            entity.setBase64(dto.getBase64());
            entity.setEmbedding(dto.getEmbedding());
            return entity;
        });

        testEmbedding = new float[512];
        Arrays.fill(testEmbedding, 0.5f);
        when(recognitionService.getEmbedding(anyString())).thenReturn(testEmbedding);

        when(applicantRepository.save(any(Applicant.class))).thenAnswer(invocation -> invocation.getArgument(0));
    }

    // 1. Тест на отсутствие фото
    @Test(expected = RuntimeException.class)
    public void shouldThrowWhenNoPhotoProvided() {
        ApplicantDTO dto = createTestDto(null, 1077);
        applicantService.addApplicantWithVerification(dto);

        verify(errorApplicantService).save(any(ApplicantDTO.class), contains("Не удалось распознать номер"));
    }

    // 2. Тест на распознавание номера
    @Test
    public void shouldRecognizeApplicantIdFromPhoto() {
        when(recognitionService.recognizeApplicantId(testImage1)).thenReturn(1077);

        ApplicantDTO dto = createTestDto(testImage1, null);
        applicantService.addApplicantWithVerification(dto);

        assertEquals(Integer.valueOf(1077), dto.getApplicantId());
        verify(recognitionService).recognizeApplicantId(testImage1);
    }

    // 3. Тест на существующий номер
    @Test(expected = RuntimeException.class)
    public void shouldThrowWhenApplicantIdExists() {
        when(recognitionService.recognizeApplicantId(testImage1)).thenReturn(1077);
        when(applicantRepository.findByApplicantId(1077)).thenReturn(Optional.of(new Applicant()));

        ApplicantDTO dto = createTestDto(testImage1, 1077);
        applicantService.addApplicantWithVerification(dto);

        verify(errorApplicantService).save(any(ApplicantDTO.class), contains("уже существует"));
    }

    // 4. Тест на обнаружение лица
    @Test
    public void shouldDetectFaceAndGenerateEmbedding() {
        when(recognitionService.recognizeApplicantId(testImage1)).thenReturn(1077);

        ApplicantDTO dto = createTestDto(testImage1, 1077);
        applicantService.addApplicantWithVerification(dto);

        assertNotNull(dto.getEmbedding());
        verify(recognitionService).getEmbedding(testImage1);
    }

    // 5. Тест на обнаружение похожего абитуриента
    @Test(expected = RuntimeException.class)
    public void shouldDetectSimilarApplicant() {
        when(applicantRepository.findAll()).thenReturn(Collections.singletonList(createTestApplicant(1077, testEmbedding)));

        when(recognitionService.recognizeApplicantId(testImage2)).thenReturn(1045);

        ApplicantDTO dto = createTestDto(testImage2, 1045);
        applicantService.addApplicantWithVerification(dto);

        verify(errorApplicantService).save(any(ApplicantDTO.class), contains("Найден похожий абитуриент"));
    }

    // 6. Тест на обнаружение в черном списке
    @Test(expected = RuntimeException.class)
    public void shouldDetectBlacklistedApplicant() {
        BlackList blacklisted = new BlackList();
        blacklisted.setApplicantId(1077);
        blacklisted.setEmbedding(Arrays.toString(testEmbedding));
        when(blackListRepository.findAll()).thenReturn(Collections.singletonList(blacklisted));

        when(recognitionService.recognizeApplicantId(testImage1)).thenReturn(1045);

        ApplicantDTO dto = createTestDto(testImage1, 1045);
        applicantService.addApplicantWithVerification(dto);

        verify(errorApplicantService).save(any(ApplicantDTO.class), contains("черном списке"));
    }

    // 7. Тест на успешное добавление
    @Test
    public void shouldSuccessfullyAddApplicant() {
        when(recognitionService.recognizeApplicantId(testImage1)).thenReturn(1077);

        ApplicantDTO dto = createTestDto(testImage1, 1077);
        applicantService.addApplicantWithVerification(dto);

        verify(applicantRepository).save(any(Applicant.class));
        verify(errorApplicantService, never()).save(any(), anyString());
    }

    private ApplicantDTO createTestDto(String image, Integer applicantId) {
        ApplicantDTO dto = new ApplicantDTO();
        dto.setBase64(image);
        dto.setApplicantId(applicantId);
        dto.setName("Test");
        dto.setSurname("User");
        dto.setPhoneNum("+79990001122");
        return dto;
    }

    private Applicant createTestApplicant(int id, float[] embedding) {
        Applicant applicant = new Applicant();
        applicant.setApplicantId(id);
        applicant.setEmbedding(Arrays.toString(embedding));
        return applicant;
    }
}
