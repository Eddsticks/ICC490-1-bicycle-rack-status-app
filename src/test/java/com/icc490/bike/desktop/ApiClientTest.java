package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordRequest;
import com.icc490.bike.desktop.model.RecordPageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

        apiClient = new ApiClient(mockHttpClient, objectMapper);
    }

    @Test
    void testGetAllRecordsSuccess() throws Exception {
        Record record1 = new Record(1L, "s1", "StudentA", "Bike1", Instant.now(), null, null, 1L);
        Record record2 = new Record(2L, "s2", "StudentB", "Bike2", Instant.now(), null, null, 2L);
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
    }

    @Test
    void testCreateRecordSerializationErrorReturnsNull() throws Exception {
        ApiClient brokenApiClient = new ApiClient(mockHttpClient, new ObjectMapper() {
            @Override
            public String writeValueAsString(Object value) {
                throw new RuntimeException("Forced serialization error");
            }
        });

        RecordRequest recordRequest = new RecordRequest("s123", "Student Name", "Bike Desc", 1L, 1L);
        Record result = brokenApiClient.createRecord(recordRequest).get();

        assertNull(result);
    }

    @Test
    void testGetAllRecordsReturnsNullOnApiError() throws Exception {
        when(mockHttpResponse.statusCode()).thenReturn(500);
        when(mockHttpResponse.body()).thenReturn("{\"status\":500,\"error\":\"Internal Server Error\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        List<Record> result = apiClient.getAllRecords().get();
        assertNull(result);
    }

    @Test
    void testCheckOutRecordReturnsNullOnError() throws Exception {
        when(mockHttpResponse.statusCode()).thenReturn(404);
        when(mockHttpResponse.body()).thenReturn("{\"status\":404,\"error\":\"Not Found\"}");
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        Record result = apiClient.checkOutRecord(99L).get();
        assertNull(result);
    }

    @Test
    void testCheckOutRecordNetworkErrorReturnsNull() {
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Simulated network error")));

        Record result = apiClient.checkOutRecord(1L).join();
        assertNull(result);
    }
}
