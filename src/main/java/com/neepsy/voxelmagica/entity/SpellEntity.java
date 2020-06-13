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

    @Override
    protected boolean isFireballFiery() {
        return false;
    }

    //copied from AbstractArrowEntity
    public void shoot(Entity shooter, float pitch, float yaw, float p_184547_4_, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin(pitch * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(yaw * ((float)Math.PI / 180F)) * MathHelper.cos(pitch * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        this.setMotion(this.getMotion().add(shooter.getMotion().x, shooter.onGround ? 0.0D : shooter.getMotion().y, shooter.getMotion().z));
    }

    /**
     * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
     */
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vec3d vec3d = (new Vec3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
        this.setMotion(vec3d);
        float f = MathHelper.sqrt(horizontalMag(vec3d));
        this.rotationYaw = (float)(MathHelper.atan2(vec3d.x, vec3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vec3d.y, (double)f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }
}
