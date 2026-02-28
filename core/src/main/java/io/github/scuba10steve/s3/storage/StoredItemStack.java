package io.github.scuba10steve.s3.storage;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class StoredItemStack {
    private final ItemStack itemStack;
    private long count;
    
    public StoredItemStack(ItemStack itemStack, long count) {
        this.itemStack = itemStack.copy();
        this.count = count;
    }
    
    public ItemStack getItemStack() {
        return itemStack.copy();
    }
    
    public long getCount() {
        return count;
    }
    
    public void addCount(long amount) {
        this.count += amount;
    }
    
    public void removeCount(long amount) {
        this.count = Math.max(0, this.count - amount);
    }
    
    public void setCount(long count) {
        this.count = Math.max(0, count);
    }
    
    public CompoundTag save(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("Item", itemStack.save(registries));
        tag.putLong("Count", count);
        return tag;
    }
    
    public static StoredItemStack load(CompoundTag tag, HolderLookup.Provider registries) {
        ItemStack stack = ItemStack.parseOptional(registries, tag.getCompound("Item"));
        long count = tag.getLong("Count");
        return new StoredItemStack(stack, count);
    }
}
