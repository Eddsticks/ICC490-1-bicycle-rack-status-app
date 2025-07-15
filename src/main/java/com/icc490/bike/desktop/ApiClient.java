package com.icc490.bike.desktop;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import com.icc490.bike.desktop.model.Record;
import com.icc490.bike.desktop.model.RecordRequest;
import com.icc490.bike.desktop.model.RecordPageResponse;
import com.icc490.bike.desktop.exception.ApiErrorResponse;
import com.icc490.bike.desktop.exception.ApiException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

public class ApiClient {
    static final String BASE_URL = "http://localhost:8080";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public CompletableFuture<List<Record>> getAllRecords() {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/records"))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            RecordPageResponse pageResponse = objectMapper.readValue(response.body(), RecordPageResponse.class);
                            return pageResponse.getRecords();
                        } catch (Exception e) {
                            System.err.println("Error al deserializar la respuesta de registros: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Error de deserialización de registros", e);
                        }
                    } else {
                        String errorBody = response.body();
                        System.err.println("Error al obtener registros de la API: " + response.statusCode() + " - " + errorBody);
                        try {
                            ApiErrorResponse apiError = objectMapper.readValue(errorBody, ApiErrorResponse.class);
                            throw new ApiException("Error de API al obtener registros: " + (apiError.getError() != null ? apiError.getError().toString() : "Unknown"), apiError);
                        } catch (Exception ex) {
                            throw new ApiException("Error desconocido al obtener registros: " + errorBody, ex, null);
                        }
                    }
                })
                .exceptionally(ex -> {
                    Throwable actualCause = (ex instanceof CompletionException || ex instanceof ExecutionException) ? ex.getCause() : ex;
                    if (actualCause instanceof ApiException) {
                        throw (ApiException) actualCause;
                    }
                    System.err.println("Error de conexión/inesperado al obtener registros: " + ex.getMessage());
                    ex.printStackTrace();
                    throw new RuntimeException("Error de conexión/inesperado al obtener registros: " + ex.getMessage(), ex);
                });
    }

    public CompletableFuture<Record> createRecord(RecordRequest recordRequest) {
        try {
            String requestBody = objectMapper.writeValueAsString(recordRequest);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/api/records/check-in"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        if (response.statusCode() == 200) {
                            try {
                                return objectMapper.readValue(response.body(), Record.class);
                            } catch (Exception e) {
                                System.err.println("Error al deserializar el registro creado: " + e.getMessage());
                                e.printStackTrace();
                                throw new RuntimeException("Error de deserialización de registro creado", e);
                            }
                        } else {
                            String errorBody = response.body();
                            System.err.println("Error al crear registro en la API: " + response.statusCode() + " - " + errorBody);
                            try {
                                ApiErrorResponse apiError = objectMapper.readValue(errorBody, ApiErrorResponse.class);
                                throw new ApiException("Error de API al crear registro: " + (apiError.getError() != null ? apiError.getError().toString() : "Unknown"), apiError);
                            } catch (Exception ex) {
                                throw new ApiException("Error desconocido al crear registro: " + errorBody, ex, null);
                            }
                        }
                    })
                    .exceptionally(ex -> {
                        Throwable actualCause = (ex instanceof CompletionException || ex instanceof ExecutionException) ? ex.getCause() : ex;
                        if (actualCause instanceof ApiException) {
                            throw (ApiException) actualCause;
                        }
                        System.err.println("Error de conexión/inesperado al crear registro: " + ex.getMessage());
                        ex.printStackTrace();
                        throw new RuntimeException("Error de conexión/inesperado al crear registro: " + ex.getMessage(), ex);
                    });
        } catch (Exception e) {
            System.err.println("Error al preparar la solicitud de creación de registro: " + e.getMessage());
            e.printStackTrace();
            return CompletableFuture.failedFuture(new RuntimeException("Error al preparar la solicitud de creación de registro", e));
        }
    }

    public CompletableFuture<Record> checkOutRecord(Long recordId) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/records/check-out/" + recordId))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.noBody())
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.statusCode() == 200) {
                        try {
                            return objectMapper.readValue(response.body(), Record.class);
                        } catch (Exception e) {
                            System.err.println("Error al deserializar el registro devuelto: " + e.getMessage());
                            e.printStackTrace();
                            throw new RuntimeException("Error de deserialización de registro devuelto", e);
                        }
                    } else {
                        String errorBody = response.body();
                        System.err.println("Error al devolver registro en la API: " + response.statusCode() + " - " + errorBody);
                        try {
                            ApiErrorResponse apiError = objectMapper.readValue(errorBody, ApiErrorResponse.class);
                            throw new ApiException("Error de API al devolver registro: " + (apiError.getError() != null ? apiError.getError().toString() : "Unknown"), apiError);
                        } catch (Exception ex) {
                            throw new ApiException("Error desconocido al devolver registro: " + errorBody, ex, null);
                        }
                    }
                })
                .exceptionally(ex -> {
                    Throwable actualCause = (ex instanceof CompletionException || ex instanceof ExecutionException) ? ex.getCause() : ex;
                    if (actualCause instanceof ApiException) {
                        throw (ApiException) actualCause;
                    }
                    System.err.println("Error de conexión/inesperado al devolver registro: " + ex.getMessage());
                    ex.printStackTrace();
                    throw new RuntimeException("Error de conexión/inesperado al devolver registro: " + ex.getMessage(), ex);
                });
    }
}