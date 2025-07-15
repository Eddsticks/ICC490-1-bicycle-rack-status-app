package com.icc490.bike.desktop.exception;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiErrorResponseTest {

    @Test
    void testNoArgsConstructor() {
        ApiErrorResponse errorResponse = new ApiErrorResponse();
        assertNotNull(errorResponse);
        assertNull(errorResponse.getTimestamp());
        assertEquals(0, errorResponse.getStatus());
        assertNull(errorResponse.getError());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Instant timestamp = Instant.now();
        int status = 400;
        String errorMessage = "Bad Request";

        ApiErrorResponse errorResponse = new ApiErrorResponse(timestamp, status, errorMessage);

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(errorMessage, errorResponse.getError());
    }

    @Test
    void testSetters() {
        ApiErrorResponse errorResponse = new ApiErrorResponse();

        Instant timestamp = Instant.now();
        int status = 404;
        String errorMessage = "Not Found";

        errorResponse.setTimestamp(timestamp);
        errorResponse.setStatus(status);
        errorResponse.setError(errorMessage);

        assertEquals(timestamp, errorResponse.getTimestamp());
        assertEquals(status, errorResponse.getStatus());
        assertEquals(errorMessage, errorResponse.getError());
    }

    @Test
    void testToStringWithSingleError() {
        Instant timestamp = Instant.parse("2024-07-14T12:30:00Z");
        int status = 500;
        String errorMessage = "Internal Server Error";

        ApiErrorResponse errorResponse = new ApiErrorResponse(timestamp, status, errorMessage);

        String expectedToString = "ApiErrorResponse{timestamp=" + timestamp + ", status=500, error=\"Internal Server Error\"}";
        assertEquals(expectedToString, errorResponse.toString());
    }

    @Test
    void testToStringWithErrorList() {
        Instant timestamp = Instant.parse("2024-07-14T13:00:00Z");
        int status = 422;
        List<String> errors = Arrays.asList("Field 'name' is required", "Field 'email' is invalid");

        ApiErrorResponse errorResponse = new ApiErrorResponse(timestamp, status, errors);

        String expectedToString = "ApiErrorResponse{timestamp=" + timestamp + ", status=422, error=[\"Field 'name' is required\", \"Field 'email' is invalid\"]}";
        assertEquals(expectedToString, errorResponse.toString());
    }

    @Test
    void testToStringWithNullError() {
        Instant timestamp = Instant.parse("2024-07-14T14:00:00Z");
        int status = 200;
        Object error = null;

        ApiErrorResponse errorResponse = new ApiErrorResponse(timestamp, status, error);

        String expectedToString = "ApiErrorResponse{timestamp=" + timestamp + ", status=200, error=null}";
        assertEquals(expectedToString, errorResponse.toString());
    }
}