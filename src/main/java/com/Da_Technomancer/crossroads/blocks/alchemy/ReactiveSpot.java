package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

public class ReactiveSpot extends ContainerBlock{

	public ReactiveSpot(){
		super(Properties.create(Material.SPONGE).hardnessAndResistance(0).doesNotBlockMovement().noDrops());
		String name = "reactive_spot";
		setRegistryName(name);
		CrossroadsBlocks.toRegister.add(this);
		//No item form
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new ReactiveSpotTileEntity();
	}

	@Override
	public VoxelShape getRaytraceShape(BlockState state, IBlockReader worldIn, BlockPos pos){
		return VoxelShapes.empty();
	}
}
