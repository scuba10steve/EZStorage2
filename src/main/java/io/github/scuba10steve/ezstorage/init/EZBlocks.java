package io.github.scuba10steve.ezstorage.init;

import io.github.scuba10steve.ezstorage.block.BlockStorage;
import io.github.scuba10steve.ezstorage.block.BlockStorageCore;
import io.github.scuba10steve.ezstorage.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EZBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, RefStrings.MODID);

    // Storage blocks with default capacities (config values applied at runtime)
    public static final Supplier<Block> STORAGE_BOX = BLOCKS.register("storage_box", 
        () -> new BlockStorage(10000));
    public static final Supplier<Block> CONDENSED_STORAGE_BOX = BLOCKS.register("condensed_storage_box", 
        () -> new BlockStorage(40000));
    public static final Supplier<Block> SUPER_STORAGE_BOX = BLOCKS.register("super_storage_box", 
        () -> new BlockStorage(160000));
    public static final Supplier<Block> ULTRA_STORAGE_BOX = BLOCKS.register("ultra_storage_box", 
        () -> new BlockStorage(640000));
    public static final Supplier<Block> HYPER_STORAGE_BOX = BLOCKS.register("hyper_storage_box", 
        () -> new BlockStorage(2560000));
    public static final Supplier<Block> STORAGE_CORE = BLOCKS.register("storage_core", BlockStorageCore::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
