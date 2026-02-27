package io.github.scuba10steve.s3.events;

import io.github.scuba10steve.s3.block.StorageMultiblock;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.config.StorageConfig;
import io.github.scuba10steve.s3.ref.RefStrings;
import io.github.scuba10steve.s3.util.BlockRef;
import io.github.scuba10steve.s3.util.StorageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;

/**
 * Security event handling. Cancels unauthorized interactions with secured storage multiblocks.
 */
@EventBusSubscriber(modid = RefStrings.MODID)
public class SecurityEvents {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (shouldCancelInteraction(event.getLevel(), event.getPos(), event.getEntity())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onLeftClickBlock(PlayerInteractEvent.LeftClickBlock event) {
        if (shouldCancelInteraction(event.getLevel(), event.getPos(), event.getEntity())) {
            event.setCanceled(true);
        }
    }

    private static boolean shouldCancelInteraction(Level level, BlockPos pos, Player player) {
        if (!StorageConfig.ENABLE_SECURITY.get()) return false;
        if (level.isClientSide) return false;

        Block block = level.getBlockState(pos).getBlock();
        if (block instanceof StorageMultiblock) {
            SecurityBoxBlockEntity securityBox = StorageUtils.findSecurityBox(
                new BlockRef(block, pos), level);
            return securityBox != null && !securityBox.isPlayerAllowed(player);
        }
        return false;
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (!StorageConfig.ENABLE_SECURITY.get()) return;
        if (event.getLevel().isClientSide()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        if (!(event.getLevel() instanceof Level level)) return;

        for (BlockRef neighbor : StorageUtils.getNeighbors(event.getPos(), level)) {
            if (neighbor.block instanceof StorageMultiblock) {
                SecurityBoxBlockEntity securityBox = StorageUtils.findSecurityBox(neighbor, level);
                if (securityBox != null && !securityBox.isPlayerAllowed(player)) {
                    event.setCanceled(true);
                    return;
                }
            }
        }
    }
}
