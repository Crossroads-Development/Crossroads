package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.CrystalMasterAxisTileEntity;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ClockworkStabilizerTileEntity;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ClockworkStabilizer extends BeamBlock{

	public ClockworkStabilizer(){
		super("clock_stabilizer");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ClockworkStabilizerTileEntity();
	}

	@Override
	public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face){
		return BlockFaceShape.UNDEFINED;
	}

	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos){
		TileEntity te = worldIn.getTileEntity(pos);
		return te instanceof ClockworkStabilizerTileEntity ? ((ClockworkStabilizerTileEntity) te).getRedstone() : 0;
	}

	@Override
	public boolean hasComparatorInputOverride(IBlockState state){
		return true;
	}
}
