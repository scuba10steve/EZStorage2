package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.InputPortBlockEntity;
import io.github.scuba10steve.s3.init.EZBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

/**
 * The Input Port allows items to be inserted into the storage system via hoppers and pipes.
 * Items placed into the Input Port's internal slot are automatically transferred to the
 * connected Storage Core. Can be disabled with a redstone signal.
 */
public class BlockInputPort extends StorageMultiblock implements EntityBlock {

    public BlockInputPort() {
        super(Properties.of().strength(3.5f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new InputPortBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == EZBlockEntities.INPUT_PORT.get()
            ? (lvl, pos, st, be) -> ((InputPortBlockEntity) be).tick()
            : null;
    }
}
