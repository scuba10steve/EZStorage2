package io.github.scuba10steve.s3.util;

/**
 * List modes for the Extract Port filtering.
 */
public enum ExtractListMode {
    IGNORE("Ignore", "Extracts any item from storage"),
    WHITELIST("Whitelist", "Only extracts items in the filter list"),
    BLACKLIST("Blacklist", "Extracts any item NOT in the filter list"),
    DISABLED("Disabled", "Extraction is disabled");

    private final String displayName;
    private final String description;

    ExtractListMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public static ExtractListMode fromOrdinal(int ordinal) {
        return values()[ordinal % values().length];
    }

    public ExtractListMode rotateMode() {
        return fromOrdinal(this.ordinal() + 1);
    }

    @Override
    public String toString() {
        return displayName;
    }
}
