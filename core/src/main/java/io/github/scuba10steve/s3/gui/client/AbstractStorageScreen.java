package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.gui.server.StorageCoreMenu;
import io.github.scuba10steve.s3.network.SortModePacket;
import io.github.scuba10steve.s3.network.StorageClickPacket;
import io.github.scuba10steve.s3.storage.StorageInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import io.github.scuba10steve.s3.util.CountFormatter;
import io.github.scuba10steve.s3.util.SortMode;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Base screen class for storage-based GUIs.
 * Provides common functionality for rendering storage items, scrollbar, and handling storage interactions.
 */
public abstract class AbstractStorageScreen<T extends StorageCoreMenu> extends AbstractContainerScreen<T> {

    // Vanilla scrollbar sprites (1.21+ sprite-based rendering)
    protected static final ResourceLocation SCROLLER_SPRITE =
        ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller");
    protected static final ResourceLocation SCROLLER_DISABLED_SPRITE =
        ResourceLocation.withDefaultNamespace("container/creative_inventory/scroller_disabled");

    protected ResourceLocation texture;
    protected int scrollRow = 0;
    protected float currentScroll = 0.0F;
    protected int storageRows = 6;
    protected int storageAreaHeight = 108; // 6 rows * 18px

    // Search functionality
    protected EditBox searchField;
    protected List<StoredItemStack> filteredItems = new ArrayList<>();
    protected boolean searchActive = false;
    protected long lastKnownTotalCount = -1; // Track inventory changes for filtered list refresh
    protected int lastKnownItemTypes = -1;

    // Sort functionality
    protected Button sortButton;
    protected boolean sortActive = false;

    // Tooltip: set during renderLabels, consumed during renderTooltip
    private ItemStack hoveredStorageStack = ItemStack.EMPTY;

    protected AbstractStorageScreen(T menu, Inventory playerInventory, Component title, ResourceLocation texture) {
        super(menu, playerInventory, title);
        this.texture = texture;
        this.titleLabelY = 6;
    }

    @Override
    protected void init() {
        super.init();

        // Create search field - positioned at top of GUI
        this.searchField = new EditBox(this.font, this.leftPos + 10, this.topPos + 4, 80, 12, Component.translatable("gui.s3.search"));
        this.searchField.setMaxLength(50);
        this.searchField.setBordered(true);
        this.searchField.setTextColor(0xFFFFFF);
        this.searchField.setResponder(this::onSearchChanged);

        // Check if search box is available
        StorageInventory inventory = menu.getInventory();
        searchActive = inventory != null && inventory.hasSearchBox();

        if (searchActive) {
            this.addWidget(this.searchField);
            this.searchField.setFocused(S3Platform.getConfig().isSearchAutoFocus());
        }

        // Check if sort box is available and create sort button
        // Position below storage area dynamically based on storageAreaHeight
        sortActive = inventory != null && inventory.hasSortBox();
        if (sortActive) {
            SortMode currentMode = inventory.getSortMode();
            int sortButtonY = this.topPos + 18 + storageAreaHeight; // Just below storage grid
            this.sortButton = Button.builder(
                    Component.literal(currentMode.getDisplayName()),
                    this::onSortButtonPressed
                )
                .bounds(this.leftPos + 118, sortButtonY, 50, 12)
                .build();
            this.addRenderableWidget(this.sortButton);
        }

        // Initialize filtered items
        updateFilteredItems();
    }

    /**
     * Called when the sort button is pressed
     */
    protected void onSortButtonPressed(Button button) {
        S3Platform.getNetworkHelper().sendToServer(new SortModePacket(menu.getPos()));
    }

    /**
     * Called when search text changes
     */
    protected void onSearchChanged(String text) {
        updateFilteredItems();
        scrollRow = 0;
        currentScroll = 0.0F;
    }

    /**
     * Updates the filtered items list based on search text
     */
    protected void updateFilteredItems() {
        StorageInventory inventory = menu.getInventory();
        if (inventory == null) {
            filteredItems = new ArrayList<>();
            return;
        }

        // Get items (sorted if sort box is present)
        List<StoredItemStack> allItems = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();

        if (!searchActive || searchField == null || searchField.getValue().isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
            return;
        }

        String searchText = searchField.getValue().toLowerCase(Locale.ROOT);
        boolean tagSearch = searchText.startsWith("$");
        boolean modSearch = searchText.startsWith("@");
        boolean tabSearch = searchText.startsWith("%");

        if (tagSearch || modSearch || tabSearch) {
            searchText = searchText.substring(1);
        }

        final String finalSearchText = searchText;
        final boolean finalTagSearch = tagSearch;
        final boolean finalModSearch = modSearch;
        final boolean finalTabSearch = tabSearch;

        filteredItems = allItems.stream()
            .filter(stored -> matchesSearch(stored, finalSearchText, finalTagSearch, finalModSearch, finalTabSearch))
            .toList();
    }

    /**
     * Checks if an item matches the search criteria
     */
    protected boolean matchesSearch(StoredItemStack stored, String searchText, boolean tagSearch, boolean modSearch, boolean tabSearch) {
        if (searchText.isEmpty()) return true;

        ItemStack stack = stored.getItemStack();

        if (tagSearch) {
            // Search item tags
            return stack.getTags()
                .anyMatch(tag -> tag.location().toString().toLowerCase(Locale.ROOT).contains(searchText));
        } else if (modSearch) {
            // Search mod ID and mod name
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            String modId = id.getNamespace().toLowerCase(Locale.ROOT);
            return modId.contains(searchText);
        } else if (tabSearch) {
            // Search creative tab - simplified check using item's default tab
            // This is a simplified implementation since creative tabs work differently in modern MC
            String itemName = stack.getItem().toString().toLowerCase(Locale.ROOT);
            return itemName.contains(searchText);
        } else {
            // Search item name and tooltip
            String displayName = stack.getHoverName().getString().toLowerCase(Locale.ROOT);
            if (displayName.contains(searchText)) {
                return true;
            }

            // Also search tooltips
            if (minecraft != null && minecraft.player != null && minecraft.level != null) {
                Item.TooltipContext tooltipContext = Item.TooltipContext.of(minecraft.level);
                List<Component> tooltip = stack.getTooltipLines(
                    tooltipContext,
                    minecraft.player,
                    minecraft.options.advancedItemTooltips ? TooltipFlag.ADVANCED : TooltipFlag.NORMAL
                );
                for (Component line : tooltip) {
                    if (line.getString().toLowerCase(Locale.ROOT).contains(searchText)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight, 256, 256);

        // Draw search bar background if search box is present
        if (searchActive && searchField != null) {
            searchField.render(guiGraphics, mouseX, mouseY, partialTick);
        }

        // Draw scrollbar thumb using vanilla scroller sprite
        int scrollbarX = x + 175;
        int scrollbarY = y + 18 + (int)((storageAreaHeight - 15) * currentScroll);
        boolean canScroll = canScrollItems();
        guiGraphics.blitSprite(canScroll ? SCROLLER_SPRITE : SCROLLER_DISABLED_SPRITE, scrollbarX, scrollbarY, 12, 15);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Check if search box status changed
        StorageInventory inventory = menu.getInventory();
        boolean shouldHaveSearch = inventory != null && inventory.hasSearchBox();
        if (shouldHaveSearch != searchActive) {
            searchActive = shouldHaveSearch;
            if (searchActive && searchField != null) {
                this.addWidget(searchField);
            }
            updateFilteredItems();
        }

        // Check if sort box status changed
        boolean shouldHaveSort = inventory != null && inventory.hasSortBox();
        if (shouldHaveSort != sortActive) {
            sortActive = shouldHaveSort;
            // Refresh will handle adding/removing button
        }

        // Check if inventory contents changed and update filtered items if search is active
        if (inventory != null) {
            long currentTotalCount = inventory.getTotalItemCount();
            int currentItemTypes = inventory.getStoredItems().size();
            if (currentTotalCount != lastKnownTotalCount || currentItemTypes != lastKnownItemTypes) {
                lastKnownTotalCount = currentTotalCount;
                lastKnownItemTypes = currentItemTypes;
                if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
                    updateFilteredItems();
                }
            }
        }

        // Update sort button text to reflect current mode
        if (sortActive && sortButton != null && inventory != null) {
            SortMode currentMode = inventory.getSortMode();
            sortButton.setMessage(Component.literal(currentMode.getDisplayName()));
        }

        // Only render title if search is NOT active (search bar replaces the title area)
        if (!searchActive) {
            guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        }

        // Always render inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        // Render item count
        if (inventory != null) {
            String amount = formatCount(inventory.getTotalItemCount()) + "/" + formatCount(inventory.getMaxItems());
            int stringWidth = font.width(amount);
            guiGraphics.drawString(font, amount, 187 - stringWidth, 6, 0x404040, false);
        }

        // Render stored items
        renderStoredItems(guiGraphics);

        // Render hover highlight for storage slots and detect hovered item for tooltip
        renderStorageHighlight(guiGraphics, mouseX, mouseY);

        // Capture hovered storage item for tooltip rendering
        hoveredStorageStack = ItemStack.EMPTY;
        Integer slotIndex = getSlotAt(mouseX, mouseY);
        if (slotIndex != null) {
            List<StoredItemStack> items = getDisplayItems();
            if (slotIndex < items.size()) {
                StoredItemStack stored = items.get(slotIndex);
                if (stored != null && !stored.getItemStack().isEmpty()) {
                    hoveredStorageStack = stored.getItemStack();
                }
            }
        }
    }

    /**
     * Renders a highlight overlay when hovering over a storage slot.
     */
    protected void renderStorageHighlight(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Integer slotIndex = getSlotAt(mouseX, mouseY);
        if (slotIndex == null) return;

        // Use filtered items if searching
        List<StoredItemStack> itemsToUse;
        if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
            itemsToUse = filteredItems;
        } else {
            StorageInventory inventory = menu.getInventory();
            if (inventory == null) return;
            itemsToUse = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
        }

        if (slotIndex >= itemsToUse.size()) return;

        StoredItemStack stored = itemsToUse.get(slotIndex);
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
        // Use filtered items if search is active, otherwise get fresh from inventory
        List<StoredItemStack> itemsToRender;
        if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
            itemsToRender = filteredItems;
        } else {
            StorageInventory inventory = menu.getInventory();
            if (inventory == null) return;
            // Use sorted items if sort box is present
            itemsToRender = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
        }

        int startX = 8;
        int startY = 18;

        for (int row = 0; row < storageRows; row++) {
            for (int col = 0; col < 9; col++) {
                int index = (scrollRow * 9) + (row * 9) + col;
                if (index >= itemsToRender.size()) return;

                StoredItemStack stored = itemsToRender.get(index);
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
        return CountFormatter.formatCount(count);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderTooltip(guiGraphics, mouseX, mouseY);

        if (!this.menu.getCarried().isEmpty()) return;
        if (hoveredStorageStack.isEmpty()) return;

        guiGraphics.renderTooltip(this.font, this.getTooltipFromContainerItem(hoveredStorageStack),
                hoveredStorageStack.getTooltipImage(), mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Handle search field clicks
        if (searchActive && searchField != null) {
            if (searchField.isMouseOver(mouseX, mouseY)) {
                if (button == 1) { // Right click to clear
                    searchField.setValue("");
                    updateFilteredItems();
                }
                searchField.setFocused(true);
                return true;
            } else {
                searchField.setFocused(false);
            }
        }

        Integer slot = getSlotAt((int)mouseX, (int)mouseY);
        if (slot != null) {
            StorageInventory inventory = menu.getInventory();
            if (inventory != null && minecraft != null && minecraft.player != null) {
                // Check if player is holding items - if so, insert them
                ItemStack carried = minecraft.player.containerMenu.getCarried();
                if (!carried.isEmpty()) {
                    S3Platform.getNetworkHelper().sendToServer(
                        new StorageClickPacket(menu.getPos(), -1, button, hasShiftDown())
                    );
                    return true;
                }

                // Use filtered items if searching, otherwise all items (sorted if sort box present)
                List<StoredItemStack> itemsToUse;
                if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
                    itemsToUse = filteredItems;
                } else {
                    itemsToUse = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
                }

                // Find the actual inventory index for the clicked item
                if (slot < itemsToUse.size()) {
                    StoredItemStack stored = itemsToUse.get(slot);
                    if (stored != null && !stored.getItemStack().isEmpty()) {
                        // Find the real index in the full inventory
                        List<StoredItemStack> allItems = inventory.getStoredItems();
                        int realIndex = -1;
                        for (int i = 0; i < allItems.size(); i++) {
                            if (ItemStack.isSameItemSameComponents(allItems.get(i).getItemStack(), stored.getItemStack())) {
                                realIndex = i;
                                break;
                            }
                        }

                        if (realIndex >= 0) {
                            S3Platform.getNetworkHelper().sendToServer(
                                new StorageClickPacket(menu.getPos(), realIndex, button, hasShiftDown())
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
        // Use filtered items for scroll calculation if searching
        List<StoredItemStack> itemsForScroll;
        if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
            itemsForScroll = filteredItems;
        } else {
            StorageInventory inventory = menu.getInventory();
            if (inventory == null) return false;
            itemsForScroll = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
        }

        int maxRows = (itemsForScroll.size() + 8) / 9 - storageRows;
        if (maxRows <= 0) return false;

        if (scrollY > 0) {
            scrollRow = Math.max(0, scrollRow - 1);
        } else if (scrollY < 0) {
            scrollRow = Math.min(maxRows, scrollRow + 1);
        }

        currentScroll = maxRows > 0 ? (float)scrollRow / maxRows : 0;
        return true;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // Handle search field input
        if (searchActive && searchField != null && searchField.isFocused()) {
            if (keyCode == 256) { // Escape key
                searchField.setFocused(false);
                return true;
            }
            // Block inventory key (and other game keys) while typing in search field
            // This prevents 'e' from closing the GUI while searching
            if (minecraft != null && minecraft.options.keyInventory.matches(keyCode, scanCode)) {
                return true; // Consume the event, don't close inventory
            }
            if (searchField.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        // Handle character typing in search field
        if (searchActive && searchField != null && searchField.isFocused()) {
            return searchField.charTyped(codePoint, modifiers);
        }
        return super.charTyped(codePoint, modifiers);
    }

    /**
     * Returns true if the storage has more items than visible rows can display.
     */
    protected boolean canScrollItems() {
        List<StoredItemStack> items;
        if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
            items = filteredItems;
        } else {
            StorageInventory inventory = menu.getInventory();
            if (inventory == null) return false;
            items = sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
        }
        return (items.size() + 8) / 9 > storageRows;
    }

    // Getters for JEI integration
    public List<StoredItemStack> getDisplayItems() {
        if (searchActive && searchField != null && !searchField.getValue().isEmpty()) {
            return filteredItems;
        }
        StorageInventory inventory = menu.getInventory();
        if (inventory == null) return List.of();
        return sortActive ? inventory.getSortedItems() : inventory.getStoredItems();
    }

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
