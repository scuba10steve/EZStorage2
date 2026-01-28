package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.blockentity.CraftingBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.SearchBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.ref.RefStrings;
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

    public static final Supplier<BlockEntityType<CraftingBoxBlockEntity>> CRAFTING_BOX = 
        BLOCK_ENTITIES.register("crafting_box", () -> 
            BlockEntityType.Builder.of(CraftingBoxBlockEntity::new, EZBlocks.CRAFTING_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<SearchBoxBlockEntity>> SEARCH_BOX = 
        BLOCK_ENTITIES.register("search_box", () -> 
            BlockEntityType.Builder.of(SearchBoxBlockEntity::new, EZBlocks.SEARCH_BOX.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
