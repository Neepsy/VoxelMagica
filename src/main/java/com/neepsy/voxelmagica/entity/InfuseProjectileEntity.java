package com.neepsy.voxelmagica.entity;


import com.neepsy.voxelmagica.items.ModItems;
import com.neepsy.voxelmagica.util.Constants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.IPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;


import java.util.List;
import java.util.Map;

public class InfuseProjectileEntity extends AbstractArrowEntity {

    int lifetimeTicks;
    public InfuseProjectileEntity(World worldIn){
        super(ModEntities.INFUSEPROJECTILE, worldIn);
        lifetimeTicks = 50;
        pickupStatus = PickupStatus.DISALLOWED;
        setKnockbackStrength(1);
        setNoGravity(true);
    }

    public InfuseProjectileEntity(EntityType<AbstractArrowEntity> type, World worldIn){
        super(type, worldIn);
        lifetimeTicks = 50;
        pickupStatus = PickupStatus.DISALLOWED;
        setKnockbackStrength(1);
        setNoGravity(true);
    }

    public InfuseProjectileEntity(PlayerEntity player, World world){
        super(ModEntities.INFUSEPROJECTILE, player,world);
        lifetimeTicks = 50;
        pickupStatus = PickupStatus.DISALLOWED;
        setKnockbackStrength(1);
        setNoGravity(true);
    }

    @Override
    public void tick() {
        super.tick();
        lifetimeTicks--;
        if(lifetimeTicks <= 0 && !world.isRemote()){
            this.remove();
        }

        if(world.isRemote()){
            this.world.addParticle(ParticleTypes.CRIT, this.getPosX(), this.getPosY(), this.getPosZ(),0,0,0);
        }
    }

    @Override
    protected ItemStack getArrowStack() {
        //dummy item (The one used for the creative tab icon lol)
        return new ItemStack(ModItems.ICONITEM);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if(result.getType() == RayTraceResult.Type.ENTITY){
            Entity hit = ((EntityRayTraceResult)result).getEntity();
            hit.attackEntityFrom(DamageSource.causeArrowDamage(this,this.getShooter()), 1f);
        }
        if(world.isRemote){
            world.addParticle(ParticleTypes.CLOUD, getPosXRandom(.02), getPosY(), getPosZRandom(.02), 0,0,0);
        }

        if(!world.isRemote()){

            AxisAlignedBB bb = new AxisAlignedBB(this.getPositionVec().add(1,1,1), this.getPositionVec().add(-1,-1,-1));
            List<Entity> nearby = world.getEntitiesInAABBexcluding(this, bb, entity -> true );
            for(Entity e : nearby){

                if(e instanceof ItemEntity){
                    ItemEntity item = (ItemEntity) e;
                    Map<Item,Item> recipes =  Constants.getInstance().infusionRecipes;
                    for(Item i : recipes.keySet()){
                        if(item.getItem().getItem() == i){
                            ItemStack shrunk = item.getItem();
                            shrunk.shrink(1);
                            item.setItem(shrunk);

                            Item toCreate = Constants.getInstance().infusionRecipes.get(i);
                            System.out.println(toCreate);
                            world.addEntity(new ItemEntity(world, getPosX(), getPosY(), getPosZ(), new ItemStack(toCreate, 1)));
                            break;
                        }
                    }
                }
            }

            this.remove();
        }
    }
}
