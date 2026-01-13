package com.zerofall.ezstorage.item;

public class ItemDolly extends EZItem {
    private final int capacity;
    
    public ItemDolly(int capacity) {
        super();
        this.capacity = capacity;
    }
    
    public int getCapacity() {
        return capacity;
    }
}
