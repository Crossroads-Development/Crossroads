package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.beams.BeamUnit;
import com.Da_Technomancer.crossroads.api.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class BeamReflectorTileEntity extends BeamRenderTE{

	public static final BlockEntityType<BeamReflectorTileEntity> TYPE = CRTileEntity.createType(BeamReflectorTileEntity::new, CRBlocks.beamReflector);

	public BeamReflectorTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		facing = -1;
	}

	private int getFacing(){
		if(facing == -1){
			BlockState s = getBlockState();
			if(s.hasProperty(CRProperties.FACING)){
				facing = s.getValue(CRProperties.FACING).get3DDataValue();
			}else{
				return 0;
			}
		}

		return facing;
	}

	private int facing = -1;
	
	@Override
	protected void doEmit(BeamUnit toEmit){
		if(beamer[facing].emit(toEmit, level)){
			refreshBeam(facing);
		}
		if(!toEmit.isEmpty()){
			prevMag[facing] = toEmit;
		}
	}

	@Override
	protected boolean[] inputSides(){
		boolean[] out = {true, true, true, true, true, true};
		out[getFacing()] = false;
		return out;
	}

	@Override
	protected boolean[] outputSides(){
		boolean[] out = {false, false, false, false, false, false};
		out[getFacing()] = true;
		return out;
	}
} 
