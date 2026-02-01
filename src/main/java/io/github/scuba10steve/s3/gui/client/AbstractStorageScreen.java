package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.gui.server.StorageCoreMenu;
import io.github.scuba10steve.s3.network.StorageClickPacket;
import io.github.scuba10steve.s3.storage.EZInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

/**
 * Base screen class for storage-based GUIs.
 * Provides common functionality for rendering storage items, scrollbar, and handling storage interactions.
 */
public abstract class AbstractStorageScreen<T extends StorageCoreMenu> extends AbstractContainerScreen<T> {

    // Vanilla creative inventory tabs texture contains the scrollbar thumb at (232, 0)
    protected static final ResourceLocation CREATIVE_TABS =
        ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tabs.png");

    protected ResourceLocation texture;
    protected int scrollRow = 0;
    protected float currentScroll = 0.0F;
    protected int storageRows = 6;
    protected int storageAreaHeight = 108; // 6 rows * 18px

    protected AbstractStorageScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(menu, playerInventory, title);
        this.texture = texture;
        this.titleLabelY = 6;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Draw scrollbar thumb from vanilla creative tabs texture (thumb is at u=232, v=0, 12x15 pixels)
        int scrollbarX = x + 175;
        int scrollbarY = y + 18 + (int)((storageAreaHeight - 15) * currentScroll);
        guiGraphics.blit(CREATIVE_TABS, scrollbarX, scrollbarY, 232, 0, 12, 15);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        // Render item count
        EZInventory inventory = menu.getInventory();
        if (inventory != null) {
            DecimalFormat formatter = new DecimalFormat("#,###");
            String totalCount = formatter.format(inventory.getTotalItemCount());
            String max = formatter.format(inventory.getMaxItems());
            String amount = totalCount + "/" + max;
            int stringWidth = font.width(amount);
            guiGraphics.drawString(font, amount, 187 - stringWidth, 6, 0x404040, false);
        }

        // Render stored items
        renderStoredItems(guiGraphics);

        // Render hover highlight for storage slots
        renderStorageHighlight(guiGraphics, mouseX, mouseY);
    }

    /**
     * Renders a highlight overlay when hovering over a storage slot.
     */
    protected void renderStorageHighlight(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Integer slotIndex = getSlotAt(mouseX, mouseY);
        if (slotIndex == null) return;

        EZInventory inventory = menu.getInventory();
        if (inventory == null) return;

        List<StoredItemStack> storedItems = inventory.getStoredItems();
        if (slotIndex >= storedItems.size()) return;

        StoredItemStack stored = storedItems.get(slotIndex);
        if (stored == null || stored.getItemStack().isEmpty()) return;

        // Calculate the slot position relative to the GUI
        int visibleIndex = slotIndex - (scrollRow * 9);
        int row = visibleIndex / 9;
        int col = visibleIndex % 9;
        int x = 8 + (col * 18);
        int y = 18 + (row * 18);

        // Draw highlight overlay (semi-transparent white, same as vanilla slot highlight)
        guiGraphics.fillGradient(x, y, x + 16, y + 16, 0x80FFFFFF, 0x80FFFFFF);
    }

    protected void renderStoredItems(GuiGraphics guiGraphics) {
        EZInventory inventory = menu.getInventory();
        if (inventory == null) return;

        List<StoredItemStack> storedItems = inventory.getStoredItems();
        int startX = 8;
        int startY = 18;

        for (int row = 0; row < storageRows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = (scrollRow * 9) + (row * 9) + col;
                if (index >= storedItems.size()) return;

                StoredItemStack stored = storedItems.get(index);
                if (stored != null && !stored.getItemStack().isEmpty()) {
                    int x = startX + (col * 18);
                    int y = startY + (row * 18);

                    guiGraphics.renderItem(stored.getItemStack(), x, y);

                    // Render count overlay
                    String countStr = formatCount(stored.getCount());
                    guiGraphics.pose().pushPose();
                    guiGraphics.pose().translate(0, 0, 200);
                    guiGraphics.drawString(font, countStr, x + 17 - font.width(countStr), y + 9, 0xFFFFFF, true);
                    guiGraphics.pose().popPose();
                }
            }
        }
    }

    protected String formatCount(long count) {
        if (count < 1000) return String.valueOf(count);
        if (count < 1000000) return (count / 1000) + "K";
        if (count < 1000000000) return (count / 1000000) + "M";
        return (count / 1000000000) + "B";
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Integer slot = getSlotAt((int)mouseX, (int)mouseY);
        if (slot != null) {
            EZInventory inventory = menu.getInventory();
            if (inventory != null && minecraft != null && minecraft.player != null) {
                // Check if player is holding items - if so, insert them
                ItemStack carried = minecraft.player.containerMenu.getCarried();
                if (!carried.isEmpty()) {
                    if (minecraft.getConnection() != null) {
                        minecraft.getConnection().send(
                            new StorageClickPacket(menu.getPos(), -1, button, hasShiftDown())
                        );
                    }
                    return true;
                }

                // Otherwise, try to extract from the clicked slot
                List<StoredItemStack> storedItems = inventory.getStoredItems();
                if (slot < storedItems.size()) {
                    StoredItemStack stored = storedItems.get(slot);
                    if (stored != null && !stored.getItemStack().isEmpty()) {
                        if (minecraft.getConnection() != null) {
                            minecraft.getConnection().send(
                                new StorageClickPacket(menu.getPos(), slot, button, hasShiftDown())
                            );
                        }
                        return true;
                    }
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    protected Integer getSlotAt(int mouseX, int mouseY) {
        int startX = leftPos + 8;
        int startY = topPos + 18;

        int clickedX = mouseX - startX;
        int clickedY = mouseY - startY;

        if (clickedX >= 0 && clickedY >= 0 && clickedX < 162 && clickedY < storageAreaHeight) {
            int column = clickedX / 18;
            int row = clickedY / 18;
            if (column < 9 && row < storageRows) {
                return (scrollRow * 9) + (row * 9) + column;
            }
        }
        return null;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        EZInventory inventory = menu.getInventory();
        if (inventory == null) return false;

        List<StoredItemStack> storedItems = inventory.getStoredItems();
        int maxRows = (storedItems.size() + 8) / 9 - storageRows;
        if (maxRows <= 0) return false;

        if (scrollY > 0) {
            scrollRow = Math.max(0, scrollRow - 1);
        } else if (scrollY < 0) {
            scrollRow = Math.min(maxRows, scrollRow + 1);
        }

        currentScroll = maxRows > 0 ? (float)scrollRow / maxRows : 0;
        return true;
    }

    // Getters for JEI integration
    public int getStorageRows() {
        return storageRows;
    }

    public int getScrollRow() {
        return scrollRow;
    }

    public int getGuiLeft() {
        return leftPos;
    }

    public int getGuiTop() {
        return topPos;
    }
}
