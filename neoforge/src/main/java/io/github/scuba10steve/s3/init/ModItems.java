package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.item.ItemDolly;
import io.github.scuba10steve.s3.item.ItemKey;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, RefStrings.MODID);

    // Block items
    public static final Supplier<Item> STORAGE_BOX = ITEMS.register("storage_box", 
        () -> new BlockItem(ModBlocks.STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> CONDENSED_STORAGE_BOX = ITEMS.register("condensed_storage_box", 
        () -> new BlockItem(ModBlocks.CONDENSED_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> COMPRESSED_STORAGE_BOX = ITEMS.register("compressed_storage_box", 
        () -> new BlockItem(ModBlocks.COMPRESSED_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> SUPER_STORAGE_BOX = ITEMS.register("super_storage_box", 
        () -> new BlockItem(ModBlocks.SUPER_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> ULTRA_STORAGE_BOX = ITEMS.register("ultra_storage_box", 
        () -> new BlockItem(ModBlocks.ULTRA_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> HYPER_STORAGE_BOX = ITEMS.register("hyper_storage_box", 
        () -> new BlockItem(ModBlocks.HYPER_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> ULTIMATE_STORAGE_BOX = ITEMS.register("ultimate_storage_box", 
        () -> new BlockItem(ModBlocks.ULTIMATE_STORAGE_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> STORAGE_CORE = ITEMS.register("storage_core", 
        () -> new BlockItem(ModBlocks.STORAGE_CORE.get(), new Item.Properties()));
    public static final Supplier<Item> CRAFTING_BOX = ITEMS.register("crafting_box", 
        () -> new BlockItem(ModBlocks.CRAFTING_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> SEARCH_BOX = ITEMS.register("search_box",
        () -> new BlockItem(ModBlocks.SEARCH_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> SORT_BOX = ITEMS.register("sort_box",
        () -> new BlockItem(ModBlocks.SORT_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> BLANK_BOX = ITEMS.register("blank_box",
        () -> new BlockItem(ModBlocks.BLANK_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> INPUT_PORT = ITEMS.register("input_port",
        () -> new BlockItem(ModBlocks.INPUT_PORT.get(), new Item.Properties()));
    public static final Supplier<Item> EXTRACT_PORT = ITEMS.register("extract_port",
        () -> new BlockItem(ModBlocks.EXTRACT_PORT.get(), new Item.Properties()));
    public static final Supplier<Item> EJECT_PORT = ITEMS.register("eject_port",
        () -> new BlockItem(ModBlocks.EJECT_PORT.get(), new Item.Properties()));
    public static final Supplier<Item> SECURITY_BOX = ITEMS.register("security_box",
        () -> new BlockItem(ModBlocks.SECURITY_BOX.get(), new Item.Properties()));
    public static final Supplier<Item> ACCESS_TERMINAL = ITEMS.register("access_terminal",
        () -> new BlockItem(ModBlocks.ACCESS_TERMINAL.get(), new Item.Properties()));
    public static final Supplier<Item> STORAGE_INTERFACE = ITEMS.register("storage_interface",
        () -> new BlockItem(ModBlocks.STORAGE_INTERFACE.get(), new Item.Properties()));

    // Items
    public static final Supplier<Item> KEY = ITEMS.register("key", ItemKey::new);
    public static final Supplier<Item> DOLLY = ITEMS.register("dolly", () -> new ItemDolly(6));
    public static final Supplier<Item> DOLLY_SUPER = ITEMS.register("dolly_super", () -> new ItemDolly(16));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
