package com.neepsy.voxelmagica.items;

import com.neepsy.voxelmagica.VoxelMagica;
import com.neepsy.voxelmagica.util.ManaProvider;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class SoulGemItem extends Item {

    private float manaPercent;
    private static final long UPDATE_FREQ_MILLIS = 6000;
    private long lastUpdateTime;

    public SoulGemItem(){
        super(new Item.Properties().maxStackSize(1).group(VoxelMagica.creativeTab));
        setRegistryName("soulgem");
        lastUpdateTime = System.currentTimeMillis();
        this.addPropertyOverride(new ResourceLocation("condition"),new SoulGemAnimator());
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack item = playerIn.getHeldItem(handIn);
        playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
            if(!worldIn.isRemote()){
                recalculateMana(m.getMana());
                playerIn.sendMessage(generateMessage(getManaPercentageInt()));
            }
        });

        return ActionResult.resultPass(item);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(generateMessage(getManaPercentageInt()));
    }

    private StringTextComponent generateMessage(int mana){
        int manaPercent = getManaPercentageInt();
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

    public int getManaPercentageInt(){
        return (int) (manaPercent * 100);
    }

    public float getManaPercentage(){
        return manaPercent;
    }


    private void recalculateMana(int mana){
        manaPercent = (float) mana/ 10000f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        //perform every 10 seconds
        super.inventoryTick(stack,worldIn,entityIn,itemSlot,isSelected);

        //Only check every 10 seconds
        if(entityIn instanceof  PlayerEntity && System.currentTimeMillis() - lastUpdateTime >= UPDATE_FREQ_MILLIS){
            PlayerEntity playerIn = (PlayerEntity) entityIn;

            playerIn.getCapability(ManaProvider.MANA_CAP).ifPresent(m -> {
                if(!worldIn.isRemote()){
                    recalculateMana(m.getMana());
                    lastUpdateTime = System.currentTimeMillis();
                    System.out.println("Mana%: " + getManaPercentageInt());
                }
            });
        }
    }
}
