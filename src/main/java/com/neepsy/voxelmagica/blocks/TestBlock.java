package com.neepsy.voxelmagica.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class TestBlock extends Block {
    public TestBlock(){
        super(Block.Properties.create(Material.WOOD).sound(SoundType.WOOD).hardnessAndResistance(1.0f));
        setRegistryName("testblock");
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        return state.get(BlockStateProperties.LIT) ? 14 : 0;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if(placer != null){
            state = state.with(BlockStateProperties.LIT, false);
            state = state.with(BlockStateProperties.FACING, getFacingFromEntity(pos, placer));
            worldIn.setBlockState(pos, state, 2);


        }
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if(!worldIn.isRemote){
            TileEntity TE = worldIn.getTileEntity(pos);
            if(TE instanceof INamedContainerProvider){
                NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) TE, TE.getPos());
            }
            else{
                throw new IllegalStateException("Container is missing!");
            }
            return ActionResultType.SUCCESS;

        }

        return ActionResultType.SUCCESS;
    }

    public static Direction getFacingFromEntity(BlockPos pos, LivingEntity placer){
        return Direction.getFacingFromVector((float) placer.getPosX()- pos.getX(), (float) placer.getPosY()- pos.getY(), (float) placer.getPosZ()- pos.getZ());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.FACING, BlockStateProperties.LIT);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TestBlockTile();
    }
}
