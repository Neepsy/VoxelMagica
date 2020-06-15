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
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class ShockboltProjectileEntity extends ProjectileItemEntity {
    private int lifetimeTicks;
    public static RedstoneParticleData particles = new RedstoneParticleData(.55f,.16f,.73f,1f);
    private static float damage = Config.SHOCKBOLT_DMG.get().floatValue();

    public ShockboltProjectileEntity(World worldIn){
        super(ModEntities.SHOCKBOLTPROJECTILE, worldIn);
        init();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE;
    }

    public ShockboltProjectileEntity(PlayerEntity playerIn, World worldIn){
        super(ModEntities.SHOCKBOLTPROJECTILE, playerIn, worldIn);
        init();
    }

    private void init(){
        lifetimeTicks = 72;
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
            world.addParticle(ParticleTypes.EXPLOSION,getPosXRandom(.02), getPosY(), getPosZRandom(.02), 0,0,0);
        }
        if(result.getType() == RayTraceResult.Type.ENTITY){
            Entity hit = ((EntityRayTraceResult)result).getEntity();

            hit.attackEntityFrom(DamageSource.causeIndirectDamage(this,getThrower()), damage / 2);
            //reset iframes to deal magic part of damage;
            hit.hurtResistantTime = 0;
            hit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this,getThrower()), damage / 2);
        }
        this.remove();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}
