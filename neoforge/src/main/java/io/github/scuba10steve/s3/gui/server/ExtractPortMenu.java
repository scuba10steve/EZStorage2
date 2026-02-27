package io.github.scuba10steve.s3.gui.server;

import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menu for the Extract Port configuration GUI.
 */
public class ExtractPortMenu extends AbstractContainerMenu {

    private final ExtractPortBlockEntity blockEntity;
    private final BlockPos pos;

    // Client constructor
    public ExtractPortMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, data.readBlockPos()));
    }

    // Server constructor
    public ExtractPortMenu(int containerId, Inventory playerInventory, ExtractPortBlockEntity blockEntity) {
        super(S3Platform.getExtractPortMenuType(), containerId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();

        // Add filter slots (9 slots in a row)
        for (int i = 0; i < 9; i++) {
            addSlot(new FilterSlot(blockEntity, i, 8 + i * 18, 20));
        }

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 69 + row * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 127));
        }
    }

    private static ExtractPortBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof ExtractPortBlockEntity be) {
            return be;
        }
        throw new IllegalStateException("No ExtractPortBlockEntity at " + pos);
    }

    public ExtractPortBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            // From filter slots (0-8) to player inventory
            if (index < 9) {
                if (!this.moveItemStackTo(slotStack, 9, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            }
            // From player inventory to somewhere else in player inventory
            else {
                // Player inventory to hotbar
                if (index < 36) {
                    if (!this.moveItemStackTo(slotStack, 36, 45, false)) {
                        return ItemStack.EMPTY;
                    }
                }
                // Hotbar to player inventory
                else {
                    if (!this.moveItemStackTo(slotStack, 9, 36, false)) {
                        return ItemStack.EMPTY;
                    }
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return result;
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        // Handle filter slot clicks specially - just copy the item type
        if (slotId >= 0 && slotId < 9) {
            Slot slot = this.slots.get(slotId);
            ItemStack carried = getCarried();

            if (clickType == ClickType.PICKUP || clickType == ClickType.QUICK_MOVE) {
                if (carried.isEmpty()) {
                    // Clear the filter slot
                    slot.set(ItemStack.EMPTY);
                } else {
                    // Set filter to a copy with count 1
                    slot.set(carried.copyWithCount(1));
                }
                blockEntity.setChanged();
                return;
            }
        }

        super.clicked(slotId, button, clickType, player);
    }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    /**
     * Filter slot that only stores item type references (count always 1)
     */
    private static class FilterSlot extends Slot {
        private final ExtractPortBlockEntity blockEntity;
        private final int filterIndex;

        public FilterSlot(ExtractPortBlockEntity blockEntity, int index, int x, int y) {
            super(new FilterSlotContainer(blockEntity, index), 0, x, y);
            this.blockEntity = blockEntity;
            this.filterIndex = index;
        }

        @Override
        public ItemStack getItem() {
            return blockEntity.getFilterList().get(filterIndex);
        }

        @Override
        public void set(ItemStack stack) {
            blockEntity.getFilterList().set(filterIndex, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
            setChanged();
        }

        @Override
        public int getMaxStackSize() {
            return 1;
        }

        @Override
        public boolean mayPickup(Player player) {
            return true;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            return true;
        }
    }

    /**
     * Dummy container for filter slots
     */
    private static class FilterSlotContainer implements net.minecraft.world.Container {
        private final ExtractPortBlockEntity blockEntity;
        private final int index;

        public FilterSlotContainer(ExtractPortBlockEntity blockEntity, int index) {
            this.blockEntity = blockEntity;
            this.index = index;
        }

        @Override
        public int getContainerSize() { return 1; }

        @Override
        public boolean isEmpty() { return blockEntity.getFilterList().get(index).isEmpty(); }

        @Override
        public ItemStack getItem(int slot) { return blockEntity.getFilterList().get(index); }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            ItemStack stack = blockEntity.getFilterList().get(index);
            blockEntity.getFilterList().set(index, ItemStack.EMPTY);
            return stack;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return removeItem(slot, 1);
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
            blockEntity.getFilterList().set(index, stack.isEmpty() ? ItemStack.EMPTY : stack.copyWithCount(1));
        }

        @Override
        public void setChanged() { blockEntity.setChanged(); }

        @Override
        public boolean stillValid(Player player) { return true; }

        @Override
        public void clearContent() { blockEntity.getFilterList().set(index, ItemStack.EMPTY); }
    }
}
