package com.neepsy.voxelmagica.items.spells;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.entity.InfuseProjectileEntity;
import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SpellInfuseItem extends Item {

    private int manaCost = 100;

    public SpellInfuseItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("spellinfuse");
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);

        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(m.getMana() >= manaCost){
                m.consume(manaCost);
                ModItems.triggerGCD(playerIn, 5);
                System.out.println(m.getMana() + " mana left!");

                if(!worldIn.isRemote()){
                    System.out.println("Attempting to spawn");
                    InfuseProjectileEntity projectile = new InfuseProjectileEntity(playerIn, worldIn);
                    projectile.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0,3,0);
                    worldIn.addEntity(projectile);
                }

            }
        });
        return ActionResult.resultSuccess(item);
    }


}
