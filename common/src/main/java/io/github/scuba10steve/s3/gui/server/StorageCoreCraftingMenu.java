package io.github.scuba10steve.s3.gui.server;

import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.storage.StorageInventory;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;

import java.util.List;
import java.util.Optional;

public class StorageCoreCraftingMenu extends StorageCoreMenu {

    private final Container craftMatrix;
    private final ResultContainer craftResult;

    public StorageCoreCraftingMenu(int containerId, Inventory playerInventory, net.minecraft.core.BlockPos pos) {
        super(S3Platform.getStorageCraftingMenuType(), containerId, playerInventory, pos);

        // Instantiate crafting containers
        this.craftMatrix = new TransientCraftingContainer(this, 3, 3);
        this.craftResult = new ResultContainer();

        // Add crafting result slot (slot 0)
        this.addSlot(new ResultSlot(playerInventory.player, (CraftingContainer) this.craftMatrix, craftResult, 0, 115, 117));

        // Add 3x3 crafting grid (slots 1-9)
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 3; ++col) {
                this.addSlot(new Slot(this.craftMatrix, col + row * 3, 44 + col * 18, 99 + row * 18));
            }
        }

        // Add player inventory at crafting GUI positions (slots 10-45)
        addPlayerInventory(playerInventory, 162, 220);
    }

    @Override
    public void slotsChanged(Container pContainer) {
        if (pContainer == craftMatrix) {
            if (!player.level().isClientSide) {
                updateCraftingResult();
            }
        }
    }

    private void updateCraftingResult() {
        if (!player.level().isClientSide) {
            ServerPlayer serverPlayer = (ServerPlayer) player;
            ItemStack itemstack = ItemStack.EMPTY;

            Optional<RecipeHolder<CraftingRecipe>> optional = player.level().getServer().getRecipeManager().getRecipeFor(
                    RecipeType.CRAFTING, ((CraftingContainer) craftMatrix).asCraftInput(), player.level());

            if (optional.isPresent()) {
                RecipeHolder<CraftingRecipe> recipe = optional.get();
                if (craftResult.setRecipeUsed(player.level(), serverPlayer, recipe)) {
                    itemstack = recipe.value().assemble(((CraftingContainer) craftMatrix).asCraftInput(), player.level().registryAccess());
                }
            }

            craftResult.setItem(0, itemstack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(containerId, getStateId(), 0, itemstack));
        }
    }

    @Override
    public void clicked(int slotId, int button, ClickType clickType, Player player) {
        if (slotId == 0 && clickType == ClickType.QUICK_MOVE && button == 1) {
            craftOneToCursor(player);
            return;
        }

        // Save recipe pattern before vanilla consumes ingredients
        boolean shouldRepopulate = slotId == 0
            && clickType == ClickType.PICKUP
            && slots.get(0).hasItem()
            && S3Platform.getConfig().shouldAutoRepopulateCraftingGrid();

        ItemStack[] recipePattern = null;
        if (shouldRepopulate) {
            recipePattern = new ItemStack[9];
            for (int i = 0; i < 9; i++) {
                recipePattern[i] = craftMatrix.getItem(i).copy();
            }
        }

        super.clicked(slotId, button, clickType, player);

        if (recipePattern != null) {
            moveRemaindersToStorage(player, recipePattern);
            tryToPopulateCraftingGrid(recipePattern);
            updateCraftingResult();
        }
    }

    private void craftOneToCursor(Player player) {
        Slot resultSlot = slots.get(0);
        if (!resultSlot.hasItem()) return;

        ItemStack resultTemplate = resultSlot.getItem().copy();
        ItemStack carried = getCarried();

        if (!carried.isEmpty() && !ItemStack.isSameItemSameComponents(carried, resultTemplate)) {
            return;
        }

        // Save recipe pattern for repopulation
        ItemStack[] recipePattern = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            recipePattern[i] = craftMatrix.getItem(i).copy();
        }

        int cursorLimit = Math.min(resultTemplate.getMaxStackSize(), S3Platform.getConfig().getCraftShiftClickLimit());
        int cursorCount = carried.isEmpty() ? 0 : carried.getCount();

        while (cursorCount + resultTemplate.getCount() <= cursorLimit) {
            ItemStack result = resultSlot.getItem();
            if (result.isEmpty() || !ItemStack.isSameItemSameComponents(result, resultTemplate)) {
                break;
            }

            ItemStack crafted = result.copy();
            resultSlot.onTake(player, result);

            if (carried.isEmpty()) {
                carried = crafted;
                setCarried(carried);
            } else {
                carried.grow(crafted.getCount());
            }
            cursorCount = carried.getCount();

            moveRemaindersToStorage(player, recipePattern);
            tryToPopulateCraftingGrid(recipePattern);
            updateCraftingResult();
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
            StorageInventory inventory = getInventory();
            if (inventory != null) {
                ItemStack remainder = inventory.insertItem(stackInSlot);
                slot.set(remainder);
                if (remainder.getCount() == originalStack.getCount()) {
                    return ItemStack.EMPTY;
                }
            } else {
                LOGGER.debug("StorageInventory is null, cannot move item from player inventory.");
                return ItemStack.EMPTY;
            }
        }

        if (stackInSlot.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stackInSlot.getCount() == originalStack.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stackInSlot);
        return originalStack;
    }

    /**
     * Handles shift-click crafting by continuously crafting and moving results to player inventory.
     * Automatically repopulates the crafting grid from connected storage after each craft.
     */
    private ItemStack craftMax(Player player, ItemStack originalStack) {
        Slot resultSlot = slots.get(0);
        if (!resultSlot.hasItem()) {
            return ItemStack.EMPTY;
        }

        // Store the recipe pattern for repopulation from storage
        ItemStack[] recipePattern = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            recipePattern[i] = craftMatrix.getItem(i).copy();
        }

        ItemStack resultTemplate = originalStack.copy();
        int totalResultItems = 0;
        int resultSize = resultTemplate.getCount();

        int craftLimit = S3Platform.getConfig().getCraftShiftClickLimit();
        while (totalResultItems < craftLimit) {
            ItemStack result = resultSlot.getItem();

            if (result.isEmpty() || !ItemStack.isSameItemSameComponents(result, resultTemplate)) {
                break;
            }

            ItemStack toInsert = result.copy();
            boolean movedAny = moveItemStackTo(toInsert, 10, 46, false);

            if (!movedAny || !toInsert.isEmpty()) {
                break;
            }

            resultSlot.onTake(player, result);
            totalResultItems += resultSize;

            moveRemaindersToStorage(player, recipePattern);
            tryToPopulateCraftingGrid(recipePattern);
            updateCraftingResult();
        }

        LOGGER.debug("craftMax completed: produced {} items", totalResultItems);
        return ItemStack.EMPTY;
    }

    /**
     * Moves remainder items (e.g., buckets from milk buckets, damaged tools) out of the crafting grid
     * after a craft operation. Items that don't match the original recipe pattern are considered
     * remainders and are moved to storage, player inventory, or dropped on the ground.
     */
    private void moveRemaindersToStorage(Player player, ItemStack[] recipePattern) {
        for (int i = 0; i < 9; i++) {
            ItemStack gridItem = craftMatrix.getItem(i);
            if (gridItem.isEmpty()) {
                continue;
            }
            if (ItemStack.isSameItemSameComponents(gridItem, recipePattern[i])) {
                continue;
            }
            // This slot has an item that doesn't match the recipe pattern — it's a remainder
            StorageInventory inventory = getInventory();
            if (inventory != null) {
                ItemStack remaining = inventory.insertItem(gridItem);
                if (!remaining.isEmpty()) {
                    if (!moveItemStackTo(remaining, 10, 46, false)) {
                        player.drop(remaining, false);
                    }
                }
            } else {
                if (!moveItemStackTo(gridItem, 10, 46, false)) {
                    player.drop(gridItem, false);
                }
            }
            craftMatrix.setItem(i, ItemStack.EMPTY);
        }
    }

    /**
     * Attempts to repopulate empty crafting grid slots from the connected storage.
     */
    private boolean tryToPopulateCraftingGrid(ItemStack[] recipe) {
        StorageInventory inventory = getInventory();
        if (inventory == null) {
            LOGGER.debug("Cannot populate crafting grid: StorageInventory is null.");
            return false;
        }

        boolean allItemsPopulated = true;
        for (int i = 0; i < recipe.length; i++) {
            if (craftMatrix.getItem(i).isEmpty() && !recipe[i].isEmpty()) {
                ItemStack extracted = inventory.extractItem(recipe[i], 1);

                if (!extracted.isEmpty()) {
                    craftMatrix.setItem(i, extracted);
                } else {
                    allItemsPopulated = false;
                    LOGGER.debug("Failed to extract item {} for crafting grid slot {}", recipe[i], i);
                }
            }
        }
        return allItemsPopulated;
    }

    @Override
    public void removed(Player pPlayer) {
        super.removed(pPlayer);
        clearGrid(pPlayer);
    }

    public void handleRecipeTransfer(List<ItemStack> recipe) {
        // Clear grid, returning items to storage
        clearGrid(player);

        StorageInventory inventory = getInventory();
        Inventory playerInv = player.getInventory();

        for (int i = 0; i < 9 && i < recipe.size(); i++) {
            ItemStack template = recipe.get(i);
            if (template.isEmpty()) continue;

            // Try player inventory first
            ItemStack extracted = extractOneFromPlayer(playerInv, template);

            // Fall back to storage
            if (extracted.isEmpty() && inventory != null) {
                extracted = inventory.extractItem(template, 1);
            }

            if (!extracted.isEmpty()) {
                craftMatrix.setItem(i, extracted);
            }
        }

        updateCraftingResult();
    }

    private ItemStack extractOneFromPlayer(Inventory playerInv, ItemStack template) {
        for (int i = 0; i < playerInv.getContainerSize(); i++) {
            ItemStack stack = playerInv.getItem(i);
            if (ItemStack.isSameItemSameComponents(stack, template)) {
                return stack.split(1);
            }
        }
        return ItemStack.EMPTY;
    }

    public void handleClearGrid(Player player) {
        clearGrid(player);
        updateCraftingResult();
    }

    private void clearGrid(Player playerIn) {
        for (int i = 0; i < 9; i++) {
            ItemStack stack = this.craftMatrix.getItem(i);
            if (!stack.isEmpty()) {
                StorageInventory inventory = getInventory();
                if (inventory != null) {
                    ItemStack remaining = inventory.insertItem(stack);
                    if (!remaining.isEmpty()) {
                        playerIn.drop(remaining, false);
                    }
                } else {
                    playerIn.drop(stack, false);
                }
                this.craftMatrix.setItem(i, ItemStack.EMPTY);
            }
        }
    }
}
