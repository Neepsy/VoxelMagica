package com.neepsy.voxelmagica.items;

import com.neepsy.voxelmagica.items.spells.SpellInfuseItem;
import com.neepsy.voxelmagica.util.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {

    @ObjectHolder("voxelmagica:iconitem")
    public static final IconItem ICONITEM = null;

    @ObjectHolder("voxelmagica:spellinfuse")
    public static final SpellInfuseItem SPELLINFUSEITEM = null;


    public static void triggerGCD(PlayerEntity player){
        for(Item i : Constants.triggersGlobalCooldown){
            player.getCooldownTracker().setCooldown(i, 40);
        }
    }

}
