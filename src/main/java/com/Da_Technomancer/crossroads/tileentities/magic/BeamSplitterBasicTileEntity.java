package com.Da_Technomancer.crossroads.tileentities.magic;

import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;

public class BeamSplitterBasicTileEntity extends BeamRenderTE{

	@Override
	protected void doEmit(MagicUnit out){
		MagicUnit outMult = out == null ? null : out.mult(.5D, false);
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
