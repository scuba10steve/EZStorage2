package com.zerofall.ezstorage.init;

import com.zerofall.ezstorage.blockentity.StorageCoreBlockEntity;
import com.zerofall.ezstorage.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class EZBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RefStrings.MODID);

    public static final Supplier<BlockEntityType<StorageCoreBlockEntity>> STORAGE_CORE = 
        BLOCK_ENTITIES.register("storage_core", () -> 
            BlockEntityType.Builder.of(StorageCoreBlockEntity::new, EZBlocks.STORAGE_CORE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
