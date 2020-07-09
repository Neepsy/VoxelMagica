package com.neepsy.voxelmagica.items.spells;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.effects.ModEffects;
import com.neepsy.voxelmagica.entity.JoltProjectileEntity;
import com.neepsy.voxelmagica.entity.ScorchProjectileEntity;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.Config;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.IntNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpellScorchItem extends Item{

    private static int manaCost = 300;
    private static int joltManaCost = 200;
    private static int cooldown = Config.GCD.get();
    private int ticksSinceUpdate = 0;

    //private boolean canCast = false;

    public SpellScorchItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("spellscorch");
        this.addPropertyOverride(new ResourceLocation("cancastscorch"), new scorchItemAnimator());
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("MP Cost: 300 (Jolt: 200)"));
        tooltip.add(new StringTextComponent("Requires 4/4 stacks of ").appendSibling(new StringTextComponent("B/W Charge").applyTextStyle(TextFormatting.GOLD)));
        tooltip.add(new StringTextComponent("Otherwise will cast ").appendSibling(new StringTextComponent("Jolt").applyTextStyle(TextFormatting.AQUA)));
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        if(nbt.getInt("ready") == 1){
            return new TranslationTextComponent(stack.getTranslationKey() + ".ready");
        }
        return super.getDisplayName(stack);

    }

    //If player does not have 4/4 stacks of astral and umbral charge, cast as base level spell
    public ActionResult<ItemStack> castJolt(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(m.getMana() >= manaCost || playerIn.isCreative()){
                m.consume(playerIn.isCreative() ? 0 : joltManaCost);
                ModItems.triggerGCD(playerIn, cooldown);

                EffectInstance smartcast = new EffectInstance(ModEffects.SMARTCAST_EFFECT,400,0,false,true);
                playerIn.addPotionEffect(smartcast);

                if(!worldIn.isRemote()){
                    JoltProjectileEntity projectile = new JoltProjectileEntity(playerIn, worldIn);
                    projectile.setMotion(0,0,0);
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,4,0);
                    worldIn.addEntity(projectile);
                    worldIn.playSound(null,playerIn.getPosX(),playerIn.getPosY(),playerIn.getPosZ(),
                            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,.5f,1.8f);
                }

            }
        });

        return ActionResult.resultSuccess(item);
    }



    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        //did not have 4/4 stacks, cast jolt
        if(!canCastScorch(playerIn)){
            return castJolt(worldIn,playerIn,handIn);
        }

        //cast scorch and remove mana
        ItemStack item = playerIn.getHeldItem(handIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(m.getMana() >= manaCost || playerIn.isCreative()){
                m.consume(playerIn.isCreative() ? 0 : manaCost);
                ModItems.triggerGCD(playerIn, cooldown);

                playerIn.removePotionEffect(ModEffects.WHITEMANA_EFFECT);
                playerIn.removePotionEffect(ModEffects.BLACKMANA_EFFECT);

                if(!worldIn.isRemote()){
                    ScorchProjectileEntity projectile = new ScorchProjectileEntity(playerIn, worldIn);
                    projectile.setMotion(0,0,0);
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,4.5f,0);
                    worldIn.addEntity(projectile);
                    worldIn.playSound(null,playerIn.getPosX(),playerIn.getPosY(),playerIn.getPosZ(),
                            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,.5f,1.8f);
                }

            }
        });

        return ActionResult.resultSuccess(item);

    }

    public boolean canCastScorch(PlayerEntity playerIn){
        EffectInstance whiteMana = playerIn.getActivePotionEffect(ModEffects.WHITEMANA_EFFECT);
        EffectInstance blackMana = playerIn.getActivePotionEffect(ModEffects.BLACKMANA_EFFECT);
        return whiteMana != null && whiteMana.getAmplifier() >= 3 && blackMana != null && blackMana.getAmplifier() >= 3;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack, worldIn, entityIn, itemSlot, isSelected);
        ticksSinceUpdate++;
        if(ticksSinceUpdate > 5 && !worldIn.isRemote()){

            boolean ready = entityIn instanceof PlayerEntity && canCastScorch((PlayerEntity) entityIn);
            CompoundNBT nbt = stack.getOrCreateTag();
            if(ready){
                nbt.putBoolean("ready",true);
            }
            else{
                nbt.putBoolean("ready",false);
            }
            ticksSinceUpdate = 0;
        }
    }

    public class scorchItemAnimator implements IItemPropertyGetter {
        @Override
        public float call(ItemStack item, @Nullable World world, @Nullable LivingEntity entity) {
            if(!(entity instanceof PlayerEntity)){
                return 0;
            }

            boolean ready = canCastScorch((PlayerEntity) entity);
            CompoundNBT nbt = item.getOrCreateTag();
            if(ready){
                nbt.putInt("ready",1);
            }
            else{
                nbt.putInt("ready",0);
            }

            return ready ? 1 : 0;
        }
    }
}
