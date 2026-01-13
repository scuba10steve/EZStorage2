package com.zerofall.ezstorage.blockentity;

import com.zerofall.ezstorage.init.EZBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class StorageCoreBlockEntity extends EZBlockEntity {
    public StorageCoreBlockEntity(BlockPos pos, BlockState state) {
        super(EZBlockEntities.STORAGE_CORE.get(), pos, state);
    }
}
