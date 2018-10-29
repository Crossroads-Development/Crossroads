package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MechanicalBeamSplitterTileEntity extends BeamRenderTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}
	
	@Override
	protected void doEmit(MagicUnit out){
		EnumFacing facing = world.getBlockState(pos).getValue(EssentialsProperties.FACING);
		TileEntity te = world.getTileEntity(pos.offset(facing));
		double splitRatio = te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()) ? Math.min(Math.abs(te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).getMotionData()[0]), 15D) / 15D : 0;
		MagicUnit outMult = out == null ? null : new MagicUnit((int) Math.round(splitRatio * (double) out.getEnergy()), (int) Math.round(splitRatio * (double) out.getPotential()), (int) Math.round(splitRatio * (double) out.getStability()), (int) Math.round(splitRatio * (double) out.getVoid()));
		if(outMult == null || outMult.getPower() == 0){
			outMult = null;
		}
		if(out != null && outMult != null){
			out = new MagicUnit(out.getEnergy() - outMult.getEnergy(), out.getPotential() - outMult.getPotential(), out.getStability() - outMult.getStability(), out.getVoid() - outMult.getVoid());
			if(out.getPower() == 0){
				out = null;
			}
		}
		beamer[0].emit(outMult, world);
		beamer[1].emit(out, world);
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {true, true, false, false, false, false};
	}
}
