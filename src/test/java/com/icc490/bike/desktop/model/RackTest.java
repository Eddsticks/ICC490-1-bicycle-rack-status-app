package com.icc490.bike.desktop.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RackTest {

    @Test
    void testNoArgsConstructor() {
        Rack rack = new Rack();
        assertNotNull(rack);
        assertNull(rack.getId());
        assertNull(rack.getRows());
        assertNull(rack.getColumns());
        assertNull(rack.getTotalHooks());
    }

    @Test
    void testAllArgsConstructorAndGetters() {
        Long id = 1L;
        Long rows = 5L;
        Long columns = 10L;
        Long totalHooks = 50L;

        Rack rack = new Rack(id, rows, columns, totalHooks);

        assertEquals(id, rack.getId());
        assertEquals(rows, rack.getRows());
        assertEquals(columns, rack.getColumns());
        assertEquals(totalHooks, rack.getTotalHooks());
    }

    @Test
    void testSetters() {
        Rack rack = new Rack();

        Long id = 2L;
        Long rows = 3L;
        Long columns = 7L;
        Long totalHooks = 21L;

        rack.setId(id);
        rack.setRows(rows);
        rack.setColumns(columns);
        rack.setTotalHooks(totalHooks);

        assertEquals(id, rack.getId());
        assertEquals(rows, rack.getRows());
        assertEquals(columns, rack.getColumns());
        assertEquals(totalHooks, rack.getTotalHooks());
    }

    @Test
    void testToString() {
        Long id = 1L;
        Long rows = 5L;
        Long columns = 10L;
        Long totalHooks = 50L;

        Rack rack = new Rack(id, rows, columns, totalHooks);
        String expectedToString = "Rack{id=1, rows=5, columns=10, totalHooks=50}";
        assertEquals(expectedToString, rack.toString());
    }

    @Test
    void testToStringWithNullValues() {
        Rack rack = new Rack(); // Using no-arg constructor
        String expectedToString = "Rack{id=null, rows=null, columns=null, totalHooks=null}";
        assertEquals(expectedToString, rack.toString());
    }
}