package com.ney.mooncompass.util;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PlaceholderUtilTest {

    @Test
    void nullAndEmptyBecomeEmptyString() {

        assertEquals("", PlaceholderUtil.apply(null, Map.of("x", "1")));
        assertEquals("", PlaceholderUtil.apply("", Map.of("x", "1")));

    }

    @Test
    void placeholdersAreReplaced() {

        Map<String, String> placeholders = PlaceholderUtil.newPlaceholders();
        placeholders.put("x", "10");
        placeholders.put("direction", "N");

        assertEquals("10 N", PlaceholderUtil.apply("{x} {direction}", placeholders));

    }

    @Test
    void unknownPlaceholdersStayUntouched() {
        assertEquals("{unknown}", PlaceholderUtil.apply("{unknown}", PlaceholderUtil.newPlaceholders()));
    }

    @Test
    void nullValuesAreSkipped() {

        Map<String, String> placeholders = PlaceholderUtil.newPlaceholders();
        placeholders.put("x", null);

        assertEquals("{x}", PlaceholderUtil.apply("{x}", placeholders));

    }
}
