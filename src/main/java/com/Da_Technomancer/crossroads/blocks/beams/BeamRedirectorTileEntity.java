package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.beams.BeamHelper;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BeamRedirectorTileEntity extends BeamRenderTE{

	public static final BlockEntityType<BeamRedirectorTileEntity> TYPE = CRTileEntity.createType(BeamRedirectorTileEntity::new, CRBlocks.beamRedirector);

	private Direction dir = null;

	public BeamRedirectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.beamRedirector){
				return Direction.NORTH;
			}
			dir = state.getValue(CRProperties.FACING);
		}
		return dir;
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		dir = null;
	}

	@Override
	protected void doEmit(BeamUnit out){
		Direction facing = getDir();
		boolean reds = getBlockState().getValue(CRProperties.REDSTONE_BOOL);//Store this before emitting, as the redstone field can be modified during execution
		BeamHelper[] beamers = getBeamHelpers();
		if(beamers[facing.get3DDataValue()].emit(reds ? out : BeamUnit.EMPTY, level)){
			refreshBeam(facing.get3DDataValue());
		}
		if(beamers[facing.getOpposite().get3DDataValue()].emit(reds ? BeamUnit.EMPTY : out, level)){
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
