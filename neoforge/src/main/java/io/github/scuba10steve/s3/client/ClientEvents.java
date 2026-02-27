package io.github.scuba10steve.s3.client;

import io.github.scuba10steve.s3.gui.client.ExtractPortScreen;
import io.github.scuba10steve.s3.gui.client.SecurityBoxScreen;
import io.github.scuba10steve.s3.gui.client.StorageCoreScreen;
import io.github.scuba10steve.s3.gui.client.StorageCoreCraftingScreen;
import io.github.scuba10steve.s3.init.ModItems;
import io.github.scuba10steve.s3.init.ModMenuTypes;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.EventBusSubscriber.Bus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

@EventBusSubscriber(modid = RefStrings.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.STORAGE_CORE.get(), StorageCoreScreen::new);
        event.register(ModMenuTypes.STORAGE_CORE_CRAFTING.get(), StorageCoreCraftingScreen::new);
        event.register(ModMenuTypes.EXTRACT_PORT.get(), ExtractPortScreen::new);
        event.register(ModMenuTypes.SECURITY_BOX.get(), SecurityBoxScreen::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            ResourceLocation dollyState = ResourceLocation.fromNamespaceAndPath(RefStrings.MODID, "dolly_state");
            registerDollyProperty(ModItems.DOLLY.get(), dollyState);
            registerDollyProperty(ModItems.DOLLY_SUPER.get(), dollyState);
        });
    }

    private static void registerDollyProperty(Item dollyItem, ResourceLocation propertyId) {
        ItemProperties.register(dollyItem, propertyId, (stack, level, entity, seed) -> {
            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null) {
                CompoundTag tag = customData.copyTag();
                if (tag.getBoolean("isFull")) {
                    if (tag.getBoolean("isChest")) {
                        return 1.0f;
                    }
                    if (tag.getBoolean("isStorageCore")) {
                        return 2.0f;
                    }
                }
            }
            return 0.0f;
        });
    }
}
