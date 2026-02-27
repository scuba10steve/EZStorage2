package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity.SecurePlayer;
import io.github.scuba10steve.s3.gui.server.SecurityBoxMenu;
import io.github.scuba10steve.s3.network.SecurityPlayerPacket;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen for the Security Box player management GUI.
 * Shows two columns: allowed players (left) and nearby players (right).
 */
public class SecurityBoxScreen extends AbstractContainerScreen<SecurityBoxMenu> {

    private static final ResourceLocation TEXTURE =
        ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/security_box.png");

    private static final int NUM_BUTTONS = 7;
    private static final int BUTTON_WIDTH = 80;
    private static final int BUTTON_HEIGHT = 14;

    private final Button[] addedPlayerButtons = new Button[NUM_BUTTONS];
    private final Button[] availablePlayerButtons = new Button[NUM_BUTTONS];

    public SecurityBoxScreen(SecurityBoxMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 230;
        this.imageWidth = 176;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        int k = this.leftPos;
        int l = this.topPos;

        // Left column: allowed players (click to remove)
        for (int i = 0; i < NUM_BUTTONS; i++) {
            final int index = i;
            addedPlayerButtons[i] = Button.builder(
                    Component.literal(""),
                    btn -> onRemovePlayer(index)
                )
                .bounds(k + 4, l + 30 + i * 15, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
            addedPlayerButtons[i].visible = false;
            addRenderableWidget(addedPlayerButtons[i]);
        }

        // Right column: nearby players (click to add)
        for (int i = 0; i < NUM_BUTTONS; i++) {
            final int index = i;
            availablePlayerButtons[i] = Button.builder(
                    Component.literal(""),
                    btn -> onAddPlayer(index)
                )
                .bounds(k + this.imageWidth - BUTTON_WIDTH - 4, l + 30 + i * 15, BUTTON_WIDTH, BUTTON_HEIGHT)
                .build();
            availablePlayerButtons[i].visible = false;
            addRenderableWidget(availablePlayerButtons[i]);
        }
    }

    private void onRemovePlayer(int index) {
        SecurityBoxBlockEntity securityBox = menu.getBlockEntity();
        List<SecurePlayer> allowed = securityBox.getAllowedPlayers();
        if (index < allowed.size()) {
            SecurePlayer player = allowed.get(index);
            S3Platform.getNetworkHelper().sendToServer(
                new SecurityPlayerPacket(menu.getPos(), player.id(), player.name(), false));
        }
    }

    private void onAddPlayer(int index) {
        List<Player> nearby = getNearbyPlayers();
        if (index < nearby.size()) {
            Player player = nearby.get(index);
            S3Platform.getNetworkHelper().sendToServer(
                new SecurityPlayerPacket(menu.getPos(), player.getUUID(), player.getName().getString(), true));
        }
    }

    private List<Player> getNearbyPlayers() {
        List<Player> nearby = new ArrayList<>();
        if (minecraft == null || minecraft.level == null) return nearby;

        SecurityBoxBlockEntity securityBox = menu.getBlockEntity();
        var pos = securityBox.getBlockPos();

        for (Player player : minecraft.level.players()) {
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 32 * 32) {
                nearby.add(player);
                if (nearby.size() >= NUM_BUTTONS) break;
            }
        }
        return nearby;
    }

    private boolean isAlreadyAllowed(Player player) {
        SecurityBoxBlockEntity securityBox = menu.getBlockEntity();
        for (SecurePlayer sp : securityBox.getAllowedPlayers()) {
            if (sp.id().equals(player.getUUID())) return true;
        }
        return false;
    }

    @Override
    protected void containerTick() {
        super.containerTick();

        SecurityBoxBlockEntity securityBox = menu.getBlockEntity();
        List<SecurePlayer> allowed = securityBox.getAllowedPlayers();
        List<Player> nearby = getNearbyPlayers();

        // Update allowed player buttons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            if (i < allowed.size()) {
                addedPlayerButtons[i].visible = true;
                addedPlayerButtons[i].setMessage(Component.literal(allowed.get(i).name()));
            } else {
                addedPlayerButtons[i].visible = false;
                addedPlayerButtons[i].setMessage(Component.literal(""));
            }
        }

        // Update nearby player buttons
        for (int i = 0; i < NUM_BUTTONS; i++) {
            if (i < nearby.size()) {
                availablePlayerButtons[i].visible = true;
                availablePlayerButtons[i].setMessage(Component.literal(nearby.get(i).getName().getString()));
                availablePlayerButtons[i].active = !isAlreadyAllowed(nearby.get(i));
            } else {
                availablePlayerButtons[i].visible = false;
                availablePlayerButtons[i].setMessage(Component.literal(""));
            }
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
        // Title centered
        guiGraphics.drawString(this.font, this.title,
            this.imageWidth / 2 - this.font.width(this.title) / 2, 6, 0x404040, false);

        // Column headers
        guiGraphics.drawString(this.font, "Allowed Players", 4, 18, 0x606060, false);
        guiGraphics.drawString(this.font, "Nearby Players",
            this.imageWidth - BUTTON_WIDTH - 4, 18, 0x606060, false);

        // Player inventory label
        guiGraphics.drawString(this.font, this.playerInventoryTitle,
            this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }
}
