package com.neepsy.voxelmagica.util;

import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.items.spells.SpellJoltItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Constants {

    private static Constants instance;
    private Constants(){setup();}

    public static synchronized Constants getInstance(){
        if(instance == null){
            instance = new Constants();
        }
        return instance;
    }
    //set of all spells and other items which are "on" the global cooldown;
    public Set<Item> triggersGlobalCooldown;

    //All items which need a different in hand model
    public Map<Item, Integer> needsCustomModel;

    //"Recipes" for infusion
    public Map<Item,Item> infusionRecipes;

    public void setup(){
        triggersGlobalCooldown = new HashSet();
        triggersGlobalCooldown.add(ModItems.ICONITEM);
        triggersGlobalCooldown.add(ModItems.SPELLINFUSEITEM);
        triggersGlobalCooldown.add(ModItems.SPELLJOLTITEM);

        infusionRecipes = new HashMap();
        infusionRecipes.put(Items.COAL, Items.DIAMOND);
        infusionRecipes.put(Items.IRON_INGOT, Items.GOLD_INGOT);

        needsCustomModel = new HashMap();
        needsCustomModel.put(ModItems.SPELLINFUSEITEM, 0);
        needsCustomModel.put(ModItems.ICONITEM, 1);
        needsCustomModel.put(ModItems.SPELLJOLTITEM, 1);

    }
}
