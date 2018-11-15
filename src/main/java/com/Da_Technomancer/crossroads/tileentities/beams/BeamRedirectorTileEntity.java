package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public class BeamRedirectorTileEntity extends BeamRenderTE{

	private boolean redstone;
	private EnumFacing dir = null;

	private EnumFacing getDir(){
		if(dir == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.beamRedirector){
				return EnumFacing.NORTH;
			}
			dir = state.getValue(EssentialsProperties.FACING);
		}
		return dir;
	}

	public void setRedstone(boolean redstone){
		if(this.redstone != redstone){
			this.redstone = redstone;
			markDirty();
		}
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getBoolean("reds");
	}

	@Override
	protected void doEmit(BeamUnit out){
		EnumFacing facing = getDir();
		if(beamer[facing.getIndex()].emit(redstone ? out : null, world)){
			refreshBeam(facing.getIndex());
		}
		if(beamer[facing.getOpposite().getIndex()].emit(redstone ? null : out, world)){
			refreshBeam(facing.getOpposite().getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] input = new boolean[] {true, true, true, true, true, true};
		EnumFacing facing = getDir();
		input[facing.getIndex()] = false;
		input[facing.getOpposite().getIndex()] = false;
		return input;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] output = new boolean[6];
		EnumFacing facing = getDir();
		output[facing.getIndex()] = true;
		output[facing.getOpposite().getIndex()] = true;
		return output;
	}
} 
