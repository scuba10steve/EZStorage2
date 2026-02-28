package io.github.scuba10steve.s3.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;

public class BlockRef {
    public final Block block;
    public final BlockPos pos;
    
    public BlockRef(Block block, BlockPos pos) {
        this.block = block;
        this.pos = pos;
    }
    
    public BlockRef(Block block, int x, int y, int z) {
        this.block = block;
        this.pos = new BlockPos(x, y, z);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BlockRef other)) return false;
        return block.equals(other.block) && pos.equals(other.pos);
    }
    
    @Override
    public int hashCode() {
        return pos.hashCode() * 31 + block.hashCode();
    }
}
