package com.neepsy.voxelmagica.util;

import com.neepsy.voxelmagica.VoxelMagica;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class CapabilityHandler {
    @SubscribeEvent
    public void attachCapability(AttachCapabilitiesEvent<Entity> e){
        if(e.getObject() instanceof PlayerEntity){
            e.addCapability(new ResourceLocation(VoxelMagica.MODID,"mana"), new ManaProvider());
            System.out.println("Added mana to player");
        }
    }

    @SubscribeEvent
    public void onLogIn(PlayerEvent.PlayerLoggedInEvent e){
        e.getPlayer().getCapability(ManaProvider.MANA_CAP).ifPresent(m ->{
            String msg = "You have " + m.getMana() + " mana.";
            e.getPlayer().sendMessage(new StringTextComponent(msg));
        });
    }
}
