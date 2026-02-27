package io.github.scuba10steve.s3.jei;

import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.init.ModMenuTypes;
import io.github.scuba10steve.s3.network.RecipeTransferPacket;
import io.github.scuba10steve.s3.storage.StorageInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.ingredient.IRecipeSlotView;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class StorageRecipeTransferHandler implements IRecipeTransferHandler<StorageCoreCraftingMenu, RecipeHolder<CraftingRecipe>> {

    private final IRecipeTransferHandlerHelper helper;

    public StorageRecipeTransferHandler(IRecipeTransferHandlerHelper helper) {
        this.helper = helper;
    }

    @Override
    public Class<StorageCoreCraftingMenu> getContainerClass() {
        return StorageCoreCraftingMenu.class;
    }

    @Override
    public Optional<MenuType<StorageCoreCraftingMenu>> getMenuType() {
        return Optional.of(ModMenuTypes.STORAGE_CORE_CRAFTING.get());
    }

    @Override
    public RecipeType<RecipeHolder<CraftingRecipe>> getRecipeType() {
        return RecipeTypes.CRAFTING;
    }

    @Override
    public @Nullable IRecipeTransferError transferRecipe(
            StorageCoreCraftingMenu container,
            RecipeHolder<CraftingRecipe> recipe,
            IRecipeSlotsView recipeSlots,
            Player player,
            boolean maxTransfer,
            boolean doTransfer) {

        Map<Integer, Ingredient> guiSlotMap = helper.getGuiSlotIndexToIngredientMap(recipe);

        // Count available items from player inventory
        Map<StackKey, Integer> playerAvailable = countPlayerInventory(player.getInventory());

        // Count available items from storage
        Map<StackKey, Integer> storageAvailable = countStorageInventory(container.getInventory());

        // Also count items currently in the crafting grid (they'll be returned to storage during transfer)
        for (int i = 0; i < 9; i++) {
            ItemStack gridItem = container.getSlot(1 + i).getItem();
            if (!gridItem.isEmpty()) {
                StackKey key = new StackKey(gridItem);
                storageAvailable.merge(key, gridItem.getCount(), Integer::sum);
            }
        }

        // Resolve each ingredient to a concrete ItemStack
        ItemStack[] resolved = new ItemStack[9];
        Arrays.fill(resolved, ItemStack.EMPTY);
        List<Ingredient> missingIngredients = new ArrayList<>();

        for (var entry : guiSlotMap.entrySet()) {
            int guiSlot = entry.getKey();
            Ingredient ingredient = entry.getValue();
            if (ingredient.isEmpty()) continue;

            ItemStack found = findAndConsume(ingredient, playerAvailable, storageAvailable);
            if (found.isEmpty()) {
                missingIngredients.add(ingredient);
            } else {
                resolved[guiSlot] = found;
            }
        }

        if (!missingIngredients.isEmpty()) {
            List<IRecipeSlotView> inputSlots = recipeSlots.getSlotViews(RecipeIngredientRole.INPUT);
            List<IRecipeSlotView> missingSlotViews = new ArrayList<>();
            for (IRecipeSlotView view : inputSlots) {
                boolean isMissing = view.getItemStacks().anyMatch(viewStack ->
                    missingIngredients.stream().anyMatch(ing -> ing.test(viewStack))
                );
                if (isMissing) {
                    missingSlotViews.add(view);
                }
            }

            if (missingSlotViews.isEmpty()) {
                return helper.createUserErrorWithTooltip(
                    Component.translatable("jei.tooltip.error.recipe.transfer.missing")
                );
            }
            return helper.createUserErrorForMissingSlots(
                Component.translatable("jei.tooltip.error.recipe.transfer.missing"),
                missingSlotViews
            );
        }

        if (doTransfer) {
            PacketDistributor.sendToServer(new RecipeTransferPacket(Arrays.asList(resolved)));
        }

        return null;
    }

    private ItemStack findAndConsume(Ingredient ingredient, Map<StackKey, Integer> playerAvailable, Map<StackKey, Integer> storageAvailable) {
        // Try player inventory first
        for (var entry : playerAvailable.entrySet()) {
            if (entry.getValue() > 0 && ingredient.test(entry.getKey().stack)) {
                entry.setValue(entry.getValue() - 1);
                return entry.getKey().stack.copy();
            }
        }
        // Try storage
        for (var entry : storageAvailable.entrySet()) {
            if (entry.getValue() > 0 && ingredient.test(entry.getKey().stack)) {
                entry.setValue(entry.getValue() - 1);
                return entry.getKey().stack.copy();
            }
        }
        return ItemStack.EMPTY;
    }

    private Map<StackKey, Integer> countPlayerInventory(Inventory inventory) {
        Map<StackKey, Integer> counts = new LinkedHashMap<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty()) {
                StackKey key = new StackKey(stack);
                counts.merge(key, stack.getCount(), Integer::sum);
            }
        }
        return counts;
    }

    private Map<StackKey, Integer> countStorageInventory(@Nullable StorageInventory inventory) {
        Map<StackKey, Integer> counts = new LinkedHashMap<>();
        if (inventory == null) return counts;
        for (StoredItemStack stored : inventory.getStoredItems()) {
            StackKey key = new StackKey(stored.getItemStack());
            counts.merge(key, (int) Math.min(stored.getCount(), Integer.MAX_VALUE), Integer::sum);
        }
        return counts;
    }

    private record StackKey(ItemStack stack) {
        StackKey(ItemStack stack) {
            this.stack = stack.copyWithCount(1);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof StackKey other && ItemStack.isSameItemSameComponents(stack, other.stack);
        }

        @Override
        public int hashCode() {
            return ItemStack.hashItemAndComponents(stack);
        }
    }
}
