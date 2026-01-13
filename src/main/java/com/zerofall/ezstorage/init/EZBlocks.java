package com.zerofall.ezstorage.init;

import com.zerofall.ezstorage.block.BlockStorage;
import com.zerofall.ezstorage.block.BlockStorageCore;
import com.zerofall.ezstorage.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EZBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(BuiltInRegistries.BLOCK, RefStrings.MODID);

    // Storage blocks
    public static final Supplier<Block> STORAGE_BOX = BLOCKS.register("storage_box", BlockStorage::new);
    public static final Supplier<Block> STORAGE_CORE = BLOCKS.register("storage_core", BlockStorageCore::new);

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
