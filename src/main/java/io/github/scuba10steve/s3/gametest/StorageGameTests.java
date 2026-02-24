package io.github.scuba10steve.s3.gametest;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.storage.StorageInventory;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@GameTestHolder("s3")
@PrefixGameTestTemplate(false)
public class StorageGameTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(StorageGameTests.class);
    private static final BlockPos CORE_POS = new BlockPos(1, 1, 1);

    @GameTest(template = "core_with_storage_box", setupTicks = 5)
    public static void core_with_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 10000);
    }

    @GameTest(template = "core_with_condensed_storage_box", setupTicks = 5)
    public static void core_with_condensed_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 40000);
    }

    @GameTest(template = "core_with_compressed_storage_box", setupTicks = 5)
    public static void core_with_compressed_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 80000);
    }

    @GameTest(template = "core_with_super_storage_box", setupTicks = 5)
    public static void core_with_super_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 160000);
    }

    @GameTest(template = "core_with_ultra_storage_box", setupTicks = 5)
    public static void core_with_ultra_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 640000);
    }

    @GameTest(template = "core_with_hyper_storage_box", setupTicks = 5)
    public static void core_with_hyper_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 2560000);
    }

    @GameTest(template = "core_with_ultimate_storage_box", setupTicks = 5)
    public static void core_with_ultimate_storage_box(GameTestHelper helper) {
        assertMultiblockFormsAndStores(helper, 10240000);
    }

    @GameTest(template = "core_with_hyper_storage_box", setupTicks = 5, timeoutTicks = 200)
    public static void hyper_storage_extract_insert_performance(GameTestHelper helper) {
        helper.runAfterDelay(5, () -> {
            StorageCoreBlockEntity core = (StorageCoreBlockEntity) helper.getBlockEntity(CORE_POS);
            if (core == null) {
                helper.fail("Storage core block entity not found");
                return;
            }

            StorageInventory inv = core.getInventory();

            // Fill with every registered item type (bypassing core to skip sync overhead during setup)
            Item lastItem = null;
            int distinctTypes = 0;
            for (Item item : BuiltInRegistries.ITEM) {
                ItemStack stack = new ItemStack(item, 64);
                if (!stack.isEmpty()) {
                    inv.insertItem(stack);
                    lastItem = item;
                    distinctTypes++;
                }
            }

            // Fill remaining capacity with the first item type
            long remaining = inv.getMaxItems() - inv.getTotalItemCount();
            if (remaining > 0) {
                inv.insertItem(new ItemStack(Items.STONE, (int) Math.min(remaining, Integer.MAX_VALUE)));
            }

            LOGGER.info("Performance test: filled inventory with {} distinct item types, total count: {}/{}",
                distinctTypes, inv.getTotalItemCount(), inv.getMaxItems());

            if (lastItem == null) {
                helper.fail("No items were inserted");
                return;
            }

            // Time extract of the last item (worst case — end of list)
            ItemStack template = new ItemStack(lastItem, 1);

            long extractStart = System.nanoTime();
            ItemStack extracted = core.extractItem(template, 1);
            long extractNanos = System.nanoTime() - extractStart;

            if (extracted.isEmpty()) {
                helper.fail("Failed to extract " + lastItem);
                return;
            }

            // Time insert of the same item back (worst case — scans full list, finds no match, appends)
            long insertStart = System.nanoTime();
            core.insertItem(extracted);
            long insertNanos = System.nanoTime() - insertStart;

            long extractMicros = extractNanos / 1000;
            long insertMicros = insertNanos / 1000;

            LOGGER.info("Performance test results ({} distinct types):", distinctTypes);
            LOGGER.info("  Extract: {} us", extractMicros);
            LOGGER.info("  Insert:  {} us", insertMicros);

            if (extractNanos >= 1_000_000) {
                helper.fail("Extract took " + extractMicros + " us, exceeds 1ms threshold");
                return;
            }
            if (insertNanos >= 1_000_000) {
                helper.fail("Insert took " + insertMicros + " us, exceeds 1ms threshold");
                return;
            }

            helper.succeed();
        });
    }

    @GameTest(template = "large_ultimate_multiblock", setupTicks = 20, timeoutTicks = 400)
    public static void large_ultimate_extract_insert_performance(GameTestHelper helper) {
        BlockPos corePos = new BlockPos(5, 1, 5);

        helper.runAfterDelay(20, () -> {
            StorageCoreBlockEntity core = (StorageCoreBlockEntity) helper.getBlockEntity(corePos);
            if (core == null) {
                helper.fail("Storage core block entity not found");
                return;
            }

            StorageInventory inv = core.getInventory();

            // Fill with 10x of every registered item type
            Item lastItem = null;
            int distinctTypes = 0;
            for (Item item : BuiltInRegistries.ITEM) {
                ItemStack stack = new ItemStack(item, 640);
                if (!stack.isEmpty()) {
                    inv.insertItem(stack);
                    lastItem = item;
                    distinctTypes++;
                }
            }

            // Fill remaining capacity split between cobblestone and oak wood
            long remaining = inv.getMaxItems() - inv.getTotalItemCount();
            long halfRemaining = remaining / 2;

            bulkInsert(inv, Items.COBBLESTONE, halfRemaining);
            bulkInsert(inv, Items.OAK_LOG, inv.getMaxItems() - inv.getTotalItemCount());

            LOGGER.info("Large perf test: {} distinct types, total count: {}/{}",
                distinctTypes, inv.getTotalItemCount(), inv.getMaxItems());

            if (lastItem == null) {
                helper.fail("No items were inserted");
                return;
            }

            // Time extract of the last item (worst case)
            ItemStack template = new ItemStack(lastItem, 1);

            long extractStart = System.nanoTime();
            ItemStack extracted = core.extractItem(template, 1);
            long extractNanos = System.nanoTime() - extractStart;

            if (extracted.isEmpty()) {
                helper.fail("Failed to extract " + lastItem);
                return;
            }

            // Time insert of the same item back
            long insertStart = System.nanoTime();
            core.insertItem(extracted);
            long insertNanos = System.nanoTime() - insertStart;

            long extractMicros = extractNanos / 1000;
            long insertMicros = insertNanos / 1000;

            LOGGER.info("Large perf test results ({} distinct types, {} total items):", distinctTypes, inv.getTotalItemCount());
            LOGGER.info("  Extract: {} us", extractMicros);
            LOGGER.info("  Insert:  {} us", insertMicros);

            if (extractNanos >= 1_000_000) {
                helper.fail("Extract took " + extractMicros + " us, exceeds 1ms threshold");
                return;
            }
            if (insertNanos >= 1_000_000) {
                helper.fail("Insert took " + insertMicros + " us, exceeds 1ms threshold");
                return;
            }

            helper.succeed();
        });
    }

    @GameTest(template = "core_with_storage_box", setupTicks = 5)
    public static void all_recipes_registered(GameTestHelper helper) {
        helper.runAfterDelay(5, () -> {
            RecipeManager recipeManager = helper.getLevel().getServer().getRecipeManager();

            String[] expectedRecipes = {
                "access_terminal", "blank_box", "compressed_storage_box", "condensed_storage_box",
                "crafting_box", "dolly", "dolly_super", "eject_port", "extract_port",
                "hyper_storage_box", "input_port", "key", "search_box", "security_box",
                "sort_box", "storage_box", "storage_core", "super_storage_box",
                "ultimate_storage_box", "ultra_storage_box"
            };

            List<String> missing = new ArrayList<>();
            for (String name : expectedRecipes) {
                ResourceLocation id = ResourceLocation.fromNamespaceAndPath("s3", name);
                Optional<RecipeHolder<?>> recipe = recipeManager.byKey(id);
                if (recipe.isEmpty()) {
                    missing.add(name);
                }
            }

            if (!missing.isEmpty()) {
                helper.fail("Missing " + missing.size() + " recipes: " + String.join(", ", missing));
                return;
            }

            LOGGER.info("All {} S3 recipes verified in RecipeManager", expectedRecipes.length);
            helper.succeed();
        });
    }

    private static void bulkInsert(StorageInventory inv, Item item, long amount) {
        while (amount > 0) {
            int batch = (int) Math.min(amount, Integer.MAX_VALUE);
            inv.insertItem(new ItemStack(item, batch));
            amount -= batch;
        }
    }

    private static void assertMultiblockFormsAndStores(GameTestHelper helper, long expectedCapacity) {
        helper.runAfterDelay(5, () -> {
            StorageCoreBlockEntity core = (StorageCoreBlockEntity) helper.getBlockEntity(CORE_POS);
            if (core == null) {
                helper.fail("Storage core block entity not found at " + CORE_POS);
                return;
            }

            StorageInventory inv = core.getInventory();

            if (inv.getMaxItems() != expectedCapacity) {
                helper.fail("Expected capacity " + expectedCapacity + ", got " + inv.getMaxItems());
                return;
            }

            core.insertItem(new ItemStack(Items.DIAMOND, 64));

            long totalCount = inv.getTotalItemCount();
            if (totalCount != 64) {
                helper.fail("Expected 64 total items, got " + totalCount);
                return;
            }

            helper.succeed();
        });
    }
}
