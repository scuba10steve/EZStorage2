package io.github.scuba10steve.s3.gui;

import io.github.scuba10steve.s3.util.CountFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FormatCountTest {

    @Test
    void testSmallValues() {
        assertEquals("0", CountFormatter.formatCount(0));
        assertEquals("1", CountFormatter.formatCount(1));
        assertEquals("999", CountFormatter.formatCount(999));
    }

    @Test
    void testThousands() {
        assertEquals("1K", CountFormatter.formatCount(1000));
        assertEquals("1.5K", CountFormatter.formatCount(1500));
        assertEquals("9.9K", CountFormatter.formatCount(9999));
        assertEquals("10K", CountFormatter.formatCount(10_000));
        assertEquals("100K", CountFormatter.formatCount(100_000));
        assertEquals("999K", CountFormatter.formatCount(999_999));
    }

    @Test
    void testMillions() {
        assertEquals("1M", CountFormatter.formatCount(1_000_000));
        assertEquals("2.8M", CountFormatter.formatCount(2_800_000));
        assertEquals("9.9M", CountFormatter.formatCount(9_999_999));
        assertEquals("10M", CountFormatter.formatCount(10_000_000));
        assertEquals("999M", CountFormatter.formatCount(999_999_999));
    }

    @Test
    void testBillions() {
        assertEquals("1B", CountFormatter.formatCount(1_000_000_000));
        assertEquals("1.2B", CountFormatter.formatCount(1_200_000_000));
        assertEquals("9.9B", CountFormatter.formatCount(9_999_999_999L));
        assertEquals("10B", CountFormatter.formatCount(10_000_000_000L));
        assertEquals("100B", CountFormatter.formatCount(100_000_000_000L));
    }

    @Test
    void testDecimalDropsTrailingZero() {
        assertEquals("1K", CountFormatter.formatCount(1000));
        assertEquals("2K", CountFormatter.formatCount(2000));
        assertEquals("1M", CountFormatter.formatCount(1_000_000));
        assertEquals("5M", CountFormatter.formatCount(5_000_000));
        assertEquals("1B", CountFormatter.formatCount(1_000_000_000));
    }
}
