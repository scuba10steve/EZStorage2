package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.blockentity.InputPortBlockEntity;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

/**
 * Registers item handler capabilities for blocks that need to interact with hoppers/pipes.
 */
@EventBusSubscriber(modid = RefStrings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register Input Port item handler capability
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.INPUT_PORT.get(),
            (blockEntity, direction) -> blockEntity.getItemHandler()
        );

        // Register Extract Port item handler capability
        event.registerBlockEntity(
            Capabilities.ItemHandler.BLOCK,
            ModBlockEntities.EXTRACT_PORT.get(),
            (blockEntity, direction) -> blockEntity.getItemHandler()
        );
    }
}
