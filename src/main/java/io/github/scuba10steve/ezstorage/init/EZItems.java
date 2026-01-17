package io.github.scuba10steve.ezstorage.init;

import io.github.scuba10steve.ezstorage.item.ItemDolly;
import io.github.scuba10steve.ezstorage.item.ItemKey;
import io.github.scuba10steve.ezstorage.ref.EZTab;
import io.github.scuba10steve.ezstorage.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EZItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RefStrings.MODID);

    // Block items
    public static final Supplier<Item> STORAGE_BOX = ITEMS.register("storage_box", 
        () -> new BlockItem(EZBlocks.STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> CONDENSED_STORAGE_BOX = ITEMS.register("condensed_storage_box", 
        () -> new BlockItem(EZBlocks.CONDENSED_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> COMPRESSED_STORAGE_BOX = ITEMS.register("compressed_storage_box", 
        () -> new BlockItem(EZBlocks.COMPRESSED_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> SUPER_STORAGE_BOX = ITEMS.register("super_storage_box", 
        () -> new BlockItem(EZBlocks.SUPER_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> ULTRA_STORAGE_BOX = ITEMS.register("ultra_storage_box", 
        () -> new BlockItem(EZBlocks.ULTRA_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> HYPER_STORAGE_BOX = ITEMS.register("hyper_storage_box", 
        () -> new BlockItem(EZBlocks.HYPER_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> STORAGE_CORE = ITEMS.register("storage_core", 
        () -> new BlockItem(EZBlocks.STORAGE_CORE.get(), new Item.Properties()));

    // Items
    public static final Supplier<Item> KEY = ITEMS.register("key", ItemKey::new);
    public static final Supplier<Item> DOLLY = ITEMS.register("dolly", () -> new ItemDolly(6));
    public static final Supplier<Item> DOLLY_SUPER = ITEMS.register("dolly_super", () -> new ItemDolly(16));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
