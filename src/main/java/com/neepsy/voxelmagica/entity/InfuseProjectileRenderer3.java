package com.neepsy.voxelmagica.entity;

import com.neepsy.voxelmagica.VoxelMagica;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;

public class InfuseProjectileRenderer3 extends ArrowRenderer<InfuseProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(VoxelMagica.MODID, "textures/entity/spells/infuseprojectile.png");

    public InfuseProjectileRenderer3(EntityRendererManager manager){
        super(manager);
        System.out.println(TEXTURE);
    }

    @Override
    public ResourceLocation getEntityTexture(InfuseProjectileEntity entity) {
        return TEXTURE;
    }
}
