package io.github.scuba10steve.s3.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ExtractListMode enum and rotation functionality
 */
class ExtractListModeTest {

    @Test
    void testRotateMode() {
        ExtractListMode mode = ExtractListMode.IGNORE;
        mode = mode.rotateMode();
        assertEquals(ExtractListMode.WHITELIST, mode);

        mode = mode.rotateMode();
        assertEquals(ExtractListMode.BLACKLIST, mode);

        mode = mode.rotateMode();
        assertEquals(ExtractListMode.DISABLED, mode);

        // Should wrap around
        mode = mode.rotateMode();
        assertEquals(ExtractListMode.IGNORE, mode);
    }

    @Test
    void testFromOrdinal() {
        assertEquals(ExtractListMode.IGNORE, ExtractListMode.fromOrdinal(0));
        assertEquals(ExtractListMode.WHITELIST, ExtractListMode.fromOrdinal(1));
        assertEquals(ExtractListMode.BLACKLIST, ExtractListMode.fromOrdinal(2));
        assertEquals(ExtractListMode.DISABLED, ExtractListMode.fromOrdinal(3));

        // Test wrap-around
        assertEquals(ExtractListMode.IGNORE, ExtractListMode.fromOrdinal(4));
        assertEquals(ExtractListMode.WHITELIST, ExtractListMode.fromOrdinal(5));
    }

    @Test
    void testAllModesHaveProperties() {
        for (ExtractListMode mode : ExtractListMode.values()) {
            assertNotNull(mode.getDisplayName(), "Display name should not be null for " + mode);
            assertNotNull(mode.getDescription(), "Description should not be null for " + mode);
            assertFalse(mode.getDisplayName().isEmpty(), "Display name should not be empty for " + mode);
            assertFalse(mode.getDescription().isEmpty(), "Description should not be empty for " + mode);
        }
    }

    @Test
    void testModeCount() {
        assertEquals(4, ExtractListMode.values().length, "Should have exactly 4 extract list modes");
    }

    @Test
    void testToStringReturnsDisplayName() {
        for (ExtractListMode mode : ExtractListMode.values()) {
            assertEquals(mode.getDisplayName(), mode.toString());
        }
    }
}
