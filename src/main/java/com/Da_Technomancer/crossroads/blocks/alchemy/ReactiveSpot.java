package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.ReactiveSpotTileEntity;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ReactiveSpot extends BlockContainer{

	public ReactiveSpot(){
		super(Material.SPONGE);
		String name = "reactive_spot";
		setUnlocalizedName(name);
		setRegistryName(name);
		setHardness(0);
		ModBlocks.toRegister.add(this);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ReactiveSpotTileEntity();
	}

	@Override
	public boolean isCollidable(){
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state){
		return false;
	}

	@Override
	public boolean isFullCube(IBlockState state){
		return false;
	}
}
