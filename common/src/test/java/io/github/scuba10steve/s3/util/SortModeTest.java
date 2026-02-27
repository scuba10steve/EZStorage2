package io.github.scuba10steve.s3.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for SortMode enum and sorting functionality
 */
class SortModeTest {

    @Test
    void testRotateMode() {
        SortMode mode = SortMode.COUNT;
        mode = mode.rotateMode();
        assertEquals(SortMode.INVERSE_COUNT, mode);
        
        mode = mode.rotateMode();
        assertEquals(SortMode.NAME, mode);
        
        mode = mode.rotateMode();
        assertEquals(SortMode.INVERSE_NAME, mode);
        
        mode = mode.rotateMode();
        assertEquals(SortMode.MOD, mode);
        
        mode = mode.rotateMode();
        assertEquals(SortMode.INVERSE_MOD, mode);
        
        // Should wrap around
        mode = mode.rotateMode();
        assertEquals(SortMode.COUNT, mode);
    }

    @Test
    void testFromOrdinal() {
        assertEquals(SortMode.COUNT, SortMode.fromOrdinal(0));
        assertEquals(SortMode.INVERSE_COUNT, SortMode.fromOrdinal(1));
        assertEquals(SortMode.NAME, SortMode.fromOrdinal(2));
        
        // Test wrap-around
        assertEquals(SortMode.COUNT, SortMode.fromOrdinal(6));
        assertEquals(SortMode.INVERSE_COUNT, SortMode.fromOrdinal(7));
    }

    @Test
    void testAllModesHaveProperties() {
        for (SortMode mode : SortMode.values()) {
            assertNotNull(mode.getDisplayName(), "Display name should not be null for " + mode);
            assertNotNull(mode.getDescription(), "Description should not be null for " + mode);
            assertNotNull(mode.getComparator(), "Comparator should not be null for " + mode);
            assertFalse(mode.getDisplayName().isEmpty(), "Display name should not be empty for " + mode);
            assertFalse(mode.getDescription().isEmpty(), "Description should not be empty for " + mode);
        }
    }

    @Test
    void testSortModeCount() {
        assertEquals(6, SortMode.values().length, "Should have exactly 6 sort modes");
    }
}
