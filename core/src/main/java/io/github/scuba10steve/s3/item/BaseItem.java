package io.github.scuba10steve.s3.item;

import net.minecraft.world.item.Item;

public class BaseItem extends Item {
    protected BaseItem() {
        super(new Item.Properties());
    }

    protected BaseItem(Item.Properties properties) {
        super(properties);
    }
}
