package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.util.BlockRef;
import io.github.scuba10steve.s3.util.EZStorageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class StorageMultiblock extends EZBlock {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageMultiblock.class);
    
    protected StorageMultiblock(Properties properties) {
        super(properties);
    }
    
    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean isMoving) {
        super.onPlace(state, level, pos, oldState, isMoving);
        if (!level.isClientSide) {
            LOGGER.info("StorageMultiblock placed at {}, attempting multiblock scan", pos);
            attemptMultiblock(level, pos);
        }
    }
    
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock()) && !level.isClientSide) {
            attemptMultiblock(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
    
    public StorageCoreBlockEntity attemptMultiblock(Level level, BlockPos pos) {
        if (level.isClientSide) return null;
        
        if (!(this instanceof BlockStorageCore)) {
            BlockRef br = new BlockRef(this, pos);
            StorageCoreBlockEntity core = findCore(br, level, null);
            if (core != null) {
                core.scanMultiblock();
            }
            return core;
        }
        return null;
    }
    
    public StorageCoreBlockEntity findCore(BlockRef br, Level level, Set<BlockRef> scanned) {
        if (scanned == null) {
            scanned = new HashSet<>();
        }
        
        LOGGER.debug("Searching for core from position {}", br.pos);
        List<BlockRef> neighbors = EZStorageUtils.getNeighbors(br.pos, level);
        LOGGER.debug("Found {} neighbors", neighbors.size());
        
        for (BlockRef blockRef : neighbors) {
            LOGGER.debug("Checking neighbor at {} - block: {}", blockRef.pos, blockRef.block.getClass().getSimpleName());
            if (blockRef.block instanceof StorageMultiblock) {
                if (blockRef.block instanceof BlockStorageCore) {
                    LOGGER.info("Found storage core at {}", blockRef.pos);
                    return (StorageCoreBlockEntity) level.getBlockEntity(blockRef.pos);
                } else {
                    if (scanned.add(blockRef)) {
                        StorageCoreBlockEntity entity = findCore(blockRef, level, scanned);
                        if (entity != null) {
                            return entity;
                        }
                    }
                }
            }
        }
        LOGGER.debug("No core found from position {}", br.pos);
        return null;
    }
}
