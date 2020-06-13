package com.neepsy.voxelmagica.entity;

import com.neepsy.voxelmagica.VoxelMagica;
import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class InfuseProjectileRenderer extends ArrowRenderer<InfuseProjectileEntity> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(VoxelMagica.MODID, "textures/entity/spells/infuseprojectile.png");

    public InfuseProjectileRenderer(EntityRendererManager manager){
        super(manager);
        System.out.println(TEXTURE);
    }

    @Override
    public ResourceLocation getEntityTexture(InfuseProjectileEntity entity) {
        return TEXTURE;
    }
}
