package io.github.scuba10steve.s3.util;

public final class CountFormatter {

    private CountFormatter() {}

    public static String formatCount(long count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 10_000) return formatWithSuffix(count, 1000, "K");
        if (count < 1_000_000) return (count / 1000) + "K";
        if (count < 10_000_000) return formatWithSuffix(count, 1_000_000, "M");
        if (count < 1_000_000_000) return (count / 1_000_000) + "M";
        if (count < 10_000_000_000L) return formatWithSuffix(count, 1_000_000_000, "B");
        return (count / 1_000_000_000) + "B";
    }

    public static String formatExactCount(long count) {
        return String.format("%,d", count);
    }

    private static String formatWithSuffix(long count, long divisor, String suffix) {
        long whole = count / divisor;
        long fraction = (count % divisor) * 10 / divisor;
        if (fraction == 0) return whole + suffix;
        return whole + "." + fraction + suffix;
    }
}
