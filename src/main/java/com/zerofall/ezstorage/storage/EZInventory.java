package com.zerofall.ezstorage.storage;

import net.minecraft.world.item.ItemStack;
import java.util.ArrayList;
import java.util.List;

public class EZInventory {
    private final List<StoredItemStack> items = new ArrayList<>();
    private long maxItems = 10000; // Default capacity
    
    public ItemStack insertItem(ItemStack stack) {
        if (stack.isEmpty()) return ItemStack.EMPTY;
        
        long totalCount = getTotalItemCount();
        if (totalCount >= maxItems) {
            return stack; // Full
        }
        
        long availableSpace = maxItems - totalCount;
        int insertAmount = (int) Math.min(availableSpace, stack.getCount());
        
        // Try to merge with existing stacks
        for (StoredItemStack stored : items) {
            if (ItemStack.isSameItemSameComponents(stored.getItemStack(), stack)) {
                stored.addCount(insertAmount);
                
                ItemStack remainder = stack.copy();
                remainder.shrink(insertAmount);
                return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
            }
        }
        
        // Create new entry
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
    
    public void setMaxItems(long maxItems) {
        this.maxItems = maxItems;
    }
    
    public void syncFromServer(List<StoredItemStack> serverItems) {
        items.clear();
        items.addAll(serverItems);
    }
}
