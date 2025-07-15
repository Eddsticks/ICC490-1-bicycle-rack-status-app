package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icc490.bike.desktop.exception.ApiErrorResponse;
import com.icc490.bike.desktop.exception.ApiException;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordPageResponse;
import com.icc490.bike.desktop.model.RecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ApiClient apiClient;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        apiClient = new ApiClient() {
            @Override
            protected HttpClient getHttpClient() {
                return mockHttpClient;
            }

            @Override
            protected ObjectMapper getObjectMapper() {
                return objectMapper;
            }
        };
    }

    @Test
    void testGetAllRecordsSuccess() throws Exception {
        Instant now = Instant.now();

        Record record1 = new Record(1L, "s123", "Student A", "Bike 1", now, null, null, null);
        Record record2 = new Record(2L, "s456", "Student B", "Bike 2", now, null, null, null);

        List<Record> expectedRecords = Arrays.asList(record1, record2);
        RecordPageResponse pageResponse = new RecordPageResponse(expectedRecords, null);
        String jsonResponse = objectMapper.writeValueAsString(pageResponse);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        List<Record> actualRecords = apiClient.getAllRecords().get();

        assertNotNull(actualRecords);
        assertEquals(2, actualRecords.size());
        assertEquals(expectedRecords.get(0).getId(), actualRecords.get(0).getId());
        assertEquals(expectedRecords.get(1).getId(), actualRecords.get(1).getId());
    }

    @Test
    void testCreateRecordSerializationErrorReturnsNull() throws Exception {
        ApiClient apiClientWithSerializationError = new ApiClient() {
            @Override
            protected HttpClient getHttpClient() {
                return mockHttpClient;
            }

            @Override
            protected ObjectMapper getObjectMapper() {
                return new ObjectMapper() {
                    @Override
                    public String writeValueAsString(Object value) throws com.fasterxml.jackson.core.JsonProcessingException {
                        throw new com.fasterxml.jackson.core.JsonProcessingException("Simulated serialization error") {};
                    }
                };
            }
        };

        RecordRequest recordRequest = new RecordRequest("s123", "Student Name", "Bike Desc", 1L, 1L);
        Record result = apiClientWithSerializationError.createRecord(recordRequest).get();

        assertNull(result);
    }

    @Test
    void testGetAllRecordsApiErrorReturnsNull() throws Exception {
        String errorJson = "{\"timestamp\":\"2023-01-01T10:00:00Z\",\"status\":404,\"error\":\"Not Found\"}";

        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn(errorJson);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> apiClient.getAllRecords().get());
        assertTrue(thrown.getCause() instanceof ApiException);
    }

    @Test
    void testCheckOutRecordApiErrorProperlyHandled() throws Exception {
        String errorJson = "{"
                + "\"timestamp\":\"2025-07-15T00:00:00Z\","
                + "\"status\":404,"
                + "\"error\":\"Record not found\""
                + "}";

        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn(errorJson);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> apiClient.checkOutRecord(999L).get());

        assertTrue(thrown.getCause() instanceof ApiException);
        ApiException apiException = (ApiException) thrown.getCause();

        assertNotNull(apiException.getErrorResponse());
        assertEquals(404, apiException.getErrorResponse().getStatus());
        assertEquals("Record not found", apiException.getErrorResponse().getError());
    }


    @Test
    void testCheckOutRecordNetworkErrorThrowsRuntimeException() {
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(new IOException("Simulated network down")));

        ExecutionException thrown = assertThrows(ExecutionException.class, () -> apiClient.checkOutRecord(1L).get());

        assertTrue(thrown.getCause() instanceof RuntimeException);
        assertTrue(thrown.getCause().getCause() instanceof IOException);
        assertTrue(thrown.getCause().getMessage().contains("Simulated network down"));
    }
}
