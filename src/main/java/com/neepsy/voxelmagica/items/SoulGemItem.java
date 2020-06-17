package com.neepsy.voxelmagica.items;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class SoulGemItem extends Item {

    //private float manaPercent;
    private static final long UPDATE_FREQ_MILLIS = 6000;
    private int ticksSinceUpdate = 0;

    public SoulGemItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("soulgem");
        this.addPropertyOverride(new ResourceLocation("condition"),new SoulGemAnimator());

    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        updateMana(playerIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(!worldIn.isRemote()){
                playerIn.sendMessage(generateMessage(item));
            }
        });

        return ActionResult.resultPass(item);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(generateMessage(stack));
        tooltip.add(new StringTextComponent("Ok, it's not actually your soul gem"));
    }



    private StringTextComponent generateMessage(ItemStack stack){
        CompoundNBT nbt = stack.getOrCreateTag();
        int manaPercent = (int) (((float) nbt.getInt("mana") / 10000) * 100);
        StringTextComponent msg = new StringTextComponent("Soul gem at ");
        StringTextComponent percentage = new StringTextComponent(manaPercent + "%");
        if(manaPercent >= 95)
            percentage.applyTextStyle(TextFormatting.AQUA);
        else if(manaPercent >= 60)
            percentage.applyTextStyle(TextFormatting.GREEN);
        else if(manaPercent >= 30)
            percentage.applyTextStyle(TextFormatting.GOLD);
        else
            percentage.applyTextStyle(TextFormatting.RED);
        msg.appendSibling(percentage);
        return msg;
    }


    public int updateMana(Entity entity){
        AtomicInteger mana = new AtomicInteger(-1);
        if(entity instanceof  PlayerEntity){
            PlayerEntity playerIn = (PlayerEntity) entity;

            playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
                mana.set(m.getMana());
            });
        }
        return mana.get();
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        super.inventoryTick(stack,worldIn,entityIn,itemSlot,isSelected);
        ticksSinceUpdate++;

        if(entityIn instanceof  PlayerEntity && ticksSinceUpdate > 20 && !worldIn.isRemote()){
            PlayerEntity playerIn = (PlayerEntity) entityIn;
            int mana = updateMana(playerIn);
            CompoundNBT nbt = stack.getOrCreateTag();
            nbt.putInt("mana",mana);
            ticksSinceUpdate = 0;
        }
    }
}
