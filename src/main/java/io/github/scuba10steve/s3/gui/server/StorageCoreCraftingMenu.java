package io.github.scuba10steve.s3.gui.server;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.init.EZMenuTypes;
import io.github.scuba10steve.s3.storage.EZInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeInput; // Explicitly import RecipeInput
import net.minecraft.world.item.crafting.RecipeType; // Explicitly import RecipeType
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class StorageCoreCraftingMenu extends AbstractContainerMenu {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreCraftingMenu.class);
    
    private final BlockPos pos;
    private final StorageCoreBlockEntity blockEntity;
    // Use Container for the field type to maintain compatibility with existing methods that might expect it,
    // but instantiate it as TransientCraftingContainer.
    private final Container craftMatrix; 
    private final ResultContainer craftResult;
    private final Player player;
    private final Level level;

    public StorageCoreCraftingMenu(int containerId, Inventory playerInventory, BlockPos pos) {
        super(EZMenuTypes.STORAGE_CORE_CRAFTING.get(), containerId);
        this.pos = pos;
        this.player = playerInventory.player;
        this.level = player.level();
        this.blockEntity = (StorageCoreBlockEntity) player.level().getBlockEntity(pos);
        // Instantiate using TransientCraftingContainer which implements CraftingContainer
        this.craftMatrix = new TransientCraftingContainer(this, 3, 3);
        this.craftResult = new ResultContainer();
        
        LOGGER.debug("Creating StorageCoreCraftingMenu at {}", pos);
        
        // ResultSlot constructor expects a CraftingContainer. We cast craftMatrix as it's a TransientCraftingContainer.
        this.addSlot(new ResultSlot(playerInventory.player, (CraftingContainer) this.craftMatrix, craftResult, 0, 124, 117));

        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.craftMatrix, col + row * 3, 44 + col * 18, 99 + row * 18));
            }
        }
        
        addPlayerInventory(playerInventory);
    }
    
    @Override
    public void slotsChanged(Container pContainer) {
        // Ensure we only update if the changed container is our crafting matrix
        if (pContainer == craftMatrix) {
            if (!level.isClientSide) {
                updateCraftingResult();
            }
        }
    }

    private void updateCraftingResult() {
        if (!level.isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;
            
            // The getRecipeFor method expects RecipeType, RecipeInput, and Level.
            // We cast craftMatrix to CraftingContainer and use asCraftInput().
            // Ensure CraftingInput and RecipeType are correctly imported.
            Optional<RecipeHolder<CraftingRecipe>> optional = level.getServer().getRecipeManager().getRecipeFor(
                    RecipeType.CRAFTING, ((CraftingContainer) craftMatrix).asCraftInput(), level);

            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipe = optional.get();
                // setRecipeUsed might require the server player and recipe
                if (craftResult.setRecipeUsed(level, serverPlayer, recipe)) {
                    // The assemble method expects RecipeInput.
                    itemstack = recipe.value().assemble(((CraftingContainer) craftMatrix).asCraftInput(), level.registryAccess());
                }
            }

            craftResult.setItem(0, itemstack);
            // Send the updated slot to the client.
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, getStateId(), 0, itemstack));
        }
    }
    

    public BlockPos getPos() {
        return pos;
    }

    public EZInventory getInventory() {
        if (blockEntity == null) {
            LOGGER.debug("BlockEntity is null at pos: {}", pos);
            return null;
        }
        // Correctly call insertItem and extractItem on the EZInventory instance
        return blockEntity.getInventory();
    }

    private void addPlayerInventory(Inventory playerInventory) {
        // Player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 162 + i * 18));
            }
        }
        
        // Hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 220));
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stackInSlot = slot.getItem();
        ItemStack originalStack = stackInSlot.copy();

        if (index == 0) { // Crafting result slot
            // Handle taking from the result slot, potentially triggering max crafting
            return craftMax(player, originalStack);
        }

        int playerInvStart = 10; // Start index for player inventory slots (after crafting grid)
        int playerInvEnd = 46;   // End index for player inventory slots

        // If moving from the crafting grid (indices 1-9) to the player inventory
        if (index > 0 && index < 10) {
            if (!moveItemStackTo(stackInSlot, playerInvStart, playerInvEnd, false)) {
                return ItemStack.EMPTY;
            }
        } 
        // If moving from the player inventory to the storage system
        else if (index >= playerInvStart && index < playerInvEnd) {
            EZInventory inventory = getInventory(); // Get EZInventory instance
            if (inventory != null) {
                // Correct call to insertItem for EZInventory
                ItemStack remainder = inventory.insertItem(stackInSlot); 
                slot.set(remainder); // Update the slot with the remainder
                // If no items were inserted into the storage, return empty
                if (remainder.getCount() == originalStack.getCount()) {
                    return ItemStack.EMPTY;
                }
            } else {
                 // If no storage is available, return empty
                 LOGGER.debug("EZInventory is null, cannot move item from player inventory.");
                 return ItemStack.EMPTY;
            }
        }

        // Update slot if item was moved
        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        // If the number of items in the slot hasn't changed, nothing was moved effectively
        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return originalStack;
    }

    // Placeholder for potential max crafting logic. Currently, this method is not fully implemented.
    private ItemStack craftMax(Player player, ItemStack originalStack) {
        LOGGER.warn("craftMax logic is not fully implemented and may not work as expected.");
        
        Slot resultSlot = slots.get(0);
        if (!resultSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        // Attempt to move the crafted item from the result slot to the player's inventory.
        if (!moveItemStackTo(resultSlot.getItem(), 10, 46, false)) {
            return ItemStack.EMPTY;
        }
        
        // Update the crafting grid after an item is taken.
        // A proper implementation would re-evaluate the recipe and attempt to refill the grid
        // from the player's inventory or storage using EZInventory.
        // For now, we call updateCraftingResult to refresh the UI, assuming grid changes are handled.
        updateCraftingResult();

        return originalStack; // Return the original item stack that was crafted.
    }

    // This method was present in previous versions and might be needed for repopulating the grid.
    // It's kept here for reference, but its implementation details would need careful review.
    private boolean tryToPopulateCraftingGrid(ItemStack[] recipe) {
        EZInventory inventory = getInventory();
        if (inventory == null) {
            LOGGER.debug("Cannot populate crafting grid: EZInventory is null.");
            return false;
        }

        boolean allItemsPopulated = true;
        for (int i = 0; i < recipe.length; i++) {
            if (craftMatrix.getItem(i).isEmpty() && !recipe[i].isEmpty()) {
                // Correct call to extractItem for EZInventory
                ItemStack extracted = inventory.extractItem(recipe[i], 1); 
                
                if (!extracted.isEmpty()) {
                    craftMatrix.setItem(i, extracted);
                } else {
                    allItemsPopulated = false; // Could not find required item in storage
                    LOGGER.debug("Failed to extract item {} for crafting grid slot {}", recipe[i], i);
                }
            }
        }
        return allItemsPopulated;
    }


    @Override
    public boolean stillValid(Player player) {
        // This method checks if the player can still access the container.
        // For a crafting table, it's usually based on distance.
        // For simplicity, we'll return true, but a real implementation might check player position.
        return true;
    }

    @Override
    public void removed(Player pPlayer) {
        // Called when the player closes the menu.
        // Ensure any items left in the crafting grid are dropped to the player.
        super.removed(pPlayer); // Call superclass method first
        clearGrid(pPlayer);
    }

    private void clearGrid(Player playerIn) {
        // Drops items from the crafting matrix into the player's inventory or drops them if inventory is full.
        for (int i = 0; i < 9; i++) { // Iterate through all 9 crafting slots
            ItemStack stack = this.craftMatrix.getItem(i);
            if (!stack.isEmpty()) {
                // Correct call to insertItem for EZInventory
                ItemStack remaining = this.blockEntity.getInventory().insertItem(stack);
                
                // If there are remaining items that couldn't be inserted into storage, drop them for the player.
                if (!remaining.isEmpty()) {
                    playerIn.drop(remaining, false); // `false` means not a storm drop
                }
                // Clear the crafting matrix slot regardless of whether it was inserted or dropped.
                this.craftMatrix.setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
