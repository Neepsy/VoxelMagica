package com.neepsy.voxelmagica.util;

public interface IMana {
    public void consume(int amount);
    public void add(int amount);
    public void setMana(int amount);
    public int getMana();
}

