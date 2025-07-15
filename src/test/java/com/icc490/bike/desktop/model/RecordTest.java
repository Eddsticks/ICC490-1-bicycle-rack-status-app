package com.icc490.bike.desktop.model;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class RecordTest {

    private static final Random RANDOM = new Random();

    private String generateRandomStudentId() {
        return "21" + String.format("%03d", RANDOM.nextInt(1000)) +
                String.format("%03d", RANDOM.nextInt(1000)) +
                RANDOM.nextInt(10) + "23";
    }

    @Test
    void testNoArgsConstructor() {
        Record record = new Record();
        assertNotNull(record);
        assertNull(record.getId());
        assertNull(record.getStudentId());
        assertNull(record.getStudentName());
        assertNull(record.getBicycleDescription());
        assertNull(record.getCheckIn());
        assertNull(record.getCheckOut());
        assertNull(record.getRack());
        assertNull(record.getHook());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Long id = 101L;
        String studentId = generateRandomStudentId();
        String studentName = "Juan Perez";
        String bicycleDescription = "Bicicleta de montaña roja";
        Instant checkIn = Instant.parse("2024-07-14T10:00:00Z");
        Instant checkOut = Instant.parse("2024-07-14T18:00:00Z");
        Rack rack = new Rack(1L, 5L, 10L, 50L);
        Long hook = 5L;

        Record record = new Record(id, studentId, studentName, bicycleDescription,
                checkIn, checkOut, rack, hook);

        assertEquals(id, record.getId());
        assertEquals(studentId, record.getStudentId());
        assertEquals(studentName, record.getStudentName());
        assertEquals(bicycleDescription, record.getBicycleDescription());
        assertEquals(checkIn, record.getCheckIn());
        assertEquals(checkOut, record.getCheckOut());
        assertEquals(rack, record.getRack());
        assertEquals(hook, record.getHook());
    }

    @Test
    void testSetters() {
        Record record = new Record();

        Long id = 102L;
        String studentId = generateRandomStudentId();
        String studentName = "Maria Gomez";
        String bicycleDescription = "Bicicleta de paseo azul";
        Instant checkIn = Instant.parse("2024-07-15T09:30:00Z");
        Instant checkOut = Instant.parse("2024-07-15T17:45:00Z");
        Rack rack = new Rack(2L, 3L, 7L, 21L);
        Long hook = 3L;

        record.setId(id);
        record.setStudentId(studentId);
        record.setStudentName(studentName);
        record.setBicycleDescription(bicycleDescription);
        record.setCheckIn(checkIn);
        record.setCheckOut(checkOut);
        record.setRack(rack);
        record.setHook(hook);

        assertEquals(id, record.getId());
        assertEquals(studentId, record.getStudentId());
        assertEquals(studentName, record.getStudentName());
        assertEquals(bicycleDescription, record.getBicycleDescription());
        assertEquals(checkIn, record.getCheckIn());
        assertEquals(checkOut, record.getCheckOut());
        assertEquals(rack, record.getRack());
        assertEquals(hook, record.getHook());
    }

    @Test
    void testToString() {
        Long id = 103L;
        String studentId = generateRandomStudentId();
        String studentName = "Carlos Ruiz";
        String bicycleDescription = "Bicicleta urbana verde";
        Instant checkIn = Instant.parse("2024-07-16T11:15:00Z");
        Instant checkOut = null;
        Rack rack = new Rack(1L, 5L, 10L, 50L);
        Long hook = 10L;

        Record record = new Record(id, studentId, studentName, bicycleDescription,
                checkIn, checkOut, rack, hook);

        assertTrue(record.toString().contains("studentId='" + studentId + "'"));
        assertTrue(record.toString().contains("checkOut=null"));
        assertTrue(record.toString().contains("hook=10"));
    }

    @Test
    void testToStringWithoutCheckOut() {
        Long id = 104L;
        String studentId = generateRandomStudentId();
        String studentName = "Ana Lopez";
        String bicycleDescription = "Bicicleta de carreras";
        Instant checkIn = Instant.parse("2024-07-17T08:00:00Z");
        Instant checkOut = null;
        Rack rack = new Rack(2L, 3L, 7L, 21L);
        Long hook = 1L;

        Record record = new Record(id, studentId, studentName, bicycleDescription,
                checkIn, checkOut, rack, hook);

        assertTrue(record.toString().contains("checkOut=null"));
        assertTrue(record.toString().contains("studentId='" + studentId + "'"));
        assertTrue(record.toString().contains("hook=1"));
    }
}