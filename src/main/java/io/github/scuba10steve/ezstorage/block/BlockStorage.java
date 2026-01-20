package io.github.scuba10steve.ezstorage.block;

import io.github.scuba10steve.ezstorage.config.EZConfig;
import io.github.scuba10steve.ezstorage.init.EZBlocks;

public class BlockStorage extends StorageMultiblock implements StorageTier {
    private final int defaultCapacity;
    
    public BlockStorage(int defaultCapacity) {
        super(Properties.of().strength(2.0f));
        this.defaultCapacity = defaultCapacity;
    }

    @Override
    public int getCapacity() {
        // Return config value if available, otherwise use default
        if (this == EZBlocks.STORAGE_BOX.get()) {
            return EZConfig.BASIC_CAPACITY.get();
        } else if (this == EZBlocks.CONDENSED_STORAGE_BOX.get()) {
            return EZConfig.CONDENSED_CAPACITY.get();
        } else if (this == EZBlocks.SUPER_STORAGE_BOX.get()) {
            return EZConfig.SUPER_CAPACITY.get();
        } else if (this == EZBlocks.ULTRA_STORAGE_BOX.get()) {
            return EZConfig.ULTRA_CAPACITY.get();
        } else if (this == EZBlocks.HYPER_STORAGE_BOX.get()) {
            return EZConfig.HYPER_CAPACITY.get();
        }
        return defaultCapacity;
    }
}
