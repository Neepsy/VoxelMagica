package com.neepsy.voxelmagica.items.spells;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.entity.InfuseProjectileEntity;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SpellInfuseItem extends Item {

    private int manaCost = 100;

    public SpellInfuseItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("spellinfuse");

    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("MP Cost: 100"));
        tooltip.add(new StringTextComponent("Enchants certain items dropped in world"));
    }



    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);

        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(m.getMana() >= manaCost || playerIn.isCreative()){
                m.consume(playerIn.isCreative() ? 0 : manaCost);
                ModItems.triggerGCD(playerIn, 5);

                if(!worldIn.isRemote()){
                    InfuseProjectileEntity projectile = new InfuseProjectileEntity(playerIn, worldIn);
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,3,0);
                    worldIn.addEntity(projectile);
                }

            }
        });
        return ActionResult.resultSuccess(item);
    }


}
