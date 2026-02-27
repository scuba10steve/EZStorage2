package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.init.ModBlockEntities;
import io.github.scuba10steve.s3.storage.StorageInventory;
import io.github.scuba10steve.s3.storage.StoredItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Eject Port Block Entity - Automatically ejects items from storage into an inventory above it.
 * Runs every tick when not powered by redstone.
 */
public class EjectPortBlockEntity extends MultiblockBlockEntity {

    public EjectPortBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EJECT_PORT.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;
        super.tick();
        
        // Don't eject if powered by redstone
        if (level.hasNeighborSignal(worldPosition)) return;
        
        StorageCoreBlockEntity core = getCore();
        if (core == null) return;
        
        // Check for inventory above
        BlockPos targetPos = worldPosition.above();
        BlockEntity targetEntity = level.getBlockEntity(targetPos);
        if (targetEntity == null) return;
        
        // Get the item handler capability
        IItemHandler targetHandler = level.getCapability(
            net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK,
            targetPos,
            level.getBlockState(targetPos),
            targetEntity,
            Direction.DOWN
        );
        
        if (targetHandler == null) return;
        
        // Get items from storage
        StorageInventory inventory = core.getInventory();
        List<StoredItemStack> items = inventory.getStoredItems();
        if (items.isEmpty()) return;
        
        // Try to eject the first item
        StoredItemStack stored = items.get(0);
        ItemStack toEject = stored.getItemStack().copy();
        int maxStackSize = toEject.getMaxStackSize();
        int ejectAmount = (int) Math.min(maxStackSize, stored.getCount());
        toEject.setCount(ejectAmount);
        
        // Try to insert into target inventory
        ItemStack remainder = insertItem(targetHandler, toEject);
        
        // Calculate how many items were actually inserted
        int inserted = ejectAmount - remainder.getCount();
        if (inserted > 0) {
            // Extract from storage
            inventory.extractItem(stored.getItemStack(), inserted);
            core.setChanged();
        }
    }
    
    /**
     * Insert an item into an item handler, trying all slots
     */
    @NotNull
    private ItemStack insertItem(IItemHandler handler, ItemStack stack) {
        ItemStack remaining = stack.copy();
        
        for (int slot = 0; slot < handler.getSlots() && !remaining.isEmpty(); slot++) {
            remaining = handler.insertItem(slot, remaining, false);
        }
        
        return remaining;
    }
}
