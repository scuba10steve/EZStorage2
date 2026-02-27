package io.github.scuba10steve.s3.datagen;

import io.github.scuba10steve.s3.datagen.GameTestStructureProvider.BlockPlacement;
import io.github.scuba10steve.s3.datagen.GameTestStructureProvider.StructureContent;
import io.github.scuba10steve.s3.ref.RefStrings;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = RefStrings.MODID, bus = EventBusSubscriber.Bus.MOD)
public class S3DataGeneration {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        GameTestStructureProvider provider = new GameTestStructureProvider(
            event.getGenerator().getPackOutput(), RefStrings.MODID);

        // Generate a core + storage block structure for each tier
        addCoreWithBlock(provider, "storage_box");
        addCoreWithBlock(provider, "condensed_storage_box");
        addCoreWithBlock(provider, "compressed_storage_box");
        addCoreWithBlock(provider, "super_storage_box");
        addCoreWithBlock(provider, "ultra_storage_box");
        addCoreWithBlock(provider, "hyper_storage_box");
        addCoreWithBlock(provider, "ultimate_storage_box");

        // Large multiblock: 10x10x5 filled with ultimate boxes + core at center
        addLargeUltimateStructure(provider);

        event.getGenerator().addProvider(event.includeServer(), provider);
    }

    private static void addLargeUltimateStructure(GameTestStructureProvider provider) {
        List<BlockPlacement> blocks = new ArrayList<>();
        // Core at center of the base layer
        blocks.add(new BlockPlacement("s3:storage_core", 5, 0, 5));
        // Fill every other position with ultimate storage boxes
        for (int x = 0; x < 10; x++) {
            for (int z = 0; z < 10; z++) {
                for (int y = 0; y < 5; y++) {
                    if (x == 5 && y == 0 && z == 5) continue; // skip core position
                    blocks.add(new BlockPlacement("s3:ultimate_storage_box", x, y, z));
                }
            }
        }
        provider.add("large_ultimate_multiblock", 10, 5, 10, new StructureContent(
            blocks, List.of(), List.of()
        ));
    }

    private static void addCoreWithBlock(GameTestStructureProvider provider, String blockName) {
        provider.add("core_with_" + blockName, 3, 3, 3, new StructureContent(
            List.of(
                new BlockPlacement("s3:storage_core", 1, 0, 1),
                new BlockPlacement("s3:" + blockName, 2, 0, 1)
            ),
            List.of(),
            List.of()
        ));
    }
}
