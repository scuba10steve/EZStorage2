package io.github.scuba10steve.s3.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CountFormatterTest {

    @Test
    void testFormatExactCountSmallValues() {
        assertEquals("0", CountFormatter.formatExactCount(0));
        assertEquals("1", CountFormatter.formatExactCount(1));
        assertEquals("999", CountFormatter.formatExactCount(999));
    }

    @Test
    void testFormatExactCountWithCommaGrouping() {
        assertEquals("1,000", CountFormatter.formatExactCount(1000));
        assertEquals("1,234", CountFormatter.formatExactCount(1234));
        assertEquals("10,000", CountFormatter.formatExactCount(10_000));
        assertEquals("999,999", CountFormatter.formatExactCount(999_999));
    }

    @Test
    void testFormatExactCountMillions() {
        assertEquals("1,000,000", CountFormatter.formatExactCount(1_000_000));
        assertEquals("1,234,567", CountFormatter.formatExactCount(1_234_567));
        assertEquals("999,999,999", CountFormatter.formatExactCount(999_999_999));
    }

    @Test
    void testFormatExactCountBillions() {
        assertEquals("1,000,000,000", CountFormatter.formatExactCount(1_000_000_000));
        assertEquals("10,000,000,000", CountFormatter.formatExactCount(10_000_000_000L));
        assertEquals("100,000,000,000", CountFormatter.formatExactCount(100_000_000_000L));
    }
}
