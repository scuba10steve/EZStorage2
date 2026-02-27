package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.block.BlockStorageCore;
import io.github.scuba10steve.s3.block.StorageMultiblock;
import io.github.scuba10steve.s3.util.BlockRef;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class MultiblockBlockEntity extends BaseBlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(MultiblockBlockEntity.class);
    protected StorageCoreBlockEntity core;
    protected BlockPos corePos; // Store core position for client sync
    private static final int UPDATE_PERIOD = 10;
    
    public MultiblockBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
    
    public void tick() {
        if (!hasCore()) {
            if (!level.isClientSide && level.getGameTime() % UPDATE_PERIOD == 0) {
                LOGGER.debug("Attempting to find core for crafting box at {}", worldPosition);
                StorageMultiblock block = (StorageMultiblock) getBlockState().getBlock();
                core = block.attemptMultiblock(level, worldPosition);
                if (core != null) {
                    LOGGER.debug("Found core at {} for crafting box at {}", core.getBlockPos(), worldPosition);
                    corePos = core.getBlockPos();
                    setChanged();
                } else {
                    LOGGER.debug("No core found for crafting box at {}", worldPosition);
                }
            } else if (level.isClientSide && corePos != null) {
                // On client, try to get core from stored position
                if (level.getBlockEntity(corePos) instanceof StorageCoreBlockEntity coreEntity) {
                    core = coreEntity;
                }
            }
        } else {
            if (!isCorePartOfMultiblock()) {
                LOGGER.debug("Core is no longer part of multiblock, disconnecting");
                core = null;
                corePos = null;
                setChanged();
            }
        }
    }
    
    public boolean hasCore() {
        return core != null && !core.isRemoved();
    }
    
    public boolean isCorePartOfMultiblock() {
        return hasCore() && core.isPartOfMultiblock(new BlockRef(getBlockState().getBlock(), worldPosition));
    }
    
    public StorageCoreBlockEntity getCore() {
        return core;
    }
}
