package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefStrings.MODID);

    public static final Supplier<CreativeModeTab> S3_TAB = CREATIVE_MODE_TABS.register("s3", () -> 
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.s3"))
            .icon(() -> new ItemStack(ModBlocks.STORAGE_CORE.get()))
            .displayItems((parameters, output) -> {
                // Add all blocks
                output.accept(ModItems.STORAGE_CORE.get());
                output.accept(ModItems.STORAGE_BOX.get());
                output.accept(ModItems.CONDENSED_STORAGE_BOX.get());
                output.accept(ModItems.COMPRESSED_STORAGE_BOX.get());
                output.accept(ModItems.SUPER_STORAGE_BOX.get());
                output.accept(ModItems.ULTRA_STORAGE_BOX.get());
                output.accept(ModItems.HYPER_STORAGE_BOX.get());
                output.accept(ModItems.ULTIMATE_STORAGE_BOX.get());
                output.accept(ModItems.CRAFTING_BOX.get());
                output.accept(ModItems.SEARCH_BOX.get());
                output.accept(ModItems.SORT_BOX.get());
                output.accept(ModItems.BLANK_BOX.get());
                output.accept(ModItems.INPUT_PORT.get());
                output.accept(ModItems.EXTRACT_PORT.get());
                output.accept(ModItems.EJECT_PORT.get());
                output.accept(ModItems.SECURITY_BOX.get());
                output.accept(ModItems.ACCESS_TERMINAL.get());

                // Add all items
                output.accept(ModItems.KEY.get());
                output.accept(ModItems.DOLLY.get());
                output.accept(ModItems.DOLLY_SUPER.get());
            })
            .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
