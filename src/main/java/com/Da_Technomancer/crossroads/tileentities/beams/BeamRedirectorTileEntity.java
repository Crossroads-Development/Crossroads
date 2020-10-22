package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamRedirectorTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_redirector")
	public static TileEntityType<BeamRedirectorTileEntity> type = null;

	private Direction dir = null;

	public BeamRedirectorTileEntity(){
		super(type);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.beamRedirector){
				return Direction.NORTH;
			}
			dir = state.get(ESProperties.FACING);
		}
		return dir;
	}

	@Override
	public void updateContainingBlockInfo(){
		Direction prev = dir;
		dir = null;
		if(prev != getDir()){
			//It's a waste to regenerate the beamers if it was only a redstone signal changing
			super.updateContainingBlockInfo();
		}
	}

	@Override
	protected void doEmit(BeamUnit out){
		Direction facing = getDir();
		boolean reds = getBlockState().get(CRProperties.REDSTONE_BOOL);//Store this before emitting, as the redstone field can be modified during execution
		if(beamer[facing.getIndex()].emit(reds ? out : BeamUnit.EMPTY, world)){
			refreshBeam(facing.getIndex());
		}
		if(beamer[facing.getOpposite().getIndex()].emit(reds ? BeamUnit.EMPTY : out, world)){
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
