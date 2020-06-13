package com.neepsy.voxelmagica.items;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.util.Config;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GriefShardItem extends Item {
    private int TICKSTOUSE = 15;
    private final int MANARESTORED = Config.GRIEFSHARD_RESTORE.get();

    public GriefShardItem(){
        super(new Item.Properties().maxStackSize(32).group(VoxelMagica.creativeTab));
        setRegistryName("griefshard");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new StringTextComponent("Restores a small amount of mana"));
    }

    @Override
    public UseAction getUseAction(ItemStack stack) {
        return UseAction.BLOCK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return TICKSTOUSE;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        playerIn.setActiveHand(handIn);
        return new ActionResult<ItemStack>(ActionResultType.PASS, playerIn.getHeldItem(handIn));
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityIn) {
        if(entityIn instanceof PlayerEntity){
            PlayerEntity playerIn = (PlayerEntity) entityIn;
            playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m ->{
                m.add(MANARESTORED);
                stack.shrink(1);
                worldIn.playSound(null,playerIn.getPosX(),playerIn.getPosY(),playerIn.getPosZ(),SoundEvents.BLOCK_LAVA_EXTINGUISH, SoundCategory.PLAYERS,.9f,1.6f + (random.nextFloat()/4));


                if(worldIn.isRemote){
                    worldIn.addParticle(ParticleTypes.SMOKE,playerIn.getPosXRandom(.03),playerIn.getPosYRandom(),playerIn.getPosZRandom(.03),0,.1,0);
                }
            });
        }

        return stack;
    }
}
