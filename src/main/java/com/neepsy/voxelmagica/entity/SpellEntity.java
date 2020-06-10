package com.neepsy.voxelmagica.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public abstract class SpellEntity extends DamagingProjectileEntity {

    public SpellEntity(EntityType<? extends DamagingProjectileEntity> type, World worldIn){
        super(type, worldIn);
        this.setNoGravity(true);
    }

    public SpellEntity(EntityType<? extends DamagingProjectileEntity> type, PlayerEntity player, World world){
        super(type, player,0, 0,0 , world);
    }

    //Shoot method taken from vanilla ThrowableEntity
    public void shoot(Entity entityShooter, float rotationPitchIn, float rotationYawIn, float pitchOffset, float velocity) {
        float f = -MathHelper.sin(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((rotationPitchIn + pitchOffset) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(rotationYawIn * ((float)Math.PI / 180F)) * MathHelper.cos(rotationPitchIn * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity);
        Vec3d vec3d = entityShooter.getMotion();
        this.setMotion(this.getMotion().add(vec3d.x, entityShooter.onGround ? 0.0D : vec3d.y, vec3d.z));
    }


    public void shoot(double x, double y, double z, float velocity) {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize();
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }
}
