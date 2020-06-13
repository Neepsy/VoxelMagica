package com.neepsy.voxelmagica.items.spells;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.effects.ModEffects;
import com.neepsy.voxelmagica.effects.SmartcastEffect;
import com.neepsy.voxelmagica.entity.InfuseProjectileEntity;
import com.neepsy.voxelmagica.entity.JoltProjectileEntity;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpellJoltItem extends Item{

    private int manaCost = 200;

    public SpellJoltItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("spelljolt");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("MP Cost: 200"));
        tooltip.add(new StringTextComponent("Grants ").appendSibling(new StringTextComponent("Smartcast").applyTextStyle(TextFormatting.GOLD)));
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(m.getMana() >= manaCost || playerIn.isCreative()){
                m.consume(playerIn.isCreative() ? 0 : manaCost);
                ModItems.triggerGCD(playerIn, 30);
                System.out.println(m.getMana() + " mana left!");

                EffectInstance smartcast = new EffectInstance(ModEffects.SMARTCAST_EFFECT,400,0,true,true);
                playerIn.addPotionEffect(smartcast);

                if(!worldIn.isRemote()){
                    JoltProjectileEntity projectile = new JoltProjectileEntity(playerIn, worldIn);
                    projectile.setMotion(0,0,0);
                    //projectile.accelerationX = 0;
                    //projectile.accelerationY = 0;
                    //projectile.accelerationZ = 0;
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,5,0);
                    worldIn.addEntity(projectile);
                }

            }
        });

        return ActionResult.resultSuccess(item);
    }
}