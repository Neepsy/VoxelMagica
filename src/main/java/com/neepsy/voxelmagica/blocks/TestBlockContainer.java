package com.neepsy.voxelmagica.blocks;

import com.neepsy.voxelmagica.util.CustomEnergy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nullable;

public class TestBlockContainer extends Container {


    private TileEntity TE;
    private PlayerEntity player;
    private IItemHandler playerInv;

    public TestBlockContainer(int id, World world, BlockPos pos, PlayerInventory inv){
        super(ModBlocks.TESTBLOCKCONTAINER, id);
        TE = world.getTileEntity(pos);
        TE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        player = inv.player;
        playerInv = new InvWrapper(inv);

        TE.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h ->{
            addSlot(h, 0, 80, 25);
        });
        layoutPlayerInventorySlots(8, 84);

        trackInt(new IntReferenceHolder() {
            @Override
            public int get() {
                return getEnergy();
            }

            @Override
            public void set(int value) {
                TE.getCapability(CapabilityEnergy.ENERGY).ifPresent(h -> ((CustomEnergy)h).setEnergy(value));
            }
        });
    }

    public int getEnergy(){
        return TE.getCapability(CapabilityEnergy.ENERGY).map(IEnergyStorage::getEnergyStored).orElse(0);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            ItemStack stack = slot.getStack();
            itemstack = stack.copy();
            if (index == 0) {
                if (!this.mergeItemStack(stack, 1, 37, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(stack, itemstack);
            } else {
                if (stack.getItem() == Items.COAL) {
                    if (!this.mergeItemStack(stack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 28) {
                    if (!this.mergeItemStack(stack, 28, 37, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index < 37 && !this.mergeItemStack(stack, 1, 28, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (stack.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, stack);
        }

        return itemstack;
    }


    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return isWithinUsableDistance(IWorldPosCallable.of(TE.getWorld(), TE.getPos()), player, ModBlocks.TESTBLOCK);
    }

    public void addSlot(IItemHandler handler, int index, int x, int y){
        addSlot(new SlotItemHandler(handler, index, x, y));
    }

    private int addSlotRange(IItemHandler handler, int index, int x, int y, int amount, int deltaX){
        for(int i = 0; i < amount; i++){
            addSlot(handler, index, x, y);
            x += deltaX;
            index++;
        }
        return index;
    }

    private int addSlotBox(IItemHandler handler, int index, int x, int y, int deltaX, int deltaY, int xAmount, int yAmount){
        for(int i = 0; i < yAmount; i++){
            index = addSlotRange(handler, index, x, y, xAmount, deltaX);
            y += deltaY;
        }
        return index;
    }

    private void layoutPlayerInventorySlots(int leftCol, int topRow){
        addSlotBox(playerInv, 9, leftCol, topRow, 18, 18, 9, 3);
        topRow += 58;
        addSlotRange(playerInv, 0, leftCol, topRow, 9, 18);
    }


}
