package io.github.scuba10steve.s3.platform;

/**
 * Platform-agnostic config interface. Implemented by NeoForge's StorageConfig wrapper.
 * Access via {@link S3Platform#getConfig()}.
 */
public interface S3Config {

    // Storage Capacities
    int getBasicCapacity();
    int getCondensedCapacity();
    int getCompressedCapacity();
    int getSuperCapacity();
    int getUltraCapacity();
    int getHyperCapacity();
    int getUltimateCapacity();

    // Feature Toggles
    boolean isSecurityEnabled();
    boolean isOpOverrideEnabled();
    boolean isSearchAutoFocus();

    // Crafting
    int getCraftShiftClickLimit();
    boolean shouldAutoRepopulateCraftingGrid();

    // Automation
    int getExtractPortInterval();
    int getMinSyncInterval();
}
