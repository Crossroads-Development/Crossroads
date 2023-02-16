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

public class CrystallinePrismTileEntity extends BeamRenderTE{

	public static final BlockEntityType<CrystallinePrismTileEntity> TYPE = CRTileEntity.createType(CrystallinePrismTileEntity::new, CRBlocks.crystallinePrism);

	private Direction dir = null;

	public CrystallinePrismTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	private Direction getDir(){
		if(dir == null){
			BlockState state = getBlockState();
			if(state.getBlock() != CRBlocks.crystallinePrism){
				return Direction.NORTH;
			}
			dir = state.getValue(CRProperties.HORIZ_FACING);
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
		Direction dir = getDir();
		BeamHelper[] beamers = getBeamHelpers();
		//Energy
		if(beamers[dir.get3DDataValue()].emit(out.mult(1, 0, 0, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Potential
		if(beamers[dir.get3DDataValue()].emit(out.mult(0, 1, 0, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Stability
		if(beamers[dir.get3DDataValue()].emit(out.mult(0, 0, 1, 0, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
		dir = dir.getClockWise();
		//Void
		if(beamers[dir.get3DDataValue()].emit(out.mult(0, 0, 0, 1, false), level)){
			refreshBeam(dir.get3DDataValue());
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {true, true, false, false, false, false};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}
} 
