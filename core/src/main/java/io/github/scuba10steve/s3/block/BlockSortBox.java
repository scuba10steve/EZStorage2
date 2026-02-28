package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.SortBoxBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;

/**
 * The Sort Box block enables sorting functionality in the Storage Core GUI.
 * When placed adjacent to a Storage Core multiblock, it enables the sort button
 * in the Storage Core GUI, allowing players to cycle through different sort modes.
 *
 * This block does not have its own GUI - it simply acts as a detection flag.
 */
public class BlockSortBox extends StorageMultiblock implements EntityBlock {
    public BlockSortBox() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SortBoxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // No tick logic needed for Sort Box
        return null;
    }
}
