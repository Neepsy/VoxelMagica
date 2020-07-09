package com.neepsy.voxelmagica.entity;

import com.neepsy.voxelmagica.entity.ModEntities;
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

import java.util.Random;

public class ScorchProjectileEntity extends ProjectileItemEntity {

    private int lifetimeTicks;
    public static RedstoneParticleData particles = new RedstoneParticleData(.8f,.05f,.05f,1f);
    private static float damage = Config.SCORCH_DMG.get().floatValue();
    private static Random rand = new Random();

    public ScorchProjectileEntity(World worldIn){
        super(ModEntities.SCORCHPROJECTILE, worldIn);
        init();
    }

    @Override
    protected Item getDefaultItem() {
        return Items.REDSTONE;
    }

    public ScorchProjectileEntity(PlayerEntity playerIn, World worldIn){
        super(ModEntities.SCORCHPROJECTILE, playerIn, worldIn);
        init();
    }

    private void init(){
        lifetimeTicks = 90;
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


    private float randPos(double center, float range){
        return ((float) center)+ (rand.nextFloat() * (range * -2) + range / 2);
    }

    protected void onImpact(RayTraceResult result) {


        if(world.isRemote()){
            for(int i = 0; i<35;i++){
                world.addParticle(particles,randPos(getPosX(), .9f), randPos(getPosY(), .9f), randPos(getPosZ(), .9f), 0 ,0,0);
            }
            world.addParticle(ParticleTypes.FLASH,getPosX(),getPosY(),getPosZ(),0,0,0);

        }
        else{
            world.playSound(null,getPosX(),getPosY(),getPosZ(), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.PLAYERS,.5f,1.2f);
            world.playSound(null,getPosX(),getPosY(),getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_TWINKLE, SoundCategory.PLAYERS,.7f,1.1f);
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
