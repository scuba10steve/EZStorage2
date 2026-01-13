package com.zerofall.ezstorage.block;

import com.zerofall.ezstorage.ref.EZTab;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class EZBlock extends Block {
    protected EZBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.0f)
        );
    }
}
