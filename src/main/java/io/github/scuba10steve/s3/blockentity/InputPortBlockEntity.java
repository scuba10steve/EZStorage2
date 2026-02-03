package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.init.EZBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Block entity for the Input Port.
 * Provides a single-slot inventory that accepts items from hoppers/pipes
 * and automatically transfers them to the connected Storage Core.
 */
public class InputPortBlockEntity extends MultiblockBlockEntity {
    private static final Logger LOGGER = LoggerFactory.getLogger(InputPortBlockEntity.class);

    private ItemStack buffer = ItemStack.EMPTY;

    public InputPortBlockEntity(BlockPos pos, BlockState state) {
        super(EZBlockEntities.INPUT_PORT.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (level == null || level.isClientSide) return;

        // Transfer items from buffer to storage core
        if (hasCore() && !buffer.isEmpty()) {
            // Check if redstone powered (disabled when powered)
            if (!level.hasNeighborSignal(worldPosition)) {
                ItemStack remainder = core.insertItem(buffer);
                buffer = remainder;
                setChanged();
            }
        }
    }

    /**
     * Returns the item handler for capability exposure
     */
    public IItemHandler getItemHandler() {
        return new InputPortItemHandler();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (!buffer.isEmpty()) {
            tag.put("Buffer", buffer.save(registries));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("Buffer")) {
            buffer = ItemStack.parseOptional(registries, tag.getCompound("Buffer"));
        }
    }

    /**
     * Internal item handler implementation for the input port
     */
    private class InputPortItemHandler implements IItemHandler {

        @Override
        public int getSlots() {
            return 1;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            return slot == 0 ? buffer : ItemStack.EMPTY;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (slot != 0 || stack.isEmpty()) {
                return stack;
            }

            // Check if redstone powered (disabled when powered)
            if (level != null && level.hasNeighborSignal(worldPosition)) {
                return stack;
            }

            int spaceAvailable = 64 - buffer.getCount();
            if (spaceAvailable <= 0) {
                return stack;
            }

            if (buffer.isEmpty()) {
                if (!simulate) {
                    buffer = stack.copyWithCount(Math.min(stack.getCount(), 64));
                    setChanged();
                }
                if (stack.getCount() <= 64) {
                    return ItemStack.EMPTY;
                } else {
                    return stack.copyWithCount(stack.getCount() - 64);
                }
            } else if (ItemStack.isSameItemSameComponents(buffer, stack)) {
                int toInsert = Math.min(stack.getCount(), spaceAvailable);
                if (!simulate) {
                    buffer.grow(toInsert);
                    setChanged();
                }
                if (toInsert >= stack.getCount()) {
                    return ItemStack.EMPTY;
                } else {
                    return stack.copyWithCount(stack.getCount() - toInsert);
                }
            }

            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            // Input port does not allow extraction
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return slot == 0 && !stack.isEmpty();
        }
    }
}
