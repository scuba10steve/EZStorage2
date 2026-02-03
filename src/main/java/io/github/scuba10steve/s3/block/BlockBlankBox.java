package io.github.scuba10steve.s3.block;

/**
 * A blank multiblock component for simple structure extension.
 * This block has no functionality - it simply allows players to extend
 * their storage multiblock structure for aesthetic purposes.
 */
public class BlockBlankBox extends StorageMultiblock {

    public BlockBlankBox() {
        super(Properties.of().strength(2.0f));
    }
}
