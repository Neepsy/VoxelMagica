package com.neepsy.voxelmagica.items;

import net.minecraft.item.Item;

public class IconItem extends Item {
    public IconItem(){
        super(new Item.Properties().maxStackSize(1));
        setRegistryName("iconitem");
    }
}
