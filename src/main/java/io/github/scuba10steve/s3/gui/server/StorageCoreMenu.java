package io.github.scuba10steve.s3.gui.server;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.gui.slot.StorageSlot;
import io.github.scuba10steve.s3.init.EZMenuTypes;
import io.github.scuba10steve.s3.storage.EZInventory;
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
    protected static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreMenu.class);

    protected final StorageCoreBlockEntity blockEntity;
    protected final BlockPos pos;
    protected final Player player;
    private final SimpleContainer storageContainer;

    public StorageCoreMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        this(EZMenuTypes.STORAGE_CORE.get(), containerId, playerInventory, pos);

        // Add storage slots (6 rows of 9 slots)
        addStorageSlots();

        // Add player inventory slots at default positions
        addPlayerInventory(playerInventory, 140, 198);
    }

    /**
     * Protected constructor for subclasses to specify their own menu type.
     */
    protected StorageCoreMenu(net.minecraft.world.inventory.MenuType<?> menuType, int containerId, Inventory playerInventory, BlockPos pos) {
        super(menuType, containerId);
        this.pos = pos;
        this.player = playerInventory.player;
        this.blockEntity = (StorageCoreBlockEntity) player.level().getBlockEntity(pos);
        this.storageContainer = new SimpleContainer(54); // 6 rows of 9 slots

        LOGGER.debug("Creating {} at {}", getClass().getSimpleName(), pos);
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

    /**
     * Adds player inventory slots at the specified Y positions.
     * @param playerInventory The player's inventory
     * @param inventoryY Y position for the main inventory (3 rows)
     * @param hotbarY Y position for the hotbar
     */
    protected void addPlayerInventory(Inventory playerInventory, int inventoryY, int hotbarY) {
        // Player inventory (3 rows)
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, inventoryY + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, hotbarY));
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
