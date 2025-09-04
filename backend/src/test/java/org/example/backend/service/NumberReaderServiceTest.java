package org.example.backend.service;

import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.NumberReaderResponse;
import org.example.backend.feign.NumberReaderFeignClient;
import org.example.backend.service.Impl.NumberReaderServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class NumberReaderServiceTest {
    @Mock
    private NumberReaderFeignClient numberReaderFeignClient;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private NumberReaderServiceImpl numberReaderService;

    private String rightImage;
    private String wrongImage;
    private final  Integer number = 100;
    private NumberReaderResponse rightResponse;
    private NumberReaderResponse wrongResponse;

    @Before
    public void setUp() {
        rightImage = "right";
        wrongImage = "wrong";
        rightResponse = new NumberReaderResponse();
        rightResponse.setNumber(number);
        wrongResponse = new NumberReaderResponse();
        wrongResponse.setNumber(null);
    }

    @Test
    public void recognizerNumber_rightImage() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity =
                ResponseEntity.ok(rightResponse);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        assertEquals(rightResponse.getNumber(), number);
        verify(numberReaderFeignClient).recognizeNumber(any(NumberReaderRequest.class));
    }

    @Test
    public void recognizerNumber_wrongImage() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity =
                ResponseEntity.ok(wrongResponse);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class))).thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(wrongImage);

        assertTrue(result.isEmpty());
        assertEquals(result, Optional.empty());
        verify(numberReaderFeignClient).recognizeNumber(any(NumberReaderRequest.class));
    }

    @Test
    public void recognizerNumber_numberInCache_shouldReturnNegativeNumber() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.ok(rightResponse);
        Cache mockCache = mock(Cache.class);
        Cache.ValueWrapper mockValueWrapper = mock(Cache.ValueWrapper.class);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);
        when(cacheManager.getCache("applicants")).thenReturn(mockCache);
        when(mockCache.get(number)).thenReturn(mockValueWrapper);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        Integer negativeNumber = number * -1;
        assertEquals(negativeNumber, result.get());
        verify(numberReaderFeignClient).recognizeNumber(any(NumberReaderRequest.class));
        verify(cacheManager).getCache("applicants");
        verify(mockCache).get(number);
    }

    @Test
    public void recognizerNumber_numberNotInCache_shouldReturnPositiveNumber() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.ok(rightResponse);
        Cache mockCache = mock(Cache.class);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);
        when(cacheManager.getCache("applicants")).thenReturn(mockCache);
        when(mockCache.get(number)).thenReturn(null);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        assertEquals(number, result.get());
        verify(cacheManager).getCache("applicants");
        verify(mockCache).get(number);
    }

    @Test
    public void recognizerNumber_cacheManagerReturnsNull_shouldReturnPositiveNumber() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.ok(rightResponse);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);
        when(cacheManager.getCache("applicants")).thenReturn(null);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        assertEquals(number, result.get()); // Должен работать без кэша
        verify(cacheManager).getCache("applicants");
    }

    @Test
    public void recognizerNumber_http404_shouldReturnEmpty() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.notFound().build();
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertFalse(result.isPresent());
    }

    @Test
    public void recognizerNumber_http500_shouldReturnEmpty() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertFalse(result.isPresent());
    }

    @Test
    public void recognizerNumber_feignClientThrowsException_shouldReturnEmpty() {
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertFalse(result.isPresent());
    }

    @Test
    public void recognizerNumber_responseBodyIsNull_shouldReturnEmpty() {
        ResponseEntity<NumberReaderResponse> mockResponseEntity =
                ResponseEntity.ok().body(null);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertFalse(result.isPresent());
    }

    @Test
    public void recognizerNumber_zeroNumber_shouldHandleCorrectly() {
        NumberReaderResponse zeroResponse = new NumberReaderResponse();
        zeroResponse.setNumber(0);
        Integer zero = 0;
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.ok(zeroResponse);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        assertEquals(zero, result.get());
    }

    @Test
    public void recognizerNumber_negativeNumber_shouldHandleCorrectly() {
        NumberReaderResponse negativeResponse = new NumberReaderResponse();
        negativeResponse.setNumber(-5);
        Integer negative = -5;
        ResponseEntity<NumberReaderResponse> mockResponseEntity = ResponseEntity.ok(negativeResponse);
        when(numberReaderFeignClient.recognizeNumber(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        Optional<Integer> result = numberReaderService.recognizerNumber(rightImage);

        assertTrue(result.isPresent());
        assertEquals(negative, result.get());
    }
}
