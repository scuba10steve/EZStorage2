package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.network.StorageClickPacket;
import io.github.scuba10steve.s3.ref.RefStrings;
import io.github.scuba10steve.s3.storage.EZInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class StorageCoreCraftingScreen extends AbstractContainerScreen<StorageCoreCraftingMenu> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreCraftingScreen.class);
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/storage_crafting_gui.png");
    
    private int scrollRow = 0;

    public StorageCoreCraftingScreen(StorageCoreCraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 256;
        this.imageWidth = 256;
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 108;
        LOGGER.info("StorageCoreCraftingScreen created");
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Render title
        guiGraphics.drawString(font, this.title.getString(), 8, 6, 0x404040, false);
        
        // Render stored items
        renderStorageItems(guiGraphics);
        
        // Render storage count
        EZInventory inventory = menu.getInventory();
        if (inventory != null) {
            java.text.DecimalFormat formatter = new java.text.DecimalFormat("#,###");
            String totalCount = formatter.format(inventory.getTotalItemCount());
            String max = formatter.format(inventory.getMaxItems());
            String amount = totalCount + "/" + max;
            int stringWidth = font.width(amount);
            guiGraphics.drawString(font, amount, 187 - stringWidth, 6, 0x404040, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight, 256, 256);
    }
    
    private void renderStorageItems(GuiGraphics guiGraphics) {
        EZInventory inventory = menu.getInventory();
        if (inventory == null) {
            LOGGER.debug("Storage inventory is null");
            // Render "No Storage Core" message
            String message = "No Storage Core Connected";
            int messageWidth = font.width(message);
            int x = (imageWidth - messageWidth) / 2;
            int y = 50;
            guiGraphics.drawString(font, message, x, y, 0xFF0000, false);
            return;
        }
        
        List<StoredItemStack> storedItems = inventory.getStoredItems();
        LOGGER.debug("Rendering storage items, count: {}", storedItems.size());
        
        int startX = 8;
        int startY = 18;
        
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int index = (scrollRow * 9) + (row * 9) + col;
                if (index >= storedItems.size()) return;
                
                StoredItemStack stored = storedItems.get(index);
                if (stored != null && !stored.getItemStack().isEmpty()) {
                    int x = startX + (col * 18);
                    int y = startY + (row * 18);
                    
                    LOGGER.debug("Rendering item at {},{}: {}", x, y, stored.getItemStack().getDisplayName().getString());
                    
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
    
    private String formatCount(long count) {
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
                    // Player is trying to place items into storage
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
                        // Send packet to server to handle extraction
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
    
    private Integer getSlotAt(int mouseX, int mouseY) {
        int startX = leftPos + 8;
        int startY = topPos + 18;
        
        int clickedX = mouseX - startX;
        int clickedY = mouseY - startY;
        
        if (clickedX >= 0 && clickedY >= 0 && clickedX < 162 && clickedY < 72) { // 4 rows = 72px
            int column = clickedX / 18;
            int row = clickedY / 18;
            if (column < 9 && row < 4) {
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
        int maxRows = (storedItems.size() + 8) / 9 - 4; // 4 visible rows
        if (maxRows <= 0) return false;
        
        if (scrollY > 0) {
            scrollRow = Math.max(0, scrollRow - 1);
        } else if (scrollY < 0) {
            scrollRow = Math.min(maxRows, scrollRow + 1);
        }
        
        return true;
    }
}
