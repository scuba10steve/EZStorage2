package io.github.scuba10steve.ezstorage.blockentity;

import io.github.scuba10steve.ezstorage.block.BlockStorage;
import io.github.scuba10steve.ezstorage.block.StorageMultiblock;
import io.github.scuba10steve.ezstorage.gui.server.StorageCoreMenu;
import io.github.scuba10steve.ezstorage.init.EZBlockEntities;
import io.github.scuba10steve.ezstorage.network.StorageSyncPacket;
import io.github.scuba10steve.ezstorage.storage.EZInventory;
import io.github.scuba10steve.ezstorage.util.BlockRef;
import io.github.scuba10steve.ezstorage.util.EZStorageUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageCoreBlockEntity extends EZBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreBlockEntity.class);
    
    private final EZInventory inventory = new EZInventory();
    private final Set<BlockRef> multiblock = new HashSet<>();
    
    public StorageCoreBlockEntity(BlockPos pos, BlockState state) {
        super(EZBlockEntities.STORAGE_CORE.get(), pos, state);
        LOGGER.info("=== StorageCoreBlockEntity created at {} ===", pos);
        LOGGER.info("StorageCoreBlockEntity created at {}", pos);
        LOGGER.debug("Initial inventory capacity: {}", inventory.getTotalItemCount());
    }

    public void scanMultiblock() {
        LOGGER.info("=== SCANNING MULTIBLOCK at {} ===", worldPosition);
        LOGGER.info("Scanning multiblock at {}", worldPosition);
        
        long totalCapacity = 0;
        multiblock.clear();
        
        BlockRef coreRef = new BlockRef(getBlockState().getBlock(), worldPosition);
        multiblock.add(coreRef);
        getValidNeighbors(coreRef);
        
        // Calculate total capacity from storage blocks
        for (BlockRef blockRef : multiblock) {
            if (blockRef.block instanceof BlockStorage storage) {
                totalCapacity += storage.getCapacity();
                LOGGER.debug("Found storage block at {} with capacity {}", blockRef.pos, storage.getCapacity());
            }
        }

        LOGGER.info("Multiblock scan complete. Found {} blocks, total capacity: {}", multiblock.size(), totalCapacity);
        inventory.setMaxItems(totalCapacity);
        setChanged();
        syncToClients();
    }
    
    private void getValidNeighbors(BlockRef br) {
        List<BlockRef> neighbors = EZStorageUtils.getNeighbors(br.pos, level);
        for (BlockRef blockRef : neighbors) {
            if (blockRef.block instanceof StorageMultiblock) {
                if (multiblock.add(blockRef)) {
                    getValidNeighbors(blockRef);
                }
            }
        }
    }
    
    public boolean isPartOfMultiblock(BlockRef blockRef) {
        return multiblock.contains(blockRef);
    }

    public ItemStack insertItem(ItemStack stack) {
        LOGGER.info("=== INSERT ATTEMPT: " + stack.getItem() + " x" + stack.getCount() + " ===");
        LOGGER.debug("Attempting to insert item: {} x{}", stack.getItem(), stack.getCount());
        
        if (stack.isEmpty()) {
            LOGGER.info("Stack is empty, returning");
            LOGGER.debug("Stack is empty, returning");
            return ItemStack.EMPTY;
        }
        
        ItemStack result = inventory.insertItem(stack);
        LOGGER.info("Insert result: " + result.getCount() + " remaining");
        LOGGER.debug("Insert result: {} remaining", result.getCount());
        
        setChanged();
        syncToClients();
        return result;
    }
    
    public ItemStack extractItem(ItemStack template, int amount) {
        LOGGER.debug("Attempting to extract item: {} x{}", template.getItem(), amount);
        
        ItemStack result = inventory.extractItem(template, amount);
        LOGGER.debug("Extract result: {} x{}", result.getItem(), result.getCount());
        
        setChanged();
        syncToClients();
        return result;
    }
    
    private void syncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayersTrackingChunk(
                serverLevel, 
                level.getChunkAt(worldPosition).getPos(),
                new StorageSyncPacket(worldPosition, inventory.getStoredItems(), inventory.getMaxItems())
            );
        }
    }
    
    public EZInventory getInventory() {
        return inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.save(registries));
        LOGGER.debug("Saved inventory data to NBT");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.load(tag.getCompound("Inventory"), registries);
            LOGGER.debug("Loaded inventory data from NBT");
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.ezstorage.storage_core");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Sync storage data to the opening player immediately
        if (level instanceof ServerLevel serverLevel) {
            PacketDistributor.sendToPlayer(
                (net.minecraft.server.level.ServerPlayer) player,
                new StorageSyncPacket(worldPosition, inventory.getStoredItems(), inventory.getMaxItems())
            );
        }
        return new StorageCoreMenu(containerId, playerInventory, this.worldPosition);
    }
}
