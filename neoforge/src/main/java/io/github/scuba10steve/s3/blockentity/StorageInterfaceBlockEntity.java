package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;

import java.util.List;

/**
 * Block entity for the Storage Interface.
 * Exposes the connected Storage Core's full inventory via IItemHandler
 * for cross-mod integration (hoppers, pipes, other storage mods).
 * No buffer — delegates directly to core.
 */
public class StorageInterfaceBlockEntity extends MultiblockBlockEntity {

    private final IItemHandler itemHandler = new StorageInterfaceItemHandler();

    public StorageInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(S3Platform.getStorageInterfaceBEType(), pos, state);
    }

    /**
     * Returns the cached item handler for capability exposure.
     */
    public IItemHandler getItemHandler() {
        return itemHandler;
    }

    private boolean isDisabledByRedstone() {
        return level != null && level.hasNeighborSignal(worldPosition);
    }

    /**
     * IItemHandler that delegates directly to the connected Storage Core.
     * Reads live from the core inventory on each call for consistent state.
     * Slots 0 through N-1 map to stored item types for extraction.
     * Any slot accepts insertion.
     */
    private class StorageInterfaceItemHandler implements IItemHandler {

        private List<StoredItemStack> getItems() {
            return hasCore() ? core.getInventory().getStoredItems() : List.of();
        }

        @Override
        public int getSlots() {
            List<StoredItemStack> items = getItems();
            return Math.max(1, items.size() + 1);
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            List<StoredItemStack> items = getItems();
            if (slot < 0 || slot >= items.size()) return ItemStack.EMPTY;
            StoredItemStack stored = items.get(slot);
            int count = (int) Math.min(stored.getCount(), stored.getItemStack().getMaxStackSize());
            return stored.getItemStack().copyWithCount(count);
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!hasCore() || stack.isEmpty() || isDisabledByRedstone()) return stack;
            if (simulate) {
                long totalCount = core.getInventory().getTotalItemCount();
                long maxItems = core.getInventory().getMaxItems();
                if (totalCount >= maxItems) return stack;
                long space = maxItems - totalCount;
                int canInsert = (int) Math.min(space, stack.getCount());
                if (canInsert >= stack.getCount()) return ItemStack.EMPTY;
                return stack.copyWithCount(stack.getCount() - canInsert);
            }
            return core.insertItem(stack);
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!hasCore() || amount <= 0 || isDisabledByRedstone()) return ItemStack.EMPTY;
            List<StoredItemStack> items = getItems();
            if (slot < 0 || slot >= items.size()) return ItemStack.EMPTY;
            StoredItemStack stored = items.get(slot);
            int maxExtract = Math.min(amount, stored.getItemStack().getMaxStackSize());
            int extractAmount = (int) Math.min(maxExtract, stored.getCount());
            if (simulate) {
                return stored.getItemStack().copyWithCount(extractAmount);
            }
            return core.extractItem(stored.getItemStack(), extractAmount);
        }

        @Override
        public int getSlotLimit(int slot) {
            return Integer.MAX_VALUE;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return true;
        }
    }
}
