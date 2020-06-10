package com.neepsy.voxelmagica.util;

import com.neepsy.voxelmagica.items.ModItems;
import net.minecraft.item.Item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constants {
    //set of all spells and other items which are "on" the global cooldown;
    public static Set<Item> triggersGlobalCooldown;

    //All items which need a different hand/GUI model
    public static Set<Item> needCustomModel;

    //"Recipes" for infusion
    public static Map<Item,Item> infusionResults;

    public static void setup(){
        triggersGlobalCooldown = new HashSet<Item>();
        triggersGlobalCooldown.add(ModItems.ICONITEM);
        triggersGlobalCooldown.add(ModItems.SPELLINFUSEITEM);

        infusionResults = new HashMap<Item,Item>();

    }
}
