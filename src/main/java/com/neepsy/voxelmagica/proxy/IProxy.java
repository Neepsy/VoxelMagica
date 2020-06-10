package com.neepsy.voxelmagica.proxy;

import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;

public interface IProxy {
    void init();

    World getClientWorld();

    void onModelBake(ModelBakeEvent e);
}
