package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.platform.S3Platform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Block entity for the Search Box.
 * The Search Box acts as a detection flag - when present in a multiblock,
 * it enables the search field in the Storage Core GUI.
 * It does not have its own GUI.
 */
public class SearchBoxBlockEntity extends MultiblockBlockEntity {

    public SearchBoxBlockEntity(BlockPos pos, BlockState state) {
        super(S3Platform.getSearchBoxBEType(), pos, state);
    }
}
