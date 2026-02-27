package io.github.scuba10steve.s3.gui.slot;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageSlot extends Slot {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageSlot.class);
    private final StorageCoreBlockEntity storageCore;
    
    public StorageSlot(Container container, StorageCoreBlockEntity storageCore, int index, int x, int y) {
        super(container, index, x, y);
        this.storageCore = storageCore;
    }
    
    @Override
    public boolean mayPlace(ItemStack stack) {
        LOGGER.debug("StorageSlot.mayPlace: {} x{}", stack.getItem(), stack.getCount());
        return !stack.isEmpty();
    }
    
    @Override
    public void set(ItemStack stack) {
        LOGGER.debug("StorageSlot.set: {} x{}", stack.getItem(), stack.getCount());
        if (!stack.isEmpty() && storageCore != null) {
            ItemStack remainder = storageCore.insertItem(stack);
            super.set(remainder);
        } else {
            super.set(stack);
        }
    }
    
    @Override
    public ItemStack remove(int amount) {
        LOGGER.debug("StorageSlot.remove: {}", amount);
        return ItemStack.EMPTY; // Storage slots don't support removal this way
    }
    
    @Override
    public int getMaxStackSize() {
        return 64;
    }
}
