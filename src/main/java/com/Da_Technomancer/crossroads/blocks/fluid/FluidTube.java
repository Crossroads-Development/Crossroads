package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FluidTube extends ContainerBlock{

	private static final double SIZE = 5D / 16D;
	protected static final VoxelShape[] SHAPES = new VoxelShape[64];

	static{
		final double size = SIZE * 16D;
		final double sizeN = 16D - size;
		//There are 64 (2^6) possible states for this block, and each one has a different shape
		//This... is gonna take a while
		VoxelShape core = makeCuboidShape(size, size, size, sizeN, sizeN, sizeN);
		VoxelShape[] pieces = new VoxelShape[6];
		pieces[0] = makeCuboidShape(size, 0, size, sizeN, size, sizeN);
		pieces[1] = makeCuboidShape(size, 16, size, sizeN, sizeN, sizeN);
		pieces[2] = makeCuboidShape(size, size, 0, sizeN, sizeN, size);
		pieces[3] = makeCuboidShape(size, size, 16, sizeN, sizeN, sizeN);
		pieces[4] = makeCuboidShape(0, size, size, size, sizeN, sizeN);
		pieces[5] = makeCuboidShape(16, size, size, sizeN, sizeN, sizeN);
		for(int i = 0; i < 64; i++){
			VoxelShape comp = core;
			for(int j = 0; j < 6; j++){
				if((i & (1 << j)) != 0){
					comp = VoxelShapes.or(comp, pieces[j]);
				}
			}
			SHAPES[i] = comp;
		}
	}

	public FluidTube(){
		this("fluid_tube");
	}

	protected FluidTube(String name){
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2));
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
		setDefaultState(getDefaultState().with(CRProperties.CONDUIT_SIDES[0], EnumTransferMode.NONE).with(CRProperties.CONDUIT_SIDES[1], EnumTransferMode.NONE).with(CRProperties.CONDUIT_SIDES[2], EnumTransferMode.NONE).with(CRProperties.CONDUIT_SIDES[3], EnumTransferMode.NONE).with(CRProperties.CONDUIT_SIDES[4], EnumTransferMode.NONE).with(CRProperties.CONDUIT_SIDES[5], EnumTransferMode.NONE));
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(CRProperties.CONDUIT_SIDES);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		int index = 0;
		for(int i = 0; i < 6; i++){
			if(state.get(CRProperties.CONDUIT_SIDES[i]).isConnection()){
				index |= 1 << i;
			}
		}
		return SHAPES[index];
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidTubeTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(EssentialsConfig.isWrench(playerIn.getHeldItem(hand))){
			if(!worldIn.isRemote){
				int face;
				if(hit.getHitVec().y < SIZE){
					face = 0;//Down
				}else if(hit.getHitVec().y > 1F - (float) SIZE){
					face = 1;//Up
				}else if(hit.getHitVec().x < (float) SIZE){
					face = 4;//West
				}else if(hit.getHitVec().x > 1F - (float) SIZE){
					face = 5;//East
				}else if(hit.getHitVec().z < (float) SIZE){
					face = 2;//North
				}else if(hit.getHitVec().z > 1F - (float) SIZE){
					face = 3;//South
				}else{
					face = hit.getFace().getIndex();
				}
				TileEntity te = worldIn.getTileEntity(pos);
				if(te instanceof FluidTubeTileEntity){
					((FluidTubeTileEntity) te).toggleConfigure(face);
				}
			}
			return true;
		}
		return false;
	}
}
