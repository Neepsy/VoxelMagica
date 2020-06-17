package com.neepsy.voxelmagica.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoulGemAnimator implements IItemPropertyGetter {
    @Override
    public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity entity) {
        Item item = itemStack.getItem();
        if(!(item instanceof SoulGemItem)){
            return 1;
        }

        SoulGemItem soulGem = (SoulGemItem) item;
        CompoundNBT nbt = itemStack.getOrCreateTag();
        int mana = nbt.getInt("mana");
        return ((float) mana) / 10000;
    }
}

