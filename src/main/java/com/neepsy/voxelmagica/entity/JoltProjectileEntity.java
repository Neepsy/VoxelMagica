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
import net.minecraft.world.World;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.Random;

public class JoltProjectileEntity extends ProjectileItemEntity {
    private int lifetimeTicks;
    public static RedstoneParticleData particles = new RedstoneParticleData(.7f,.1f,.1f,1f);
    private static float damage = Config.JOLT_DMG.get().floatValue();
    private static Random rand = new Random();

    public JoltProjectileEntity(World worldIn){
        super(ModEntities.JOLTPROJECTILE, worldIn);
        init();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE;
    }

    public JoltProjectileEntity(PlayerEntity playerIn, World worldIn){
        super(ModEntities.JOLTPROJECTILE, playerIn, worldIn);
        init();
    }

    private void init(){
        lifetimeTicks = 100;
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
            Vec3d vec3d = this.getMotion();
            double d3 = vec3d.x;
            double d4 = vec3d.y;
            double d0 = vec3d.z;
            for(int i = 0; i < 4; ++i) {
                this.world.addParticle(particles, this.getPosX() + d3 * (double)i / 4.0D, this.getPosY() + d4 * (double)i / 4.0D, this.getPosZ() + d0 * (double)i / 4.0D, -d3, -d4 + 0.2D, -d0);
            }
        }

    }

    @Override
    protected void onImpact(RayTraceResult result) {

        if(world.isRemote()){
            for(int i = 0; i<20;i++){
                world.addParticle(ParticleTypes.FIREWORK,getPosXRandom(.02), getPosY(), getPosZRandom(.02), rand.nextFloat() * (-.3) + .15 ,rand.nextFloat() * (-.3) + .15,rand.nextFloat() * (-.3) + .15);
            }
        }
        else{
            world.playSound(null,getPosX(),getPosY(),getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_BLAST, SoundCategory.PLAYERS,.8f,1.2f);

        }
        if(result.getType() == RayTraceResult.Type.ENTITY){
            Entity hit = ((EntityRayTraceResult)result).getEntity();

            hit.attackEntityFrom(DamageSource.causeIndirectDamage(this,getThrower()), damage /2);
            //reset iframes to deal magic part of damage;
            hit.hurtResistantTime = 0;
            hit.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this,getThrower()), damage /2);
        }


        this.remove();
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

}
