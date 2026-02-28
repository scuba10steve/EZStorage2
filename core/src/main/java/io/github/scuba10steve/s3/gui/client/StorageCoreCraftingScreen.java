package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.gui.server.StorageCoreCraftingMenu;
import io.github.scuba10steve.s3.network.ClearCraftingGridPacket;
import io.github.scuba10steve.s3.platform.S3Platform;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageCoreCraftingScreen extends AbstractStorageScreen<StorageCoreCraftingMenu> {
    private static final Logger LOGGER = LoggerFactory.getLogger(StorageCoreCraftingScreen.class);

    public StorageCoreCraftingScreen(StorageCoreCraftingMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title,
              ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/storage_crafting_gui.png"));

        this.imageHeight = 256;
        this.imageWidth = 256;
        this.inventoryLabelY = 151; // Position label just above player inventory (which starts at y=162)

        // Crafting GUI has 4 rows of storage instead of 6
        this.storageRows = 4;
        this.storageAreaHeight = 72; // 4 rows * 18px

        LOGGER.debug("StorageCoreCraftingScreen created");
    }

    @Override
    protected void init() {
        super.init();

        Button clearButton = Button.builder(
                Component.translatable("gui.s3.clear"),
                btn -> S3Platform.getNetworkHelper().sendToServer(new ClearCraftingGridPacket(menu.getPos()))
            )
            .bounds(this.leftPos + 108, this.topPos + 137, 30, 12)
            .build();
        this.addRenderableWidget(clearButton);
    }
}
