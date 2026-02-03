package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.init.EZBlockEntities;
import io.github.scuba10steve.s3.network.StorageSyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CraftingBoxBlockEntity extends MultiblockBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CraftingBoxBlockEntity.class);
    
    public CraftingBoxBlockEntity(BlockPos pos, BlockState state) {
        super(EZBlockEntities.CRAFTING_BOX.get(), pos, state);
        LOGGER.info("CraftingBoxBlockEntity created at {}", pos);
    }

    @Override
    public Component getDisplayName() {
        LOGGER.info("getDisplayName called");
        return Component.literal("Crafting Box");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        LOGGER.info("createMenu called for player {}", player.getName().getString());
        
        // Sync storage data to the opening player immediately
        if (hasCore() && level instanceof ServerLevel serverLevel) {
            StorageCoreBlockEntity core = getCore();
            LOGGER.info("Syncing storage data from core at {}", core.getBlockPos());
            PacketDistributor.sendToPlayer(
                (net.minecraft.server.level.ServerPlayer) player,
                new StorageSyncPacket(this.worldPosition, core.getInventory().getStoredItems(), core.getInventory().getMaxItems(), core.hasSearchBox(), core.hasSortBox(), core.getSortMode().ordinal())
            );
        } else {
            LOGGER.warn("No core found or not server level");
        }
        
        return new StorageCoreCraftingMenu(containerId, playerInventory, this.worldPosition);
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (corePos != null) {
            tag.putLong("CorePos", corePos.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CorePos")) {
            corePos = BlockPos.of(tag.getLong("CorePos"));
        }
    }
}
