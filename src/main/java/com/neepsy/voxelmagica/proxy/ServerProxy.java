package com.neepsy.voxelmagica.proxy;

import com.neepsy.voxelmagica.util.CapabilityHandler;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.common.MinecraftForge;

public class ServerProxy implements IProxy {
    public void init(){

    }

    @Override
    public World getClientWorld() {
        throw new IllegalStateException("This should not run server side!");
    }

    @Override
    public void onModelBake(ModelBakeEvent e) {
        //nothing to do on server side
    }
}
