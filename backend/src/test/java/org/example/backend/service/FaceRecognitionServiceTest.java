package org.example.backend.service;

import org.example.backend.DTO.request.NumberReaderRequest;
import org.example.backend.DTO.response.FaceRecognitionResponse;
import org.example.backend.DTO.response.NumberReaderResponse;
import org.example.backend.feign.FaceRecognizerFeignClient;
import org.example.backend.service.Impl.FaceRecognitionServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class FaceRecognitionServiceTest {
    @Mock
    private FaceRecognizerFeignClient faceRecognizerFeignClient;

    @InjectMocks
    private FaceRecognitionServiceImpl faceRecognitionService;

    private String rightImage;
    private String wrongImage;
    private float rightConfidence;
    private float wrongConfidence;
    private float[] rightEmbedding;
    private float[] wrongEmbedding;
    private FaceRecognitionResponse rightReaderRequest;
    private FaceRecognitionResponse wrongReaderRequest;

    @Before
    public void setUp() throws Exception {
        rightImage = "right";
        wrongImage = "wrong";
        rightEmbedding = new float[]{0.5f, 0.5f};
        wrongEmbedding = new float[]{1f, 1f};
        rightReaderRequest = new FaceRecognitionResponse();
        wrongReaderRequest = new FaceRecognitionResponse();

        rightReaderRequest.setEmbedding(rightEmbedding);
        wrongReaderRequest.setEmbedding(wrongEmbedding);
    }

    @Test
    public void getEmbedding_returnsEmbedding() throws Exception {
        ResponseEntity<FaceRecognitionResponse> mockResponseEntity =
                ResponseEntity.ok(rightReaderRequest);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertEquals(rightEmbedding, result);
        verify(faceRecognizerFeignClient, times(1)).recognizeFaces(any(NumberReaderRequest.class));
    }

    @Test
    public void getEmbedding_emptyEmbedding_returnsEmptyArray() {
        FaceRecognitionResponse emptyResponse = new FaceRecognitionResponse();
        emptyResponse.setEmbedding(new float[0]);

        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.ok(emptyResponse);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
        verify(faceRecognizerFeignClient).recognizeFaces(any(NumberReaderRequest.class));
    }

    @Test
    public void getEmbedding_nullEmbedding_returnsEmptyArray() {
        FaceRecognitionResponse nullResponse = new FaceRecognitionResponse();
        nullResponse.setEmbedding(null); // Null embedding

        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.ok(nullResponse);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
        verify(faceRecognizerFeignClient).recognizeFaces(any(NumberReaderRequest.class));
    }

    @Test
    public void getEmbedding_http404_returnsEmptyArray() {
        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.notFound().build();
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
    }

    @Test
    public void getEmbedding_http500_returnsEmptyArray() {
        ResponseEntity<FaceRecognitionResponse> mockResponseEntity =
                ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
    }

    @Test
    public void getEmbedding_nullResponseBody_returnsEmptyArray() {
        ResponseEntity<FaceRecognitionResponse> mockResponseEntity =
                ResponseEntity.ok().body(null);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
    }

    @Test
    public void getEmbedding_feignClientThrowsException_returnsEmptyArray() {
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenThrow(new RuntimeException("Service unavailable"));

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(new float[0], result, 0.0f);
    }

    @Test
    public void getEmbedding_differentEmbeddingSizes_returnsCorrectArray() {
        float[] largeEmbedding = new float[512];
        Arrays.fill(largeEmbedding, 0.1f);

        FaceRecognitionResponse largeResponse = new FaceRecognitionResponse();
        largeResponse.setEmbedding(largeEmbedding);

        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.ok(largeResponse);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(largeEmbedding, result, 0.0f);
        assertEquals(512, result.length);
    }

    @Test
    public void getEmbedding_specialFloatValues_handlesCorrectly() {
        float[] specialEmbedding = new float[]{Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY, 0.0f};

        FaceRecognitionResponse specialResponse = new FaceRecognitionResponse();
        specialResponse.setEmbedding(specialEmbedding);

        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.ok(specialResponse);
        when(faceRecognizerFeignClient.recognizeFaces(any(NumberReaderRequest.class)))
                .thenReturn(mockResponseEntity);

        float[] result = faceRecognitionService.getEmbedding(rightImage);

        assertArrayEquals(specialEmbedding, result, 0.0f);
    }

    @Test
    public void getEmbedding_verifyBase64PassedCorrectly() {
        String expectedBase64 = "test_base64_string";
        FaceRecognitionResponse response = new FaceRecognitionResponse();
        response.setEmbedding(new float[]{0.1f, 0.2f});

        ResponseEntity<FaceRecognitionResponse> mockResponseEntity = ResponseEntity.ok(response);

        ArgumentCaptor<NumberReaderRequest> requestCaptor =
                ArgumentCaptor.forClass(NumberReaderRequest.class);

        when(faceRecognizerFeignClient.recognizeFaces(requestCaptor.capture()))
                .thenReturn(mockResponseEntity);

        faceRecognitionService.getEmbedding(expectedBase64);

        NumberReaderRequest capturedRequest = requestCaptor.getValue();
        assertEquals(expectedBase64, capturedRequest.getBase64());
    }
}
