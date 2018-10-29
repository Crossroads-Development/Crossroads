package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

import net.minecraft.nbt.NBTTagCompound;

public class BeamSplitterTileEntity extends BeamRenderTE{

	private int redstone;

	public void setRedstone(int redstone){
		if(this.redstone != redstone){
			this.redstone = redstone;
			markDirty();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
	}

	@Override
	protected void doEmit(MagicUnit out){
		MagicUnit outMult = out == null ? null : out.mult(((double) redstone) / 15D, false);
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
