package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamRedirectorTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BeamRedirector extends BeamBlock{

	public BeamRedirector(){
		super("beam_redirector");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new BeamRedirectorTileEntity();
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		neighborChanged(state, world, pos, this, pos);
	}
	
	@Override
	public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos){
		boolean hasRedstone = worldIn.getRedstonePower(pos.east(), EnumFacing.EAST) != 0 || worldIn.getRedstonePower(pos.west(), EnumFacing.WEST) != 0 || worldIn.getRedstonePower(pos.north(), EnumFacing.NORTH) != 0 || worldIn.getRedstonePower(pos.south(), EnumFacing.SOUTH) != 0 || worldIn.getRedstonePower(pos.down(), EnumFacing.DOWN) != 0 || worldIn.getRedstonePower(pos.up(), EnumFacing.UP) != 0;
		((BeamRedirectorTileEntity) worldIn.getTileEntity(pos)).setRedstone(hasRedstone);
	}
}
