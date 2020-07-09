package com.neepsy.voxelmagica.entity;

import com.neepsy.voxelmagica.util.Config;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class AeroblastProjectileEntity extends ProjectileItemEntity {
    private int lifetimeTicks;
    public static RedstoneParticleData particles = new RedstoneParticleData(.32f,.85f,.32f,1f);
    private static float damage = Config.SHOCKBOLT_DMG.get().floatValue();
    private static int knockbackStrength = 1;

    public AeroblastProjectileEntity(World worldIn){
        super(ModEntities.AEROBLASTPROJECTILE, worldIn);
        init();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE;
    }

    public AeroblastProjectileEntity(PlayerEntity playerIn, World worldIn){
        super(ModEntities.AEROBLASTPROJECTILE, playerIn, worldIn);
        init();
    }

    private void init(){
        lifetimeTicks = 125;
        setNoGravity(true);
    }



    @Override
    public void tick() {
        super.tick();
        lifetimeTicks--;
        if(lifetimeTicks <= 0 && !world.isRemote()){
            this.remove();
        }

        if(world.isRemote){
            world.addParticle(particles,getPosX(),getPosY(),getPosZ(),0,0,0);
        }

    }

    @Override
    protected void onImpact(RayTraceResult result) {
        if(world.isRemote()){
            for(int i = 0; i < 25; i++){
                world.addParticle(ParticleTypes.ENTITY_EFFECT,getPosXRandom(.2), getPosY(), getPosZRandom(.2), .1f,.8f,.1f);
            }

        }else{
            world.playSound(null,getPosX(),getPosY(),getPosZ(), SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS,.6f,1f);
        }
        if(result.getType() == RayTraceResult.Type.ENTITY){
            Entity hit = ((EntityRayTraceResult)result).getEntity();

            hit.attackEntityFrom(DamageSource.causeIndirectDamage(this,getThrower()), damage / 2);
            //reset iframes to deal magic part of damage;
            hit.hurtResistantTime = 0;
            hit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this,getThrower()), damage / 2);

            Vec3d vec3d = this.getMotion().mul(0.4D, 0.0D, .4D).normalize().scale((double)this.knockbackStrength * 0.2D);
            if (vec3d.lengthSquared() > 0.0D) {
                hit.addVelocity(vec3d.x, 0.1D, vec3d.z);
            }
        }
        this.remove();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
