package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.StorageCoreBlockEntity;
import io.github.scuba10steve.s3.init.ModBlockEntities;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.BlockHitResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockStorageCore extends StorageMultiblock implements EntityBlock {
    private static final Logger LOGGER = LoggerFactory.getLogger(BlockStorageCore.class);
    
    public BlockStorageCore() {
        super(Properties.of().strength(2.0f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        LOGGER.debug("BlockStorageCore.newBlockEntity called at {}", pos);
        return new StorageCoreBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return type == ModBlockEntities.STORAGE_CORE.get() ? (level1, pos, state1, blockEntity) -> {
            if (blockEntity instanceof StorageCoreBlockEntity storageCore) {
                storageCore.tick();
            }
        } : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (!level.isClientSide) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof StorageCoreBlockEntity storageCore) {
                player.openMenu(storageCore, pos);
            }
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
