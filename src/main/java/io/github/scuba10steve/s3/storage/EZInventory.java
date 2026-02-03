package io.github.scuba10steve.s3.storage;

import io.github.scuba10steve.s3.util.SortMode;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EZInventory {
    private static final Logger LOGGER = LoggerFactory.getLogger(EZInventory.class);
    
    private final List<StoredItemStack> items = new ArrayList<>();
    private long maxItems = 10000; // Default capacity
    private boolean hasSearchBox = false;
    private boolean hasSortBox = false;
    private SortMode sortMode = SortMode.COUNT;
    
    public ItemStack insertItem(ItemStack stack) {
        LOGGER.debug("EZInventory.insertItem: {} x{}, current capacity: {}/{}", 
                    stack.getItem(), stack.getCount(), getTotalItemCount(), maxItems);
        
        if (stack.isEmpty()) {
            LOGGER.debug("Stack is empty, returning empty");
            return ItemStack.EMPTY;
        }
        
        long totalCount = getTotalItemCount();
        if (totalCount >= maxItems) {
            LOGGER.debug("Storage full ({}/{}), returning original stack", totalCount, maxItems);
            return stack; // Full
        }
        
        long availableSpace = maxItems - totalCount;
        int insertAmount = (int) Math.min(availableSpace, stack.getCount());
        LOGGER.debug("Available space: {}, inserting: {}", availableSpace, insertAmount);
        
        // Try to merge with existing stacks
        for (StoredItemStack stored : items) {
            if (ItemStack.isSameItemSameComponents(stored.getItemStack(), stack)) {
                LOGGER.debug("Found existing stack, merging {} items", insertAmount);
                stored.addCount(insertAmount);
                
                ItemStack remainder = stack.copy();
                remainder.shrink(insertAmount);
                return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
            }
        }
        
        // Create new entry
        LOGGER.debug("Creating new storage entry for {} x{}", stack.getItem(), insertAmount);
        items.add(new StoredItemStack(stack.copyWithCount(1), insertAmount));
        
        ItemStack remainder = stack.copy();
        remainder.shrink(insertAmount);
        return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
    }
    
    public ItemStack extractItem(ItemStack template, int amount) {
        for (StoredItemStack stored : items) {
            if (ItemStack.isSameItemSameComponents(stored.getItemStack(), template)) {
                int extractAmount = Math.min(amount, (int) stored.getCount());
                stored.removeCount(extractAmount);
                
                if (stored.getCount() <= 0) {
                    items.remove(stored);
                }
                
                return template.copyWithCount(extractAmount);
            }
        }
        return ItemStack.EMPTY;
    }
    
    public List<StoredItemStack> getStoredItems() {
        return new ArrayList<>(items);
    }
    
    public long getTotalItemCount() {
        return items.stream().mapToLong(StoredItemStack::getCount).sum();
    }
    
    public long getMaxItems() {
        return maxItems;
    }
    
    public void setMaxItems(long maxItems) {
        LOGGER.info("Setting max items to: {}", maxItems);
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
            return new ArrayList<>(items);
        }
        List<StoredItemStack> sorted = new ArrayList<>(items);
        sorted.sort(sortMode.getComparator());
        return sorted;
    }

    public void syncFromServer(List<StoredItemStack> serverItems, long maxCapacity, boolean hasSearchBox, boolean hasSortBox, int sortModeOrdinal) {
        items.clear();
        items.addAll(serverItems);
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
        for (StoredItemStack stored : items) {
            itemsList.add(stored.save(registries));
        }
        tag.put("Items", itemsList);
        
        return tag;
    }
    
    public void load(CompoundTag tag, HolderLookup.Provider registries) {
        maxItems = tag.getLong("MaxItems");
        
        items.clear();
        ListTag itemsList = tag.getList("Items", 10); // 10 = CompoundTag
        for (int i = 0; i < itemsList.size(); i++) {
            CompoundTag itemTag = itemsList.getCompound(i);
            items.add(StoredItemStack.load(itemTag, registries));
        }
        
        LOGGER.info("Loaded inventory with {} items, max capacity: {}", items.size(), maxItems);
    }
}
