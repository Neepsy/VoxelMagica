package com.neepsy.voxelmagica.items;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class SoulGemAnimator implements IItemPropertyGetter {
    @Override
    public float call(ItemStack itemStack, @Nullable World world, @Nullable LivingEntity entity) {
        Item item = itemStack.getItem();
        if(!(item instanceof SoulGemItem)){
            //This shouldn't happen
            return 0;
        }
        SoulGemItem soulGem = (SoulGemItem) item;
        return soulGem.getManaPercentage();
    }
}

