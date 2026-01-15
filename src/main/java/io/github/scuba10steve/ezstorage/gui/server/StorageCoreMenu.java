package io.github.scuba10steve.ezstorage.gui.server;

import io.github.scuba10steve.ezstorage.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.ezstorage.gui.slot.StorageSlot;
import io.github.scuba10steve.ezstorage.init.EZMenuTypes;
import io.github.scuba10steve.ezstorage.storage.EZInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageCoreMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreMenu.class);
    
    private final StorageCoreBlockEntity blockEntity;
    private final BlockPos pos;
    private final SimpleContainer storageContainer;

    public StorageCoreMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(EZMenuTypes.STORAGE_CORE.get(), containerId);
        this.pos = pos;
        this.blockEntity = (StorageCoreBlockEntity) playerInventory.player.level().getBlockEntity(pos);
        this.storageContainer = new SimpleContainer(54); // 6 rows of 9 slots
        
        LOGGER.debug("Creating StorageCoreMenu at {}", pos);
        
        // Add storage slots (6 rows of 9 slots)
        addStorageSlots();
        
        // Add player inventory slots
        addPlayerInventory(playerInventory);
    }
    
    public EZInventory getInventory() {
        return blockEntity != null ? blockEntity.getInventory() : null;
    }
    
    public BlockPos getPos() {
        return pos;
    }
    
    private void addStorageSlots() {
        // Storage items are rendered directly in the GUI, not as actual slots
        // This prevents conflicts with drag-and-drop and click handling
        // All storage interactions are handled via StorageClickPacket
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Player inventory (3 rows)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 140 + i * 18));
            }
        }
        
        // Player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 198));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        LOGGER.debug("quickMoveStack: slot {}", index);
        
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        
        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            itemstack = slotStack.copy();
            
            // All slots are now player inventory (no storage slots)
            // Try to insert into storage
            if (blockEntity != null) {
                ItemStack remainder = blockEntity.insertItem(slotStack);
                slot.set(remainder);
                if (remainder.isEmpty()) {
                    slot.set(ItemStack.EMPTY);
                } else {
                    slot.setChanged();
                }
            }
        }
        
        return itemstack;
    }

    @Override
    public boolean stillValid(Player player) {
        return blockEntity != null && 
               player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64;
    }
}
