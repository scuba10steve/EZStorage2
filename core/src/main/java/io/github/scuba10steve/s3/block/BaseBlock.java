package io.github.scuba10steve.s3.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class BaseBlock extends Block {
    protected BaseBlock() {
        super(BlockBehaviour.Properties.of()
            .mapColor(MapColor.WOOD)
            .strength(2.0f)
        );
    }

    protected BaseBlock(Properties properties) {
        super(properties);
    }
}
