package io.github.scuba10steve.ezstorage.storage;

import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EZInventory {
    private static final Logger LOGGER = LoggerFactory.getLogger(EZInventory.class);
    
    private final List<StoredItemStack> items = new ArrayList<>();
    private long maxItems = 10000; // Default capacity
    
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
    
    public void syncFromServer(List<StoredItemStack> serverItems) {
        items.clear();
        items.addAll(serverItems);
    }
}
