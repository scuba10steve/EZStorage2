package io.github.scuba10steve.s3.item;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ItemDolly extends BaseItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(ItemDolly.class);

    public ItemDolly(int capacity) {
        super(new Item.Properties().durability(capacity));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        LOGGER.debug("useOn called - side: {}, hand: {}, pos: {}, player: {}",
            level.isClientSide ? "CLIENT" : "SERVER",
            context.getHand(),
            context.getClickedPos(),
            context.getPlayer() != null ? context.getPlayer().getName().getString() : "null");

        if (context.getHand() != InteractionHand.MAIN_HAND) {
            LOGGER.debug("Rejected: not main hand");
            return InteractionResult.PASS;
        }

        ItemStack stack = context.getItemInHand();
        LOGGER.debug("Item in hand: {}, has CUSTOM_DATA: {}", stack.getItem(), stack.has(DataComponents.CUSTOM_DATA));
        CompoundTag customTag = getCustomTag(stack);

        if (customTag.getBoolean("isFull")) {
            LOGGER.debug("Dolly is full, attempting to place stored block");
            if (!level.isClientSide) {
                placeStoredBlock(context, stack, customTag);
            }
            return InteractionResult.SUCCESS;
        } else {
            BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
            LOGGER.debug("Dolly is empty, block entity at pos: {}", blockEntity != null ? blockEntity.getClass().getSimpleName() : "null");
            if (blockEntity instanceof ChestBlockEntity || blockEntity instanceof StorageCoreBlockEntity) {
                LOGGER.debug("Valid target found, picking up block");
                if (!level.isClientSide) {
                    pickUpBlock(context, stack, blockEntity);
                }
                return InteractionResult.SUCCESS;
            }
        }

        LOGGER.debug("No action taken, returning PASS");
        return InteractionResult.PASS;
    }

    private void placeStoredBlock(UseOnContext context, ItemStack stack, CompoundTag customTag) {
        Level level = context.getLevel();
        BlockPos placePos = context.getClickedPos().relative(context.getClickedFace());
        String blockTypeStr = customTag.getString("blockType");
        Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockTypeStr));

        boolean isChest = customTag.getBoolean("isChest");
        boolean isStorageCore = customTag.getBoolean("isStorageCore");

        BlockState state = null;
        if (isChest) {
            state = block.defaultBlockState().setValue(
                ChestBlock.FACING,
                context.getPlayer().getDirection().getOpposite()
            );
        } else if (isStorageCore) {
            state = block.defaultBlockState();
        }

        if (state != null) {
            level.setBlock(placePos, state, 3);
            BlockEntity newBE = level.getBlockEntity(placePos);
            if (newBE != null) {
                CompoundTag storedData = customTag.getCompound("stored");
                storedData.putInt("x", placePos.getX());
                storedData.putInt("y", placePos.getY());
                storedData.putInt("z", placePos.getZ());
                newBE.loadWithComponents(storedData, level.registryAccess());
                if (newBE instanceof StorageCoreBlockEntity core) {
                    core.scanMultiblock();
                }
            }

            stack.remove(DataComponents.CUSTOM_DATA);
            stack.hurtAndBreak(1, context.getPlayer(), EquipmentSlot.MAINHAND);
        }
    }

    private void pickUpBlock(UseOnContext context, ItemStack stack, BlockEntity blockEntity) {
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        BlockState clickedState = level.getBlockState(clickedPos);

        CompoundTag storedData = blockEntity.saveWithFullMetadata(level.registryAccess());

        CompoundTag tag = new CompoundTag();
        tag.putBoolean("isFull", true);
        tag.putString("blockType", BuiltInRegistries.BLOCK.getKey(clickedState.getBlock()).toString());
        tag.putBoolean("isChest", blockEntity instanceof ChestBlockEntity);
        tag.putBoolean("isStorageCore", blockEntity instanceof StorageCoreBlockEntity);
        tag.put("stored", storedData);

        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        // Remove block entity first to prevent item drops, then remove the block
        level.removeBlockEntity(clickedPos);
        level.setBlock(clickedPos, Blocks.AIR.defaultBlockState(), 3);
    }

    @Override
    public Component getName(ItemStack stack) {
        Component baseName = super.getName(stack);
        CompoundTag customTag = getCustomTag(stack);
        if (customTag.getBoolean("isFull")) {
            return baseName.copy().append(" (Full)");
        }
        return baseName.copy().append(" (Empty)");
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        CompoundTag customTag = getCustomTag(stack);
        if (customTag.getBoolean("isFull")) {
            String blockTypeStr = customTag.getString("blockType");
            Block block = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(blockTypeStr));
            tooltip.add(block.getName());
        }
    }

    private CompoundTag getCustomTag(ItemStack stack) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData != null) {
            return customData.copyTag();
        }
        return new CompoundTag();
    }
}
