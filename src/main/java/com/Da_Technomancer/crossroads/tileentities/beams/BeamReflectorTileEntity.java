package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamReflectorTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_reflector")
	public static BlockEntityType<BeamReflectorTileEntity> TYPE = null;

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
			if(s.hasProperty(ESProperties.FACING)){
				facing = s.getValue(ESProperties.FACING).get3DDataValue();
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
