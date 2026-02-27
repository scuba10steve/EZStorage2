package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.util.BlockRef;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Access Terminal allows players to open the Storage Core GUI remotely
 * by right-clicking this block instead of the core itself.
 */
public class BlockAccessTerminal extends StorageMultiblock {

    public BlockAccessTerminal() {
        super(Properties.of().strength(3.5f));
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            StorageCoreBlockEntity core = findCore(new BlockRef(this, pos), level, null);
            if (core != null && player instanceof ServerPlayer serverPlayer) {
                S3Platform.openMenu(serverPlayer, core, core.getBlockPos());
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
