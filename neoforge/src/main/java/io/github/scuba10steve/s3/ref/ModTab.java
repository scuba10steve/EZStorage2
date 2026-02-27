package io.github.scuba10steve.s3.ref;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class ModTab {
    public static final CreativeModeTab TAB = CreativeModeTab.builder()
        .title(net.minecraft.network.chat.Component.translatable("itemGroup.s3"))
        .icon(() -> new ItemStack(Items.CHEST))
        .build();
}
