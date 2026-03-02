package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.block.BlockAccessTerminal;
import io.github.scuba10steve.s3.block.BlockBlankBox;
import io.github.scuba10steve.s3.block.BlockCraftingBox;
import io.github.scuba10steve.s3.block.BlockEjectPort;
import io.github.scuba10steve.s3.block.BlockExtractPort;
import io.github.scuba10steve.s3.block.BlockInputPort;
import io.github.scuba10steve.s3.block.BlockSearchBox;
import io.github.scuba10steve.s3.block.BlockSecurityBox;
import io.github.scuba10steve.s3.block.BlockSortBox;
import io.github.scuba10steve.s3.block.BlockStorage;
import io.github.scuba10steve.s3.block.BlockStorageCore;
import io.github.scuba10steve.s3.block.BlockStorageInterface;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, RefStrings.MODID);

    // Storage blocks with default capacities and tier keys (config values applied at runtime)
    public static final Supplier<Block> STORAGE_BOX = BLOCKS.register("storage_box",
        () -> new BlockStorage(10000, "basic"));
    public static final Supplier<Block> CONDENSED_STORAGE_BOX = BLOCKS.register("condensed_storage_box",
        () -> new BlockStorage(40000, "condensed"));
    public static final Supplier<Block> COMPRESSED_STORAGE_BOX = BLOCKS.register("compressed_storage_box",
        () -> new BlockStorage(80000, "compressed"));
    public static final Supplier<Block> SUPER_STORAGE_BOX = BLOCKS.register("super_storage_box",
        () -> new BlockStorage(160000, "super"));
    public static final Supplier<Block> ULTRA_STORAGE_BOX = BLOCKS.register("ultra_storage_box",
        () -> new BlockStorage(640000, "ultra"));
    public static final Supplier<Block> HYPER_STORAGE_BOX = BLOCKS.register("hyper_storage_box",
        () -> new BlockStorage(2560000, "hyper"));
    public static final Supplier<Block> ULTIMATE_STORAGE_BOX = BLOCKS.register("ultimate_storage_box",
        () -> new BlockStorage(10240000, "ultimate"));
    public static final Supplier<Block> STORAGE_CORE = BLOCKS.register("storage_core", BlockStorageCore::new);
    public static final Supplier<Block> CRAFTING_BOX = BLOCKS.register("crafting_box", BlockCraftingBox::new);
    public static final Supplier<Block> SEARCH_BOX = BLOCKS.register("search_box", BlockSearchBox::new);
    public static final Supplier<Block> SORT_BOX = BLOCKS.register("sort_box", BlockSortBox::new);
    public static final Supplier<Block> BLANK_BOX = BLOCKS.register("blank_box", BlockBlankBox::new);
    public static final Supplier<Block> INPUT_PORT = BLOCKS.register("input_port", BlockInputPort::new);
    public static final Supplier<Block> EXTRACT_PORT = BLOCKS.register("extract_port", BlockExtractPort::new);
    public static final Supplier<Block> EJECT_PORT = BLOCKS.register("eject_port", BlockEjectPort::new);
    public static final Supplier<Block> SECURITY_BOX = BLOCKS.register("security_box", BlockSecurityBox::new);
    public static final Supplier<Block> ACCESS_TERMINAL = BLOCKS.register("access_terminal", BlockAccessTerminal::new);
    public static final Supplier<Block> STORAGE_INTERFACE = BLOCKS.register("storage_interface", BlockStorageInterface::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
