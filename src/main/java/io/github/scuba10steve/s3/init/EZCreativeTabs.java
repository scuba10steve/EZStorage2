package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EZCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = 
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, RefStrings.MODID);

    public static final Supplier<CreativeModeTab> EZSTORAGE_TAB = CREATIVE_MODE_TABS.register("s3", () -> 
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.s3"))
            .icon(() -> new ItemStack(EZBlocks.STORAGE_CORE.get()))
            .displayItems((parameters, output) -> {
                // Add all blocks
                output.accept(EZItems.STORAGE_CORE.get());
                output.accept(EZItems.STORAGE_BOX.get());
                output.accept(EZItems.CONDENSED_STORAGE_BOX.get());
                output.accept(EZItems.COMPRESSED_STORAGE_BOX.get());
                output.accept(EZItems.SUPER_STORAGE_BOX.get());
                output.accept(EZItems.ULTRA_STORAGE_BOX.get());
                output.accept(EZItems.HYPER_STORAGE_BOX.get());
                output.accept(EZItems.ULTIMATE_STORAGE_BOX.get());
                output.accept(EZItems.CRAFTING_BOX.get());
                output.accept(EZItems.SEARCH_BOX.get());
                output.accept(EZItems.SORT_BOX.get());

                // Add all items
                output.accept(EZItems.KEY.get());
                output.accept(EZItems.DOLLY.get());
                output.accept(EZItems.DOLLY_SUPER.get());
            })
            .build()
    );

    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
