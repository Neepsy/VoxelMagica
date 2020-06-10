package com.neepsy.voxelmagica.util;

public class Mana implements IMana{

    private int manaAmount;
    private int capacity;

    public Mana(){
        manaAmount = 10000;
        capacity = 10000;
    }

    @Override
    public void consume(int amountToConsume) {
        manaAmount = Math.max(0, manaAmount - amountToConsume);
    }

    @Override
    public void add(int amountToAdd) {
        manaAmount = Math.min(capacity, manaAmount + amountToAdd);
    }

    @Override
    public void setMana(int amount) {
        manaAmount = Math.min(capacity, amount);
    }

    @Override
    public int getMana() {
        return manaAmount;
    }
}
