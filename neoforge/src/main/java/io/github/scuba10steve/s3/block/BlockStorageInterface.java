package io.github.scuba10steve.s3.block;

import io.github.scuba10steve.s3.blockentity.StorageInterfaceBlockEntity;
import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStorageInterface extends StorageMultiblock implements EntityBlock {

    public BlockStorageInterface() {
        super(Properties.of().strength(3.5f));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StorageInterfaceBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide()) return null;
        return type == S3Platform.getStorageInterfaceBEType()
            ? (lvl, pos, st, be) -> ((StorageInterfaceBlockEntity) be).tick()
            : null;
    }
}
