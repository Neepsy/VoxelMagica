package com.neepsy.voxelmagica.util;

import net.minecraftforge.energy.EnergyStorage;

public class CustomEnergy extends EnergyStorage {
    public CustomEnergy(int cap, int maxXFer){
        super(cap, maxXFer);
    }

    public void setEnergy(int i){
        energy = i;
    }
    public void addEnergy(int i) { energy = Math.min(capacity, energy + i);}
}
