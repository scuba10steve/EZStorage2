package io.github.scuba10steve.s3.jei;

import io.github.scuba10steve.s3.gui.client.AbstractStorageScreen;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import mezz.jei.api.gui.builder.IClickableIngredientFactory;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.runtime.IClickableIngredient;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * JEI handler that allows recipe/usage lookup (R/U keys) for items displayed in storage screens.
 * This handles the custom-rendered storage items that aren't in normal inventory slots.
 */
public class StorageGuiContainerHandler<T extends AbstractStorageScreen<?>> implements IGuiContainerHandler<T> {

    @Override
    public Optional<? extends IClickableIngredient<?>> getClickableIngredientUnderMouse(
            IClickableIngredientFactory builder,
            T screen,
            double mouseX,
            double mouseY) {

        // Get the storage slot index at the mouse position
        Integer slotIndex = getStorageSlotAt(screen, (int) mouseX, (int) mouseY);
        if (slotIndex == null) {
            return Optional.empty();
        }

        // Get the displayed items (accounts for search filtering and sorting)
        List<StoredItemStack> storedItems = screen.getDisplayItems();
        if (slotIndex >= storedItems.size()) {
            return Optional.empty();
        }

        StoredItemStack stored = storedItems.get(slotIndex);
        if (stored == null || stored.getItemStack().isEmpty()) {
            return Optional.empty();
        }

        // Calculate the slot area for the clickable ingredient
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();
        int row = (slotIndex / 9) - screen.getScrollRow();
        int col = slotIndex % 9;
        int slotX = guiLeft + 8 + (col * 18);
        int slotY = guiTop + 18 + (row * 18);
        Rect2i slotArea = new Rect2i(slotX, slotY, 16, 16);

        // Create and return the clickable ingredient using the builder pattern
        ItemStack stack = stored.getItemStack();
        return builder.createBuilder(stack).buildWithArea(slotArea);
    }

    /**
     * Determines which storage slot (if any) is at the given screen coordinates.
     */
    private Integer getStorageSlotAt(T screen, int mouseX, int mouseY) {
        int guiLeft = screen.getGuiLeft();
        int guiTop = screen.getGuiTop();
        int startX = guiLeft + 8;
        int startY = guiTop + 18;

        int clickedX = mouseX - startX;
        int clickedY = mouseY - startY;

        int storageRows = screen.getStorageRows();
        int storageAreaHeight = storageRows * 18;

        if (clickedX >= 0 && clickedY >= 0 && clickedX < 162 && clickedY < storageAreaHeight) {
            int column = clickedX / 18;
            int row = clickedY / 18;
            if (column < 9 && row < storageRows) {
                int scrollRow = screen.getScrollRow();
                return (scrollRow * 9) + (row * 9) + column;
            }
        }
        return null;
    }

    @Override
    public List<Rect2i> getGuiExtraAreas(T screen) {
        return Collections.emptyList();
    }
}
