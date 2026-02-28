package io.github.scuba10steve.s3.util;

import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.Comparator;

/**
 * Sort modes for the storage system.
 * Each mode defines how items should be sorted in the Storage Core GUI.
 */
public enum SortMode {
    COUNT("# \u2193", "Sorts by descending item counts, then A-Z for equal cases.",
        (a, b) -> {
            int countCompare = Long.compare(b.getCount(), a.getCount());
            if (countCompare != 0) return countCompare;
            return getDisplayName(a).compareTo(getDisplayName(b));
        }),

    INVERSE_COUNT("# \u2191", "Sorts by ascending item counts, then Z-A for equal cases.",
        (a, b) -> {
            int countCompare = Long.compare(a.getCount(), b.getCount());
            if (countCompare != 0) return countCompare;
            return getDisplayName(b).compareTo(getDisplayName(a));
        }),

    NAME("A-Z", "Sorts A-Z, then by descending item counts for equal cases.",
        (a, b) -> {
            int nameCompare = getDisplayName(a).compareTo(getDisplayName(b));
            if (nameCompare != 0) return nameCompare;
            return Long.compare(b.getCount(), a.getCount());
        }),

    INVERSE_NAME("Z-A", "Sorts Z-A, then by ascending item counts for equal cases.",
        (a, b) -> {
            int nameCompare = getDisplayName(b).compareTo(getDisplayName(a));
            if (nameCompare != 0) return nameCompare;
            return Long.compare(a.getCount(), b.getCount());
        }),

    MOD("Mod A-Z", "Sorts by mod name A-Z, then by descending item counts for equal cases.",
        (a, b) -> {
            int modCompare = getModId(a).compareTo(getModId(b));
            if (modCompare != 0) return modCompare;
            return Long.compare(b.getCount(), a.getCount());
        }),

    INVERSE_MOD("Mod Z-A", "Sorts by mod name Z-A, then by ascending item counts for equal cases.",
        (a, b) -> {
            int modCompare = getModId(b).compareTo(getModId(a));
            if (modCompare != 0) return modCompare;
            return Long.compare(a.getCount(), b.getCount());
        });

    private final String displayName;
    private final String description;
    private final Comparator<StoredItemStack> comparator;

    SortMode(String displayName, String description, Comparator<StoredItemStack> comparator) {
        this.displayName = displayName;
        this.description = description;
        this.comparator = comparator;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Comparator<StoredItemStack> getComparator() {
        return comparator;
    }

    /**
     * Rotate to the next sort mode (cycles through all modes)
     */
    public SortMode rotateMode() {
        return fromOrdinal(this.ordinal() + 1);
    }

    /**
     * Get a sort mode from an ordinal value (wraps around if out of bounds)
     */
    public static SortMode fromOrdinal(int ordinal) {
        return values()[ordinal % values().length];
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Helper methods for comparators

    private static String getDisplayName(StoredItemStack stored) {
        return stored.getItemStack().getHoverName().getString().toLowerCase();
    }

    private static String getModId(StoredItemStack stored) {
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stored.getItemStack().getItem());
        return id.getNamespace().toLowerCase();
    }
}
