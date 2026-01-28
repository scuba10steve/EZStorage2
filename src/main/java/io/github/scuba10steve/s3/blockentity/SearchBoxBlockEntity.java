package io.github.scuba10steve.s3.blockentity;

import io.github.scuba10steve.s3.init.EZBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class SearchBoxBlockEntity extends MultiblockBlockEntity implements MenuProvider {
    
    public SearchBoxBlockEntity(BlockPos pos, BlockState state) {
        super(EZBlockEntities.SEARCH_BOX.get(), pos, state);
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Search Box");
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return null; // TODO: Implement search menu
    }
}
