package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * The Security Box restricts access to the storage multiblock.
 * When present, only whitelisted players can interact with any block in the connected multiblock.
 * Unbreakable by normal means — requires a Key item held by an allowed player.
 */
public class BlockSecurityBox extends StorageMultiblock implements EntityBlock {

    public BlockSecurityBox() {
        // Unbreakable like bedrock: -1.0f destroy time, 3600000.0f explosion resistance
        super(Properties.of().strength(-1.0f, 3600000.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SecurityBoxBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == S3Platform.getSecurityBoxBEType()
            ? (lvl, pos, st, be) -> ((SecurityBoxBlockEntity) be).tick()
            : null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide && placer instanceof Player player) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof SecurityBoxBlockEntity securityBox) {
                securityBox.addAllowedPlayer(player);
            }
        }
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        BlockEntity be = level.getBlockEntity(pos);
        if (!(be instanceof SecurityBoxBlockEntity securityBox)) {
            return InteractionResult.FAIL;
        }

        if (!securityBox.isPlayerAllowed(player)) {
            return InteractionResult.FAIL;
        }

        if (!level.isClientSide) {
            // Check if player is holding the Key item
            ItemStack mainHand = player.getMainHandItem();
            if (!mainHand.isEmpty() && mainHand.getItem() == S3Platform.getKeyItem()) {
                // Destroy the security box (drops itself)
                level.destroyBlock(pos, true);
            } else {
                // Open the Security Box GUI
                if (player instanceof ServerPlayer serverPlayer) {
                    S3Platform.openMenu(serverPlayer, securityBox, pos);
                }
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
