package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.CraftingBoxBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;

public class BlockCraftingBox extends StorageMultiblock implements EntityBlock {
    public BlockCraftingBox() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new CraftingBoxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == S3Platform.getCraftingBoxBEType() ? (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof CraftingBoxBlockEntity craftingBox) {
                craftingBox.tick();
            }
        } : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CraftingBoxBlockEntity craftingBox) {
                S3Platform.openMenu(serverPlayer, craftingBox, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
