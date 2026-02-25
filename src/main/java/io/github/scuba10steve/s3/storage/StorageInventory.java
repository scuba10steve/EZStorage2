package io.github.scuba10steve.s3.storage;

import io.github.scuba10steve.s3.util.SortMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class StorageInventory {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageInventory.class);

    private final Map<ItemKey, StoredItemStack> items = new LinkedHashMap<>();
    private long totalCount = 0;
    private long maxItems = 0;
    private boolean hasSearchBox = false;
    private boolean hasSortBox = false;
    private SortMode sortMode = SortMode.COUNT;

    public ItemStack insertItem(ItemStack stack) {
        LOGGER.debug("StorageInventory.insertItem: {} x{}, current capacity: {}/{}",
                    stack.getItem(), stack.getCount(), totalCount, maxItems);

        if (stack.isEmpty()) {
            LOGGER.debug("Stack is empty, returning empty");
            return ItemStack.EMPTY;
        }

        if (totalCount >= maxItems) {
            LOGGER.debug("Storage full ({}/{}), returning original stack", totalCount, maxItems);
            return stack; // Full
        }

        long availableSpace = maxItems - totalCount;
        int insertAmount = (int) Math.min(availableSpace, stack.getCount());
        LOGGER.debug("Available space: {}, inserting: {}", availableSpace, insertAmount);

        ItemKey key = new ItemKey(stack);
        StoredItemStack stored = items.get(key);

        if (stored != null) {
            LOGGER.debug("Found existing stack, merging {} items", insertAmount);
            stored.addCount(insertAmount);
        } else {
            LOGGER.debug("Creating new storage entry for {} x{}", stack.getItem(), insertAmount);
            items.put(key, new StoredItemStack(stack.copyWithCount(1), insertAmount));
        }

        totalCount += insertAmount;

        ItemStack remainder = stack.copy();
        remainder.shrink(insertAmount);
        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }

    public ItemStack extractItem(ItemStack template, int amount) {
        ItemKey key = new ItemKey(template);
        StoredItemStack stored = items.get(key);

        if (stored == null) {
            return ItemStack.EMPTY;
        }

        int extractAmount = Math.min(amount, (int) stored.getCount());
        stored.removeCount(extractAmount);
        totalCount -= extractAmount;

        if (stored.getCount() <= 0) {
            items.remove(key);
        }

        return template.copyWithCount(extractAmount);
    }

    public List<StoredItemStack> getStoredItems() {
        return new ArrayList<>(items.values());
    }

    public long getTotalItemCount() {
        return totalCount;
    }

    public long getMaxItems() {
        return maxItems;
    }

    public void setMaxItems(long maxItems) {
        LOGGER.debug("Setting max items to: {}", maxItems);
        this.maxItems = maxItems;
    }

    public boolean hasSearchBox() {
        return hasSearchBox;
    }

    public void setHasSearchBox(boolean hasSearchBox) {
        this.hasSearchBox = hasSearchBox;
    }

    public boolean hasSortBox() {
        return hasSortBox;
    }

    public void setHasSortBox(boolean hasSortBox) {
        this.hasSortBox = hasSortBox;
    }

    public SortMode getSortMode() {
        return sortMode;
    }

    public void setSortMode(SortMode sortMode) {
        this.sortMode = sortMode;
    }

    /**
     * Returns items sorted by the current sort mode if a Sort Box is present,
     * otherwise returns items in their natural order.
     */
    public List<StoredItemStack> getSortedItems() {
        if (!hasSortBox || sortMode == null) {
            return new ArrayList<>(items.values());
        }
        List<StoredItemStack> sorted = new ArrayList<>(items.values());
        sorted.sort(sortMode.getComparator());
        return sorted;
    }

    public void syncFromServer(List<StoredItemStack> serverItems, long maxCapacity, boolean hasSearchBox, boolean hasSortBox, int sortModeOrdinal) {
        items.clear();
        totalCount = 0;
        for (StoredItemStack stored : serverItems) {
            items.put(new ItemKey(stored.getItemStack()), stored);
            totalCount += stored.getCount();
        }
        this.maxItems = maxCapacity;
        this.hasSearchBox = hasSearchBox;
        this.hasSortBox = hasSortBox;
        this.sortMode = SortMode.fromOrdinal(sortModeOrdinal);
        LOGGER.debug("Synced from server: {} items, max capacity: {}, has search box: {}, has sort box: {}, sort mode: {}",
                    serverItems.size(), maxCapacity, hasSearchBox, hasSortBox, sortMode);
    }

    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.putLong("MaxItems", maxItems);

        ListTag itemsList = new ListTag();
        for (StoredItemStack stored : items.values()) {
            itemsList.add(stored.save(registries));
        }
        tag.put("Items", itemsList);

        return tag;
    }

    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        maxItems = tag.getLong("MaxItems");

        items.clear();
        totalCount = 0;
        ListTag itemsList = tag.getList("Items", 10); // 10 = CompoundTag
        for (int i = 0; i < itemsList.size(); i++) {
            CompoundTag itemTag = itemsList.getCompound(i);
            StoredItemStack stored = StoredItemStack.load(itemTag, registries);
            items.put(new ItemKey(stored.getItemStack()), stored);
            totalCount += stored.getCount();
        }

        LOGGER.debug("Loaded inventory with {} items, max capacity: {}", items.size(), maxItems);
    }

    private static final class ItemKey {
        private final ItemStack stack;
        private final int hash;

        ItemKey(ItemStack stack) {
            this.stack = stack.copyWithCount(1);
            this.hash = ItemStack.hashItemAndComponents(this.stack);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof ItemKey other &&
                    ItemStack.isSameItemSameComponents(this.stack, other.stack);
        }

        @Override
        public int hashCode() {
            return hash;
        }
    }
}
