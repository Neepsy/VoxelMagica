package com.neepsy.voxelmagica.util;

import com.neepsy.voxelmagica.items.ModItems;
import net.minecraft.item.Item;
import java.util.HashSet;
import java.util.Set;

public class Constants {
    //set of all spells and other items which are "on" the global cooldown;
    public static Set<Item> triggersGlobalCooldown;

    //All items which need a different hand/GUI model
    public static Set<Item> needCustomModel;

    public static void setup(){
        triggersGlobalCooldown = new HashSet<Item>();
        triggersGlobalCooldown.add(ModItems.ICONITEM);
        triggersGlobalCooldown.add(ModItems.SPELLINFUSEITEM);
    }
}
