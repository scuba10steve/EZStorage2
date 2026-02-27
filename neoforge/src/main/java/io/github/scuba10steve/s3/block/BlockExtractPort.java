package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Extract Port allows items to be extracted from the storage system via hoppers and pipes.
 * It has a configurable filter list with whitelist/blacklist modes.
 * Can be disabled with a redstone signal.
 */
public class BlockExtractPort extends StorageMultiblock implements EntityBlock {

    public BlockExtractPort() {
        super(Properties.of().strength(3.5f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ExtractPortBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == S3Platform.getExtractPortBEType()
            ? (lvl, pos, st, be) -> ((ExtractPortBlockEntity) be).tick()
            : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof ExtractPortBlockEntity extractPort) {
                serverPlayer.openMenu(extractPort, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
