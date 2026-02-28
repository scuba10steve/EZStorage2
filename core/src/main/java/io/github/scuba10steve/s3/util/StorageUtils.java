package io.github.scuba10steve.s3.util;

import io.github.scuba10steve.s3.block.BlockSecurityBox;
import io.github.scuba10steve.s3.block.StorageMultiblock;
import io.github.scuba10steve.s3.blockentity.SecurityBoxBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class StorageUtils {
    public static void getModNameFromID(String modId) {
        // Placeholder for mod name mapping
    }

    public static List<BlockRef> getNeighbors(BlockPos pos, Level level) {
        List<BlockRef> neighbors = new ArrayList<>();

        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            Block block = level.getBlockState(neighborPos).getBlock();
            neighbors.add(new BlockRef(block, neighborPos));
        }

        return neighbors;
    }

    /**
     * Walks connected StorageMultiblock blocks recursively to find a SecurityBoxBlockEntity.
     * Returns the SecurityBoxBlockEntity if found, null otherwise.
     */
    public static SecurityBoxBlockEntity findSecurityBox(BlockRef start, Level level) {
        return findSecurityBox(start, level, new HashSet<>());
    }

    private static SecurityBoxBlockEntity findSecurityBox(BlockRef start, Level level, Set<BlockRef> scanned) {
        if (!scanned.add(start)) return null;

        // Check if this block itself is a security box
        if (start.block instanceof BlockSecurityBox) {
            if (level.getBlockEntity(start.pos) instanceof SecurityBoxBlockEntity securityBox) {
                return securityBox;
            }
        }

        // Walk neighbors
        for (BlockRef neighbor : getNeighbors(start.pos, level)) {
            if (neighbor.block instanceof StorageMultiblock) {
                SecurityBoxBlockEntity result = findSecurityBox(neighbor, level, scanned);
                if (result != null) return result;
            }
        }

        return null;
    }
}
