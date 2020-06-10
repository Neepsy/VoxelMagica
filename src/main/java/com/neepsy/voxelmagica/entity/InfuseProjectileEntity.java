package com.neepsy.voxelmagica.entity;

import com.neepsy.voxelmagica.items.ModItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.entity.projectile.DamagingProjectileEntity;
import net.minecraft.entity.projectile.ProjectileItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class InfuseProjectileEntity extends AbstractArrowEntity {

    int lifetimeTicks;
    public InfuseProjectileEntity(EntityType<AbstractArrowEntity> type, World worldIn){
        super(type, worldIn);
        lifetimeTicks = 50;

        this.setNoGravity(true);
    }

    public InfuseProjectileEntity(PlayerEntity player, World world){
        super(ModEntities.INFUSEPROJECTILE, player,world);
        lifetimeTicks = 50;
        this.pickupStatus = PickupStatus.DISALLOWED;
        this.setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        lifetimeTicks--;
        if(lifetimeTicks <= 0){
            this.remove();
        }

        if(world.isRemote()){
            this.world.addParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(),0,0,0);
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        return new ItemStack(ModItems.ICONITEM);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        IPacket p = NetworkHooks.getEntitySpawningPacket(this);
        System.out.println(p);
        return p;
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if(result.getType() == RayTraceResult.Type.ENTITY){
            Entity hit = ((EntityRayTraceResult)result).getEntity();
            hit.attackEntityFrom(DamageSource.causeArrowDamage(this,this.getShooter()), .2f);
        }

        if(!world.isRemote()){
            this.remove();
        }
    }
}
