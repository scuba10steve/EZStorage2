package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.config.StorageConfig;
import io.github.scuba10steve.s3.gui.server.ExtractPortMenu;
import io.github.scuba10steve.s3.init.ModBlockEntities;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import io.github.scuba10steve.s3.util.ExtractListMode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Block entity for the Extract Port.
 * Provides item extraction from storage via hoppers/pipes with filtering options.
 */
public class ExtractPortBlockEntity extends MultiblockBlockEntity implements MenuProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExtractPortBlockEntity.class);

    // Buffer slot that hoppers extract from
    private ItemStack buffer = ItemStack.EMPTY;

    // Filter list (9 slots for whitelist/blacklist)
    private final NonNullList<ItemStack> filterList = NonNullList.withSize(9, ItemStack.EMPTY);

    // Configuration
    private ExtractListMode listMode = ExtractListMode.IGNORE;
    private boolean roundRobin = false;
    private int roundRobinIndex = 0;

    public ExtractPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EXTRACT_PORT.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) return;

        if (hasCore()) {
            int extractionInterval = StorageConfig.EXTRACT_PORT_INTERVAL.get();

            // Only attempt operations at intervals to reduce network traffic
            if (level.getGameTime() % extractionInterval == 0) {
                // If buffer has items, try to push to adjacent inventories
                if (!buffer.isEmpty()) {
                    buffer = pushToAdjacentInventories(buffer);
                }

                // Pull from storage into buffer when empty and not powered
                if (buffer.isEmpty() && !level.hasNeighborSignal(worldPosition)) {
                    if (listMode != ExtractListMode.DISABLED) {
                        buffer = extractFromStorage();
                        if (!buffer.isEmpty()) {
                            setChanged();
                        }
                    }
                }
            }
        }
    }

    /**
     * Attempts to push items from the buffer into adjacent inventories.
     * @return The remaining items that couldn't be inserted
     */
    private ItemStack pushToAdjacentInventories(ItemStack stack) {
        if (stack.isEmpty() || level == null) return stack;

        for (Direction direction : Direction.values()) {
            if (stack.isEmpty()) break;

            BlockPos adjacentPos = worldPosition.relative(direction);

            // Skip the storage core - we don't want to push back into storage
            if (hasCore() && adjacentPos.equals(core.getBlockPos())) {
                continue;
            }

            // Get the item handler capability from the adjacent block
            IItemHandler handler = level.getCapability(
                Capabilities.ItemHandler.BLOCK,
                adjacentPos,
                direction.getOpposite()
            );

            if (handler != null) {
                ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                if (remainder.getCount() != stack.getCount()) {
                    setChanged();
                }
                stack = remainder;
            }
        }

        return stack;
    }

    /**
     * Extracts an item from storage based on filter settings
     */
    private ItemStack extractFromStorage() {
        if (!hasCore()) return ItemStack.EMPTY;

        List<StoredItemStack> storedItems = core.getInventory().getStoredItems();
        if (storedItems.isEmpty()) return ItemStack.EMPTY;

        // Find a valid item to extract
        int startIndex = roundRobin ? roundRobinIndex : 0;
        int size = storedItems.size();

        for (int i = 0; i < size; i++) {
            int index = (startIndex + i) % size;
            StoredItemStack stored = storedItems.get(index);

            if (stored != null && !stored.getItemStack().isEmpty() && canExtract(stored.getItemStack())) {
                if (roundRobin) {
                    roundRobinIndex = (index + 1) % Math.max(1, size);
                }

                // Extract up to a stack
                int amount = Math.min(stored.getItemStack().getMaxStackSize(), (int) stored.getCount());
                return core.extractItem(stored.getItemStack(), amount);
            }
        }

        return ItemStack.EMPTY;
    }

    /**
     * Checks if an item can be extracted based on filter settings
     */
    private boolean canExtract(ItemStack stack) {
        switch (listMode) {
            case IGNORE:
                return true;
            case WHITELIST:
                return isInFilterList(stack);
            case BLACKLIST:
                return !isInFilterList(stack);
            case DISABLED:
                return false;
            default:
                return true;
        }
    }

    /**
     * Checks if an item is in the filter list
     */
    private boolean isInFilterList(ItemStack stack) {
        for (ItemStack filter : filterList) {
            if (!filter.isEmpty() && ItemStack.isSameItem(filter, stack)) {
                return true;
            }
        }
        return false;
    }

    public IItemHandler getItemHandler() {
        return new ExtractPortItemHandler();
    }

    // Getters and setters
    public ExtractListMode getListMode() {
        return listMode;
    }

    public void setListMode(ExtractListMode mode) {
        this.listMode = mode;
        setChanged();
        syncToClients();
    }

    public void cycleListMode() {
        this.listMode = listMode.rotateMode();
        setChanged();
        syncToClients();
    }

    public boolean isRoundRobin() {
        return roundRobin;
    }

    public void setRoundRobin(boolean roundRobin) {
        this.roundRobin = roundRobin;
        setChanged();
        syncToClients();
    }

    public NonNullList<ItemStack> getFilterList() {
        return filterList;
    }

    public ItemStack getBuffer() {
        return buffer;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.putInt("ListMode", listMode.ordinal());
        tag.putBoolean("RoundRobin", roundRobin);
        tag.putInt("RoundRobinIndex", roundRobinIndex);

        if (!buffer.isEmpty()) {
            tag.put("Buffer", buffer.save(registries));
        }

        ListTag filterTag = new ListTag();
        for (int i = 0; i < filterList.size(); i++) {
            if (!filterList.get(i).isEmpty()) {
                CompoundTag itemTag = new CompoundTag();
                itemTag.putInt("Slot", i);
                filterTag.add(filterList.get(i).save(registries, itemTag));
            }
        }
        tag.put("FilterList", filterTag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        listMode = ExtractListMode.fromOrdinal(tag.getInt("ListMode"));
        roundRobin = tag.getBoolean("RoundRobin");
        roundRobinIndex = tag.getInt("RoundRobinIndex");

        if (tag.contains("Buffer")) {
            buffer = ItemStack.parseOptional(registries, tag.getCompound("Buffer"));
        }

        // Clear and load filter list
        for (int i = 0; i < filterList.size(); i++) {
            filterList.set(i, ItemStack.EMPTY);
        }
        ListTag filterTag = tag.getList("FilterList", 10);
        for (int i = 0; i < filterTag.size(); i++) {
            CompoundTag itemTag = filterTag.getCompound(i);
            int slot = itemTag.getInt("Slot");
            if (slot >= 0 && slot < filterList.size()) {
                filterList.set(slot, ItemStack.parseOptional(registries, itemTag));
            }
        }
    }

    // Client sync methods
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag, registries);
        return tag;
    }

    /**
     * Syncs this block entity's data to all tracking clients
     */
    public void syncToClients() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container.s3.extract_port");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new ExtractPortMenu(containerId, playerInventory, this);
    }

    /**
     * Internal item handler for extraction
     */
    private class ExtractPortItemHandler implements IItemHandler {
        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? buffer : ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            // Extract port does not accept items
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (slot != 0 || buffer.isEmpty() || amount <= 0) {
                return ItemStack.EMPTY;
            }

            int toExtract = Math.min(amount, buffer.getCount());
            ItemStack result = buffer.copyWithCount(toExtract);

            if (!simulate) {
                buffer.shrink(toExtract);
                if (buffer.isEmpty()) {
                    buffer = ItemStack.EMPTY;
                }
                setChanged();
            }

            return result;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return false; // No insertion allowed
        }
    }
}
