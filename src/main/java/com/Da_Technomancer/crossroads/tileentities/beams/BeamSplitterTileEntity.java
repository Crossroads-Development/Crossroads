package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;

public class BeamSplitterTileEntity extends BeamRenderTE{

	private int redstone = 0;
	private Direction dir = null;

	private Direction getDir(){
		if(dir == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.beamSplitter){
				return Direction.NORTH;
			}
			dir = state.get(EssentialsProperties.FACING);
		}
		return dir;
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		dir = null;
	}

	public void setRedstone(int redstone){
		if(this.redstone != redstone){
			this.redstone = redstone;
			markDirty();
		}
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
	}

	@Override
	protected void doEmit(BeamUnit out){
		//As it would turn out, the problem of meeting a quota for the sum of values drawn from a limited source while also approximately maintaining the source ratio is quite messy when all values must be integers
		//This is about as clean an implementation as is possible
		int toFill = out == null ? 0 : (int) Math.round(out.getPower() * Math.min(redstone, 12) / 12D);
		Direction facing = getDir();
		BeamUnit toDraw;
		BeamUnit remain;

		if(out == null || toFill == 0){
			if(beamer[facing.getIndex()].emit(null, world)){
				refreshBeam(facing.getIndex());
			}
			if(beamer[facing.getOpposite().getIndex()].emit(out, world)){
				refreshBeam(facing.getOpposite().getIndex());
			}
			return;
		}

		if(toFill < out.getPower()){
			int[] output = out.mult(((double) toFill) / ((double) out.getPower()), true).getValues();//Use the floor formula as a starting point
			int[] stored = out.getValues();
			int available = 0;

			for(int i = 0; i < 4; i++){
				stored[i] -= output[i];
				available += stored[i];
				toFill -= output[i];
			}

			toFill = Math.min(toFill, available);
			available -= toFill;

			int source = 0;

			//Round-robin distribution of drawing additional power from storage to meet the quota
			//Ignoring the source element ratio, as toFill << RATES[storage] in most cases, making the effect on ratio minor
			for(int i = 0; i < toFill; i++){
				while(stored[source] == 0){
					source++;
				}
				output[source]++;
				stored[source]--;
				source++;
			}
			toDraw = new BeamUnit(output);
			remain = available == 0 ? null : new BeamUnit(stored[0], stored[1], stored[2], stored[3]);
		}else{
			toDraw = out;
			remain = null;
		}


		if(beamer[facing.getIndex()].emit(toDraw, world)){
			refreshBeam(facing.getIndex());
		}
		if(beamer[facing.getOpposite().getIndex()].emit(remain, world)){
			refreshBeam(facing.getOpposite().getIndex());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] input = new boolean[] {true, true, true, true, true, true};
		Direction facing = getDir();
		input[facing.getIndex()] = false;
		input[facing.getOpposite().getIndex()] = false;
		return input;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] output = new boolean[6];
		Direction facing = getDir();
		output[facing.getIndex()] = true;
		output[facing.getOpposite().getIndex()] = true;
		return output;
	}
} 
