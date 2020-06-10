package com.neepsy.voxelmagica.blocks;

import com.neepsy.voxelmagica.util.CustomEnergy;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import com.neepsy.voxelmagica.util.Config;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TestBlockTile extends TileEntity implements ITickableTileEntity, INamedContainerProvider {


    private LazyOptional<ItemStackHandler> handler = LazyOptional.of(this::createHandler);
    private LazyOptional<EnergyStorage> energy = LazyOptional.of(this::createEnergy);

    private int ticksPerItem = Config.TESTBLOCK_BURNTIME.get();
    private int energyPerTick = Config.TESTBLOCK_GENERATION.get();
    private int ticksLeft = 0;
    private int maxOut = Config.TESTBLOCK_XFER.get();

    @Override
    public void tick() {
        if(world.isRemote){
            return;
        }

        if(ticksLeft > 0){
            ticksLeft--;
            energy.ifPresent(e -> {
                if(e.getEnergyStored() < e.getMaxEnergyStored()){
                    ((CustomEnergy)e).addEnergy(energyPerTick);
                    markDirty();
                }
            });

        }
        else{
            handler.ifPresent(h -> {
                ItemStack stack = h.getStackInSlot(0);
                if(stack.getItem() == Items.COAL){
                    h.extractItem(0,1,false);
                    ticksLeft = ticksPerItem;
                    world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, true), 3);
                }
                else{
                    world.setBlockState(pos, getBlockState().with(BlockStateProperties.LIT, false), 3);
                }
            });
        }


        pushPower();
    }

    private void pushPower() {
        energy.ifPresent(energy -> {

                    for (Direction dir : Direction.values()) {
                        TileEntity neighbor = world.getTileEntity(pos.offset(dir));
                        if (neighbor != null) {
                            neighbor.getCapability(CapabilityEnergy.ENERGY, dir).ifPresent(e ->
                            {
                                if (e.canReceive()) {
                                    int sent = e.receiveEnergy(Math.min(energy.getEnergyStored(), maxOut), false);
                                    energy.extractEnergy(sent, false);
                                    markDirty();
                                }
                            });
                        }
                    }
                }
        );
    }

    private ItemStackHandler createHandler(){
            return new ItemStackHandler(1){
                @Override
                public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                    return (stack.getItem() == Items.COAL);
                }

                @Nonnull
                @Override
                public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                    if(stack.getItem() != Items.COAL){
                        return stack;
                    }
                    return super.insertItem(slot, stack, simulate);
                }

                @Override
                protected void onContentsChanged(int slot) {
                    markDirty();
                }
            };
    }

    private EnergyStorage createEnergy(){
        return new CustomEnergy(Config.TESTBLOCK_CAPACITY.get(), maxOut);
    }


    public TestBlockTile(){
        super(ModBlocks.TESTBLOCKTILE);
    }



    @Override
    public void read(CompoundNBT compound) {
        CompoundNBT nbt = compound.getCompound("inv");
        handler.ifPresent(h -> ((INBTSerializable<CompoundNBT>)h).deserializeNBT(nbt));
        energy.ifPresent(e -> ((CustomEnergy)e).setEnergy(compound.getInt("energy")));
        createHandler().deserializeNBT(nbt);
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        handler.ifPresent(h -> {
            CompoundNBT nbt = ((INBTSerializable<CompoundNBT>)h).serializeNBT();
            compound.put("inv", nbt);
        });

        energy.ifPresent(e -> compound.putInt("energy", e.getEnergyStored()));

        return super.write(compound);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return handler.cast();
        }

        if(cap == CapabilityEnergy.ENERGY){
            return energy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Test Generator");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory inv, PlayerEntity player) {
        return new TestBlockContainer(id, world, pos, inv);
    }


}
