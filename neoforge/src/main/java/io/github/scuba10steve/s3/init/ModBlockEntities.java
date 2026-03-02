package io.github.scuba10steve.s3.init;

import io.github.scuba10steve.s3.blockentity.CraftingBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.EjectPortBlockEntity;
import io.github.scuba10steve.s3.blockentity.ExtractPortBlockEntity;
import io.github.scuba10steve.s3.blockentity.InputPortBlockEntity;
import io.github.scuba10steve.s3.blockentity.SearchBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.SortBoxBlockEntity;
import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.blockentity.StorageInterfaceBlockEntity;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, RefStrings.MODID);

    public static final Supplier<BlockEntityType<StorageCoreBlockEntity>> STORAGE_CORE = 
        BLOCK_ENTITIES.register("storage_core", () -> 
            BlockEntityType.Builder.of(StorageCoreBlockEntity::new, ModBlocks.STORAGE_CORE.get()).build(null));

    public static final Supplier<BlockEntityType<CraftingBoxBlockEntity>> CRAFTING_BOX = 
        BLOCK_ENTITIES.register("crafting_box", () -> 
            BlockEntityType.Builder.of(CraftingBoxBlockEntity::new, ModBlocks.CRAFTING_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<SearchBoxBlockEntity>> SEARCH_BOX =
        BLOCK_ENTITIES.register("search_box", () ->
            BlockEntityType.Builder.of(SearchBoxBlockEntity::new, ModBlocks.SEARCH_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<SortBoxBlockEntity>> SORT_BOX =
        BLOCK_ENTITIES.register("sort_box", () ->
            BlockEntityType.Builder.of(SortBoxBlockEntity::new, ModBlocks.SORT_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<InputPortBlockEntity>> INPUT_PORT =
        BLOCK_ENTITIES.register("input_port", () ->
            BlockEntityType.Builder.of(InputPortBlockEntity::new, ModBlocks.INPUT_PORT.get()).build(null));

    public static final Supplier<BlockEntityType<ExtractPortBlockEntity>> EXTRACT_PORT =
        BLOCK_ENTITIES.register("extract_port", () ->
            BlockEntityType.Builder.of(ExtractPortBlockEntity::new, ModBlocks.EXTRACT_PORT.get()).build(null));

    public static final Supplier<BlockEntityType<EjectPortBlockEntity>> EJECT_PORT =
        BLOCK_ENTITIES.register("eject_port", () ->
            BlockEntityType.Builder.of(EjectPortBlockEntity::new, ModBlocks.EJECT_PORT.get()).build(null));

    public static final Supplier<BlockEntityType<SecurityBoxBlockEntity>> SECURITY_BOX =
        BLOCK_ENTITIES.register("security_box", () ->
            BlockEntityType.Builder.of(SecurityBoxBlockEntity::new, ModBlocks.SECURITY_BOX.get()).build(null));

    public static final Supplier<BlockEntityType<StorageInterfaceBlockEntity>> STORAGE_INTERFACE =
        BLOCK_ENTITIES.register("storage_interface", () ->
            BlockEntityType.Builder.of(StorageInterfaceBlockEntity::new, ModBlocks.STORAGE_INTERFACE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
