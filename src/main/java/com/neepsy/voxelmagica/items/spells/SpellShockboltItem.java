package com.neepsy.voxelmagica.items.spells;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.effects.ModEffects;
import com.neepsy.voxelmagica.entity.AeroblastProjectileEntity;
import com.neepsy.voxelmagica.entity.ShockboltProjectileEntity;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.Config;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpellShockboltItem extends Item {

    private static int manaCostFull = 600;
    private static int manaCostReduced = 250;
    private static int cooldown = Config.GCD.get();

    public SpellShockboltItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("spellshockbolt");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("MP Cost: 600 (250)"));
        tooltip.add(new StringTextComponent("Smartcast").applyTextStyle(TextFormatting.GOLD).appendSibling(new StringTextComponent(" reduces mana cost").applyTextStyle(TextFormatting.RESET)));
        tooltip.add(new StringTextComponent("Grants one stack of ").appendSibling(new StringTextComponent("Umbral Charge").applyTextStyle(TextFormatting.GOLD)));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {

            EffectInstance smartcastEffect = playerIn.getActivePotionEffect(ModEffects.SMARTCAST_EFFECT);
            int manaCost = smartcastEffect == null ? manaCostFull : manaCostReduced;
            manaCost = playerIn.isCreative() ? 0 : manaCost;

            if(m.getMana() >= manaCost || playerIn.isCreative()){
                m.consume(playerIn.isCreative() ? manaCost : manaCost);
                ModItems.triggerGCD(playerIn, cooldown);

                //reduce level of smartcast by one
                if(smartcastEffect != null){
                    int newAmp = smartcastEffect.getAmplifier() - 1;
                    playerIn.removePotionEffect(ModEffects.SMARTCAST_EFFECT);
                    if(newAmp >= 0){
                        EffectInstance newEffect = new EffectInstance(ModEffects.SMARTCAST_EFFECT,
                                smartcastEffect.getDuration(),newAmp,true,true);
                        playerIn.addPotionEffect(newEffect);
                    }
                }

                //give stack of black mana (up to 5)
                EffectInstance blackMana = playerIn.getActivePotionEffect(ModEffects.BLACKMANA_EFFECT);
                int manaLevel = blackMana == null ? 0 : blackMana.getAmplifier() + 1;
                manaLevel = Math.min(manaLevel,4);
                EffectInstance newMana = new EffectInstance(ModEffects.BLACKMANA_EFFECT,12000,manaLevel,true,true);
                playerIn.addPotionEffect(newMana);


                if(!worldIn.isRemote()){
                    ShockboltProjectileEntity projectile = new ShockboltProjectileEntity (playerIn, worldIn);
                    projectile.setMotion(0,0,0);
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,3.2f,0);
                    worldIn.addEntity(projectile);
                    worldIn.playSound(null,playerIn.getPosX(),playerIn.getPosY(),playerIn.getPosZ(),
                            SoundEvents.ENTITY_BLAZE_SHOOT, SoundCategory.PLAYERS,.5f,2f);
                }

            }
        });

        return ActionResult.resultSuccess(item);
    }

}
