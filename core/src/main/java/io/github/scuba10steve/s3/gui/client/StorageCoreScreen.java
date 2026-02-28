package io.github.scuba10steve.s3.gui.client;

import io.github.scuba10steve.s3.gui.server.StorageCoreMenu;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class StorageCoreScreen extends AbstractStorageScreen<StorageCoreMenu> {

    public StorageCoreScreen(StorageCoreMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title,
              ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "textures/gui/storage_core.png"));

        this.imageHeight = 222;
        this.imageWidth = 195;
        this.inventoryLabelY = this.imageHeight - 94;

        // Storage core has 6 rows
        this.storageRows = 6;
        this.storageAreaHeight = 108; // 6 rows * 18px
    }
}
