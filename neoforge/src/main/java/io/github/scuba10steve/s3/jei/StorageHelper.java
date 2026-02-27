package io.github.scuba10steve.s3.jei;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.List;

public class StorageHelper {
    
    public static List<ItemStack> getAllStoredItems() {
        List<ItemStack> items = new ArrayList<>();
        Level level = Minecraft.getInstance().level;
        
        if (level != null) {
            // Simple approach: find storage cores in render distance
            for (int x = -64; x <= 64; x += 16) {
                for (int z = -64; z <= 64; z += 16) {
                    for (int y = -64; y <= 320; y += 16) {
                        try {
                            BlockEntity be = level.getBlockEntity(Minecraft.getInstance().player.blockPosition().offset(x, y, z));
                            if (be instanceof StorageCoreBlockEntity storageCore) {
                                for (StoredItemStack stored : storageCore.getInventory().getStoredItems()) {
                                    if (stored.getCount() > 0) {
                                        ItemStack stack = stored.getItemStack().copy();
                                        stack.setCount((int) Math.min(stored.getCount(), 64));
                                        items.add(stack);
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // Skip invalid positions
                        }
                    }
                }
            }
        }
        
        return items;
    }
    
    public static boolean hasItem(ItemStack itemStack) {
        Level level = Minecraft.getInstance().level;
        
        if (level != null && Minecraft.getInstance().player != null) {
            // Check nearby storage cores
            for (int x = -10; x <= 10; x++) {
                for (int y = -10; y <= 10; y++) {
                    for (int z = -10; z <= 10; z++) {
                        try {
                            BlockEntity be = level.getBlockEntity(Minecraft.getInstance().player.blockPosition().offset(x, y, z));
                            if (be instanceof StorageCoreBlockEntity storageCore) {
                                for (StoredItemStack stored : storageCore.getInventory().getStoredItems()) {
                                    if (ItemStack.isSameItemSameComponents(stored.getItemStack(), itemStack) && stored.getCount() > 0) {
                                        return true;
                                    }
                                }
                            }
                        } catch (Exception ignored) {
                            // Skip invalid positions
                        }
                    }
                }
            }
        }
        
        return false;
    }
}
