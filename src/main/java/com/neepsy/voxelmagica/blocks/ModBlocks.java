package com.neepsy.voxelmagica.blocks;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

public class ModBlocks {
    @ObjectHolder("voxelmagica:testblock")
    public static final TestBlock TESTBLOCK = null;

    @ObjectHolder("voxelmagica:testblock")
    public static final TileEntityType<TestBlockTile> TESTBLOCKTILE = null;

    @ObjectHolder("voxelmagica:testblock")
    public static final ContainerType<TestBlockContainer> TESTBLOCKCONTAINER = null;
}
