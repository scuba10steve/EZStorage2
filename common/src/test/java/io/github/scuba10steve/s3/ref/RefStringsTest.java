package io.github.scuba10steve.s3.ref;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefStringsTest {

    @Test
    void testModId() {
        assertEquals("s3", RefStrings.MODID);
    }

    @Test
    void testModName() {
        assertEquals("Steve's Simple Storage", RefStrings.NAME);
    }

    @Test
    void testModVersion() {
        assertEquals("2.5.0", RefStrings.VERSION);
    }
}
