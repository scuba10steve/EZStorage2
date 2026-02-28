package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.SearchBoxBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;

/**
 * The Search Box block enables search functionality in the Storage Core GUI.
 * When placed adjacent to a Storage Core multiblock, it enables the search field
 * in the Storage Core GUI, allowing players to filter stored items.
 *
 * This block does not have its own GUI - it simply acts as a detection flag.
 */
public class BlockSearchBox extends StorageMultiblock implements EntityBlock {
    public BlockSearchBox() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SearchBoxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        // No tick logic needed for Search Box
        return null;
    }
}
