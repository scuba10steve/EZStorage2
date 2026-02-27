package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.EjectPortBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Eject Port - Automatically pushes items from storage into an inventory above it.
 * When placed adjacent to a Storage Core multiblock, it will continuously eject items
 * from the storage system into any inventory (chest, hopper, etc.) placed directly above it.
 */
public class BlockEjectPort extends StorageMultiblock implements EntityBlock {
    public BlockEjectPort() {
        super(Properties.of().strength(2.0f));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EjectPortBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide() ? null : (lvl, pos, st, be) -> {
            if (be instanceof EjectPortBlockEntity ejectPort) {
                ejectPort.tick();
            }
        };
    }
}
