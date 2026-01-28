package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.SearchBoxBlockEntity;
import io.github.scuba10steve.s3.init.EZBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

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
        return type == EZBlockEntities.SEARCH_BOX.get() ? (level1, pos, state1, blockEntity) -> {
            // Ticker logic will be implemented later
        } : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof SearchBoxBlockEntity searchBox) {
                player.openMenu(searchBox, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
