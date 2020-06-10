package com.neepsy.voxelmagica.util;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;


public class ManaProvider implements ICapabilitySerializable<INBT> {
    @CapabilityInject(IMana.class)
    public static final Capability<IMana> MANA_CAP = null;

    private LazyOptional<IMana> instance = LazyOptional.of(MANA_CAP::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == MANA_CAP ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return MANA_CAP.getStorage().writeNBT(MANA_CAP, instance.orElseThrow( () -> new NullPointerException("Lazy Optional was empty")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        MANA_CAP.getStorage().readNBT(MANA_CAP, instance.orElseThrow( () -> new NullPointerException("Lazy Optional was empty")), null, nbt);
    }
}
