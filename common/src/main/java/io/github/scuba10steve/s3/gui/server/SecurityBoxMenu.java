package io.github.scuba10steve.s3.gui.server;

import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.network.SecuritySyncPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Menu for the Security Box player management GUI.
 * Contains only player inventory slots — no item slots since this is a player management UI.
 */
public class SecurityBoxMenu extends AbstractContainerMenu {

    private final SecurityBoxBlockEntity blockEntity;
    private final BlockPos pos;

    // Client constructor
    public SecurityBoxMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, getBlockEntity(playerInventory, data.readBlockPos()));
    }

    // Server constructor
    public SecurityBoxMenu(int containerId, Inventory playerInventory, SecurityBoxBlockEntity blockEntity) {
        super(S3Platform.getSecurityBoxMenuType(), containerId);
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();

        int yOffset = 64;

        // Player inventory (3 rows)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18 + yOffset));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            addSlot(new Slot(playerInventory, i, 8 + i * 18, 142 + yOffset));
        }

        // Sync whitelist to the opening player
        if (playerInventory.player instanceof ServerPlayer serverPlayer) {
            S3Platform.getNetworkHelper().sendToPlayer(
                serverPlayer,
                new SecuritySyncPacket(pos, blockEntity.getAllowedPlayers())
            );
        }
    }

    private static SecurityBoxBlockEntity getBlockEntity(Inventory playerInventory, BlockPos pos) {
        if (playerInventory.player.level().getBlockEntity(pos) instanceof SecurityBoxBlockEntity be) {
            return be;
        }
        throw new IllegalStateException("No SecurityBoxBlockEntity at " + pos);
    }

    public SecurityBoxBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public BlockPos getPos() {
        return pos;
    }

    /**
     * Handle remove-player actions via clickMenuButton.
     * The button id is the index in the allowed players list to remove.
     */
    @Override
    public boolean clickMenuButton(Player player, int id) {
        if (id >= 0 && id < blockEntity.getAllowedPlayerCount()) {
            var removed = blockEntity.getAllowedPlayers().get(id);
            blockEntity.removeAllowedPlayer(removed.id());
            return true;
        }
        return false;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            result = slotStack.copy();

            // Player inventory to hotbar or vice versa
            if (index < 27) {
                if (!this.moveItemStackTo(slotStack, 27, 36, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!this.moveItemStackTo(slotStack, 0, 27, false)) {
                    return ItemStack.EMPTY;
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
    public boolean stillValid(Player player) {
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }
}
