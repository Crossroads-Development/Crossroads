package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamRedirectorTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_redirector")
	public static BlockEntityType<BeamRedirectorTileEntity> type = null;

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
			dir = state.getValue(ESProperties.FACING);
		}
		return dir;
	}

	@Override
	public void clearCache(){
		dir = null;
		super.clearCache();
	}

	@Override
	protected void doEmit(BeamUnit out){
		Direction facing = getDir();
		boolean reds = getBlockState().getValue(CRProperties.REDSTONE_BOOL);//Store this before emitting, as the redstone field can be modified during execution
		if(beamer[facing.get3DDataValue()].emit(reds ? out : BeamUnit.EMPTY, level)){
			refreshBeam(facing.get3DDataValue());
		}
		if(beamer[facing.getOpposite().get3DDataValue()].emit(reds ? BeamUnit.EMPTY : out, level)){
			refreshBeam(facing.getOpposite().get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] input = new boolean[] {true, true, true, true, true, true};
		Direction facing = getDir();
		input[facing.get3DDataValue()] = false;
		input[facing.getOpposite().get3DDataValue()] = false;
		return input;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] output = new boolean[6];
		Direction facing = getDir();
		output[facing.get3DDataValue()] = true;
		output[facing.getOpposite().get3DDataValue()] = true;
		return output;
	}
} 
