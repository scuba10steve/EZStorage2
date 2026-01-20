package io.github.scuba10steve.ezstorage.init;

import io.github.scuba10steve.ezstorage.ref.RefStrings;
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

    public static final Supplier<CreativeModeTab> EZSTORAGE_TAB = CREATIVE_MODE_TABS.register("ezstorage", () -> 
        CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.ezstorage"))
            .icon(() -> new ItemStack(EZBlocks.STORAGE_CORE.get()))
            .displayItems((parameters, output) -> {
                // Add all blocks
                output.accept(EZBlocks.STORAGE_CORE.get());
                output.accept(EZBlocks.STORAGE_BOX.get());
                output.accept(EZBlocks.CONDENSED_STORAGE_BOX.get());
                output.accept(EZBlocks.COMPRESSED_STORAGE_BOX.get());
                output.accept(EZBlocks.SUPER_STORAGE_BOX.get());
                output.accept(EZBlocks.ULTRA_STORAGE_BOX.get());
                output.accept(EZBlocks.HYPER_STORAGE_BOX.get());
                output.accept(EZBlocks.ULTIMATE_STORAGE_BOX.get());
                
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
