package io.github.scuba10steve.s3.platform;

import io.github.scuba10steve.s3.config.StorageConfig;

/**
 * NeoForge implementation of S3Config, delegating to StorageConfig ModConfigSpec values.
 */
public class NeoForgeConfig implements S3Config {

    @Override public int getBasicCapacity() { return StorageConfig.BASIC_CAPACITY.get(); }
    @Override public int getCondensedCapacity() { return StorageConfig.CONDENSED_CAPACITY.get(); }
    @Override public int getCompressedCapacity() { return StorageConfig.COMPRESSED_CAPACITY.get(); }
    @Override public int getSuperCapacity() { return StorageConfig.SUPER_CAPACITY.get(); }
    @Override public int getUltraCapacity() { return StorageConfig.ULTRA_CAPACITY.get(); }
    @Override public int getHyperCapacity() { return StorageConfig.HYPER_CAPACITY.get(); }
    @Override public int getUltimateCapacity() { return StorageConfig.ULTIMATE_CAPACITY.get(); }

    @Override public boolean isSecurityEnabled() { return StorageConfig.ENABLE_SECURITY.get(); }
    @Override public boolean isOpOverrideEnabled() { return StorageConfig.ENABLE_OP_OVERRIDE.get(); }
    @Override public boolean isSearchAutoFocus() { return StorageConfig.SEARCH_AUTO_FOCUS.get(); }

    @Override public int getCraftShiftClickLimit() { return StorageConfig.CRAFT_SHIFT_CLICK_LIMIT.get(); }
    @Override public int getExtractPortInterval() { return StorageConfig.EXTRACT_PORT_INTERVAL.get(); }
    @Override public int getMinSyncInterval() { return StorageConfig.MIN_SYNC_INTERVAL.get(); }
}
