package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.icc490.bike.desktop.model.Rack;
import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordPageResponse;
import com.icc490.bike.desktop.model.RecordRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApiClientTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockHttpResponse;

    private ObjectMapper objectMapper;
    private ApiClient apiClient;

    private static final Random RANDOM = new Random();

    private String generateRandomStudentId() {
        return "21" + String.format("%03d", RANDOM.nextInt(1000)) +
                String.format("%03d", RANDOM.nextInt(1000)) +
                RANDOM.nextInt(10) + "23";
    }

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Mock HttpClient.newHttpClient() using Mockito.mockStatic
        try (MockedStatic<HttpClient> mockedStaticHttpClient = mockStatic(HttpClient.class)) {
            mockedStaticHttpClient.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
            apiClient = new ApiClient();
        }
    }

    @Test
    void testGetAllRecordsSuccess() throws Exception {
        Record record1 = new Record(1L, generateRandomStudentId(), "User One", "Bike A", Instant.now(), null, new Rack(1L, 5L, 10L, 50L), 1L);
        Record record2 = new Record(2L, generateRandomStudentId(), "User Two", "Bike B", Instant.now(), null, new Rack(1L, 5L, 10L, 50L), 2L);
        List<Record> records = Arrays.asList(record1, record2);
        RecordPageResponse pageResponse = new RecordPageResponse(records, null);

        String jsonResponse = objectMapper.writeValueAsString(pageResponse);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        List<Record> result = apiClient.getAllRecords().get();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(record1.getStudentId(), result.get(0).getStudentId());
        assertEquals(record2.getStudentId(), result.get(1).getStudentId());
    }

    @Test
    void testCreateRecordSuccess() throws Exception {
        String studentId = generateRandomStudentId();
        RecordRequest request = new RecordRequest(studentId, "New User", "New Bike", 1L, 3L);
        Record createdRecord = new Record(10L, studentId, "New User", "New Bike", Instant.now(), null, new Rack(1L, 5L, 10L, 50L), 3L);
        String jsonResponse = objectMapper.writeValueAsString(createdRecord);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        Record result = apiClient.createRecord(request).get();

        assertNotNull(result);
        assertEquals(createdRecord.getStudentId(), result.getStudentId());
        assertEquals(createdRecord.getHook(), result.getHook());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).sendAsync(requestCaptor.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(ApiClient.BASE_URL + "/api/records/check-in", capturedRequest.uri().toString());
        assertEquals("POST", capturedRequest.method());
    }

    @Test
    void testCheckOutRecordSuccess() throws Exception {
        Long recordId = 5L;
        Record checkedOutRecord = new Record(recordId, generateRandomStudentId(), "User to checkout", "Bike to checkout", Instant.now(), Instant.now(), new Rack(1L, 5L, 10L, 50L), 5L);
        String jsonResponse = objectMapper.writeValueAsString(checkedOutRecord);

        when(mockHttpResponse.statusCode()).thenReturn(200);
        when(mockHttpResponse.body()).thenReturn(jsonResponse);
        when(mockHttpClient.sendAsync(any(HttpRequest.class), any(HttpResponse.BodyHandlers.ofString().getClass())))
                .thenReturn(CompletableFuture.completedFuture(mockHttpResponse));

        Record result = apiClient.checkOutRecord(recordId).get();

        assertNotNull(result);
        assertEquals(checkedOutRecord.getId(), result.getId());
        assertNotNull(result.getCheckOut());

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(mockHttpClient).sendAsync(requestCaptor.capture(), any(HttpResponse.BodyHandlers.ofString().getClass()));
        HttpRequest capturedRequest = requestCaptor.getValue();
        assertEquals(ApiClient.BASE_URL + "/api/records/check-out/" + recordId, capturedRequest.uri().toString());
        assertEquals("PUT", capturedRequest.method());
    }
}