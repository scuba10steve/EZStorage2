package com.zerofall.ezstorage.ref;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RefStringsTest {

    @Test
    void testModId() {
        assertEquals("ezstorage", RefStrings.MODID);
    }

    @Test
    void testModName() {
        assertEquals("EZStorage 2", RefStrings.NAME);
    }

    @Test
    void testModVersion() {
        assertEquals("2.5.0", RefStrings.VERSION);
    }
}
