package io.github.scuba10steve.ezstorage.gui.client;

import io.github.scuba10steve.ezstorage.gui.server.StorageCoreMenu;
import io.github.scuba10steve.ezstorage.network.StorageClickPacket;
import io.github.scuba10steve.ezstorage.ref.RefStrings;
import io.github.scuba10steve.ezstorage.storage.EZInventory;
import io.github.scuba10steve.ezstorage.storage.StoredItemStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import java.text.DecimalFormat;
import java.util.List;

public class StorageCoreScreen extends AbstractContainerScreen<StorageCoreMenu> {
    
    private static final ResourceLocation TEXTURE = 
        ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/storage_core.png");
    private static final ResourceLocation CREATIVE_TABS = 
        ResourceLocation.withDefaultNamespace("textures/gui/container/creative_inventory/tabs.png");
    
    private int scrollRow = 0;
    private float currentScroll = 0.0F;

    public StorageCoreScreen(StorageCoreMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 222;
        this.imageWidth = 195; // Wider to accommodate sidebar
        this.titleLabelY = 6;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
        
        // Draw scrollbar from the GUI texture itself
        int scrollbarX = x + 175;
        int scrollbarY = y + 18 + (int)((108 - 17) * currentScroll);
        guiGraphics.blit(TEXTURE, scrollbarX, scrollbarY, 195, 0, 12, 15);
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
        
        // Render stored items in sidebar
        renderStoredItems(guiGraphics);
    }
    
    private void renderStoredItems(GuiGraphics guiGraphics) {
        EZInventory inventory = menu.getInventory();
        if (inventory == null) return;
        
        List<StoredItemStack> storedItems = inventory.getStoredItems();
        int startX = 8;
        int startY = 18;
        
        for (int row = 0; row < 6; row++) {
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
            if (inventory != null) {
                List<StoredItemStack> storedItems = inventory.getStoredItems();
                if (slot < storedItems.size()) {
                    StoredItemStack stored = storedItems.get(slot);
                    if (stored != null && !stored.getItemStack().isEmpty()) {
                        // Send packet to server to handle extraction
                        if (minecraft != null && minecraft.getConnection() != null) {
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
        
        if (clickedX >= 0 && clickedY >= 0 && clickedX < 162 && clickedY < 108) {
            int column = clickedX / 18;
            int row = clickedY / 18;
            if (column < 9 && row < 6) {
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
        int maxRows = (storedItems.size() + 8) / 9 - 6;
        if (maxRows <= 0) return false;
        
        if (scrollY > 0) {
            scrollRow = Math.max(0, scrollRow - 1);
        } else if (scrollY < 0) {
            scrollRow = Math.min(maxRows, scrollRow + 1);
        }
        
        currentScroll = maxRows > 0 ? (float)scrollRow / maxRows : 0;
        return true;
    }
}
