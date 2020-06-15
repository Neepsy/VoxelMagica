package com.neepsy.voxelmagica.items;

import com.neepsy.voxelmagica.items.spells.SpellAeroblastItem;
import com.neepsy.voxelmagica.items.spells.SpellInfuseItem;
import com.neepsy.voxelmagica.items.spells.SpellJoltItem;
import com.neepsy.voxelmagica.items.spells.SpellShockboltItem;
import com.neepsy.voxelmagica.util.Constants;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraftforge.registries.ObjectHolder;

public class ModItems {

    @ObjectHolder("voxelmagica:iconitem")
    public static final IconItem ICONITEM = null;

    @ObjectHolder("voxelmagica:soulgem")
    public static final SoulGemItem SOULGEMITEM = null;

    @ObjectHolder("voxelmagica:spellinfuse")
    public static final SpellInfuseItem SPELLINFUSEITEM = null;

    @ObjectHolder("voxelmagica:spelljolt")
    public static final SpellJoltItem SPELLJOLTITEM = null;

    @ObjectHolder("voxelmagica:spellshockbolt")
    public static final SpellShockboltItem SPELLSHOCKBOLTITEM = null;

    @ObjectHolder("voxelmagica:spellaeroblast")
    public static final SpellAeroblastItem SPELLAEROBLASTITEM = null;

    @ObjectHolder("voxelmagica:griefshard")
    public static final GriefShardItem GRIEFSHARDITEM = null;




    public static void triggerGCD(PlayerEntity player, int ticks){
        for(Item i : Constants.getInstance().triggersGlobalCooldown){
            player.getCooldownTracker().setCooldown(i, ticks);
        }
    }

}
