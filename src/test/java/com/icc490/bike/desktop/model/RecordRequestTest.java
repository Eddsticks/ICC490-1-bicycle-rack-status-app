package com.icc490.bike.desktop.model;

import org.junit.jupiter.api.Test;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RecordRequestTest {

    private static final Random RANDOM = new Random();

    private String generateRandomStudentId() {
        return "21" + String.format("%03d", RANDOM.nextInt(1000)) +
                String.format("%03d", RANDOM.nextInt(1000)) +
                RANDOM.nextInt(10) + "23";
    }

    @Test
    void testNoArgsConstructor() {
        RecordRequest request = new RecordRequest();
        assertNotNull(request);
        assertNull(request.getStudentId());
        assertNull(request.getStudentName());
        assertNull(request.getBicycleDescription());
        assertNull(request.getRackId());
        assertNull(request.getHook());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        String studentId = generateRandomStudentId();
        String studentName = "Pedro Castro";
        String bicycleDescription = "Bicicleta de carretera negra";
        Long rackId = 1L;
        Long hook = 12L;

        RecordRequest request = new RecordRequest(studentId, studentName, bicycleDescription, rackId, hook);

        assertEquals(studentId, request.getStudentId());
        assertEquals(studentName, request.getStudentName());
        assertEquals(bicycleDescription, request.getBicycleDescription());
        assertEquals(rackId, request.getRackId());
        assertEquals(hook, request.getHook());
    }

    @Test
    void testSetters() {
        RecordRequest request = new RecordRequest();

        String studentId = generateRandomStudentId();
        String studentName = "Laura Diaz";
        String bicycleDescription = "Bicicleta plegable gris";
        Long rackId = 2L;
        Long hook = 7L;

        request.setStudentId(studentId);
        request.setStudentName(studentName);
        request.setBicycleDescription(bicycleDescription);
        request.setRackId(rackId);
        request.setHook(hook);

        assertEquals(studentId, request.getStudentId());
        assertEquals(studentName, request.getStudentName());
        assertEquals(bicycleDescription, request.getBicycleDescription());
        assertEquals(rackId, request.getRackId());
        assertEquals(hook, request.getHook());
    }

    @Test
    void testToString() {
        String studentId = generateRandomStudentId();
        String studentName = "Roberto Soto";
        String bicycleDescription = "BMX azul";
        Long rackId = 3L;
        Long hook = 2L;

        RecordRequest request = new RecordRequest(studentId, studentName, bicycleDescription, rackId, hook);

        String expectedToStringPrefix = "RecordRequest{studentId='" + studentId + "', studentName='Roberto Soto', bicycleDescription='BMX azul', rackId=3, hook=2}";
        assertEquals(expectedToStringPrefix, request.toString());
    }
}