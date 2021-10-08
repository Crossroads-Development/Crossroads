package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;

import net.minecraft.world.level.block.state.BlockBehaviour.Properties;

public class ReactiveSpot extends BaseEntityBlock{

	public ReactiveSpot(){
		super(Properties.of(Material.SPONGE).strength(0).noCollission().noDrops());
		String name = "reactive_spot";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		//No item form
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new ReactiveSpotTileEntity();
	}

	@Override
	public VoxelShape getInteractionShape(BlockState state, BlockGetter worldIn, BlockPos pos){
		return Shapes.empty();
	}
}
