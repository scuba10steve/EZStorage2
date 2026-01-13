package com.zerofall.ezstorage.ref;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EZTab {
    public static final CreativeModeTab TAB = CreativeModeTab.builder()
        .title(net.minecraft.network.chat.Component.translatable("itemGroup.ezstorage"))
        .icon(() -> new ItemStack(Items.CHEST))
        .build();
}
