package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.gui.server.ExtractPortMenu;
import io.github.scuba10steve.s3.network.ExtractPortConfigPacket;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

/**
 * Screen for the Extract Port configuration GUI.
 */
public class ExtractPortScreen extends AbstractContainerScreen<ExtractPortMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/extract_port.png");

    private Button listModeButton;
    private Checkbox roundRobinCheckbox;

    public ExtractPortScreen(ExtractPortMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 151;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        ExtractPortBlockEntity be = menu.getBlockEntity();

        // List mode button
        listModeButton = Button.builder(
                Component.literal(be.getListMode().getDisplayName()),
                this::onListModePressed
            )
            .bounds(this.leftPos + 99, this.topPos + 42, 70, 20)
            .build();
        addRenderableWidget(listModeButton);

        // Round-robin checkbox
        roundRobinCheckbox = Checkbox.builder(
                Component.literal("Round Robin"),
                this.font
            )
            .pos(this.leftPos + 8, this.topPos + 42)
            .selected(be.isRoundRobin())
            .onValueChange((checkbox, selected) -> onRoundRobinChanged(selected))
            .build();
        addRenderableWidget(roundRobinCheckbox);
    }

    private void onListModePressed(Button button) {
        if (minecraft != null && minecraft.getConnection() != null) {
            minecraft.getConnection().send(new ExtractPortConfigPacket(menu.getPos(), true, false));
        }
    }

    private void onRoundRobinChanged(boolean selected) {
        if (minecraft != null && minecraft.getConnection() != null) {
            minecraft.getConnection().send(new ExtractPortConfigPacket(menu.getPos(), false, selected));
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        // Update button text
        ExtractPortBlockEntity be = menu.getBlockEntity();
        listModeButton.setMessage(Component.literal(be.getListMode().getDisplayName()));

        // Draw title
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);

        // Draw tooltip for list mode button
        if (listModeButton.isHovered()) {
            guiGraphics.renderTooltip(this.font,
                Component.literal(be.getListMode().getDescription()),
                mouseX - leftPos, mouseY - topPos);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
