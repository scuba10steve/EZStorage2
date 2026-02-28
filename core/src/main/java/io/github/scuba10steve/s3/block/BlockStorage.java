package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.platform.S3Config;
import io.github.scuba10steve.s3.platform.S3Platform;

public class BlockStorage extends StorageMultiblock implements StorageTier {
    private final int defaultCapacity;
    private final String tierKey;

    public BlockStorage(int defaultCapacity, String tierKey) {
        super(Properties.of().strength(2.0f));
        this.defaultCapacity = defaultCapacity;
        this.tierKey = tierKey;
    }

    @Override
    public int getCapacity() {
        S3Config config = S3Platform.getConfig();
        if (config == null) return defaultCapacity;
        return switch (tierKey) {
            case "basic" -> config.getBasicCapacity();
            case "condensed" -> config.getCondensedCapacity();
            case "compressed" -> config.getCompressedCapacity();
            case "super" -> config.getSuperCapacity();
            case "ultra" -> config.getUltraCapacity();
            case "hyper" -> config.getHyperCapacity();
            case "ultimate" -> config.getUltimateCapacity();
            default -> defaultCapacity;
        };
    }
}
