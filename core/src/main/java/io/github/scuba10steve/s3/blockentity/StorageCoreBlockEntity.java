package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.block.BlockCraftingBox;
import io.github.scuba10steve.s3.block.BlockSearchBox;
import io.github.scuba10steve.s3.block.BlockSecurityBox;
import io.github.scuba10steve.s3.block.BlockSortBox;
import io.github.scuba10steve.s3.block.BlockStorage;
import io.github.scuba10steve.s3.block.StorageMultiblock;
import io.github.scuba10steve.s3.util.SortMode;
import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.gui.server.StorageCoreMenu;
import io.github.scuba10steve.s3.network.StorageSyncPacket;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.storage.StorageInventory;
import io.github.scuba10steve.s3.util.BlockRef;
import io.github.scuba10steve.s3.util.StorageUtils;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageCoreBlockEntity extends BaseBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreBlockEntity.class);

    private final StorageInventory inventory = new StorageInventory();
    private final Set<BlockRef> multiblock = new HashSet<>();
    private boolean hasCraftingBox = false;
    private boolean hasSearchBox = false;
    private boolean hasSortBox = false;
    private boolean hasSecurityBox = false;
    private SortMode sortMode = SortMode.COUNT;
    private boolean needsScan = true;

    // Sync throttling to prevent rapid consecutive syncs causing visual flicker
    private long lastSyncTime = 0;

    public StorageCoreBlockEntity(BlockPos pos, BlockState state) {
        super(S3Platform.getStorageCoreBEType(), pos, state);
        LOGGER.debug("StorageCoreBlockEntity created at {}", pos);
        LOGGER.debug("Initial inventory capacity: {}", inventory.getTotalItemCount());
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        if (needsScan) {
            scanMultiblock();
            needsScan = false;
        }
    }

    public void scanMultiblock() {
        LOGGER.debug("Scanning multiblock at {}", worldPosition);
        
        long totalCapacity = 0;
        multiblock.clear();
        hasCraftingBox = false;
        hasSearchBox = false;
        hasSortBox = false;
        hasSecurityBox = false;
        
        BlockRef coreRef = new BlockRef(getBlockState().getBlock(), worldPosition);
        multiblock.add(coreRef);
        getValidNeighbors(coreRef);
        
        // Calculate total capacity from storage blocks and detect crafting boxes
        for (BlockRef blockRef : multiblock) {
            if (blockRef.block instanceof BlockStorage storage) {
                totalCapacity += storage.getCapacity();
                LOGGER.debug("Found storage block at {} with capacity {}", blockRef.pos, storage.getCapacity());
            } else if (blockRef.block instanceof BlockCraftingBox) {
                hasCraftingBox = true;
                LOGGER.debug("Found crafting box at {}", blockRef.pos);
            } else if (blockRef.block instanceof BlockSearchBox) {
                hasSearchBox = true;
                LOGGER.debug("Found search box at {}", blockRef.pos);
            } else if (blockRef.block instanceof BlockSortBox) {
                hasSortBox = true;
                LOGGER.debug("Found sort box at {}", blockRef.pos);
            } else if (blockRef.block instanceof BlockSecurityBox) {
                hasSecurityBox = true;
                LOGGER.debug("Found security box at {}", blockRef.pos);
            }
        }

        LOGGER.debug("Multiblock scan complete. Found {} blocks, total capacity: {}, has crafting box: {}, has search box: {}, has sort box: {}, has security box: {}",
                   multiblock.size(), totalCapacity, hasCraftingBox, hasSearchBox, hasSortBox, hasSecurityBox);
        inventory.setMaxItems(totalCapacity);
        setChanged();
        syncToClients();
    }
    
    private void getValidNeighbors(BlockRef br) {
        List<BlockRef> neighbors = StorageUtils.getNeighbors(br.pos, level);
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
        LOGGER.debug("Attempting to insert item: {} x{}", stack.getItem(), stack.getCount());

        if (stack.isEmpty()) {
            LOGGER.debug("Stack is empty, returning");
            return ItemStack.EMPTY;
        }

        ItemStack result = inventory.insertItem(stack);
        LOGGER.debug("Insert result: {} remaining", result.getCount());

        setChanged();
        forceSyncToClients(); // Always sync inventory changes immediately
        return result;
    }

    public ItemStack extractItem(ItemStack template, int amount) {
        LOGGER.debug("Attempting to extract item: {} x{}", template.getItem(), amount);

        ItemStack result = inventory.extractItem(template, amount);
        LOGGER.debug("Extract result: {} x{}", result.getItem(), result.getCount());

        setChanged();
        forceSyncToClients(); // Always sync inventory changes immediately
        return result;
    }
    
    private void syncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            // Throttle syncs to prevent visual flicker from rapid consecutive updates
            long currentTime = level.getGameTime();
            int minSyncInterval = S3Platform.getConfig().getMinSyncInterval();
            if (minSyncInterval > 0 && currentTime - lastSyncTime < minSyncInterval) {
                return; // Skip this sync, another one happened very recently
            }
            lastSyncTime = currentTime;

            S3Platform.getNetworkHelper().sendToPlayersTrackingChunk(
                serverLevel,
                worldPosition,
                new StorageSyncPacket(worldPosition, inventory.getStoredItems(), inventory.getMaxItems(), hasSearchBox, hasSortBox, sortMode.ordinal())
            );
        }
    }

    /**
     * Forces an immediate sync to clients, bypassing throttling.
     * Use sparingly - only for critical updates like inventory changes.
     */
    public void forceSyncToClients() {
        if (level instanceof ServerLevel serverLevel) {
            lastSyncTime = level.getGameTime();
            S3Platform.getNetworkHelper().sendToPlayersTrackingChunk(
                serverLevel,
                worldPosition,
                new StorageSyncPacket(worldPosition, inventory.getStoredItems(), inventory.getMaxItems(), hasSearchBox, hasSortBox, sortMode.ordinal())
            );
        }
    }
    
    public StorageInventory getInventory() {
        return inventory;
    }

    public boolean hasSearchBox() {
        return hasSearchBox;
    }

    public boolean hasSortBox() {
        return hasSortBox;
    }

    public boolean hasSecurityBox() {
        return hasSecurityBox;
    }

    public SortMode getSortMode() {
        return sortMode;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.put("Inventory", inventory.save(registries));
        tag.putInt("SortMode", sortMode.ordinal());
        LOGGER.debug("Saved inventory data to NBT");
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Inventory")) {
            inventory.load(tag.getCompound("Inventory"), registries);
            LOGGER.debug("Loaded inventory data from NBT");
        }
        if (tag.contains("SortMode")) {
            sortMode = SortMode.fromOrdinal(tag.getInt("SortMode"));
            LOGGER.debug("Loaded sort mode from NBT: {}", sortMode);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.s3.storage_core");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // Sync storage data to the opening player immediately
        if (level instanceof ServerLevel serverLevel) {
            S3Platform.getNetworkHelper().sendToPlayer(
                (net.minecraft.server.level.ServerPlayer) player,
                new StorageSyncPacket(worldPosition, inventory.getStoredItems(), inventory.getMaxItems(), hasSearchBox, hasSortBox, sortMode.ordinal())
            );
        }
        
        // Open crafting GUI if we have a crafting box, otherwise normal storage GUI
        if (hasCraftingBox) {
            LOGGER.debug("Opening crafting GUI for storage core at {}", worldPosition);
            return new StorageCoreCraftingMenu(containerId, playerInventory, this.worldPosition);
        } else {
            return new StorageCoreMenu(containerId, playerInventory, this.worldPosition);
        }
    }
}
