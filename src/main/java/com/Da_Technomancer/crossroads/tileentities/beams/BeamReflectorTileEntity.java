package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class BeamReflectorTileEntity extends BeamRenderTE{

	@ObjectHolder("beam_reflector")
	private static TileEntityType<BeamReflectorTileEntity> type = null;

	public BeamReflectorTileEntity(){
		super(type);
	}

	@Override
	public void resetBeamer(){
		super.resetBeamer();
		facing = -1;
	}

	private int getFacing(){
		if(facing == -1){
			BlockState s = world.getBlockState(pos);
			if(s.has(EssentialsProperties.FACING)){
				facing = s.get(EssentialsProperties.FACING).getIndex();
			}else{
				return 0;
			}
		}

		return facing;
	}

	private int facing = -1;
	
	@Override
	protected void doEmit(BeamUnit toEmit){
		if(beamer[facing].emit(toEmit, world)){
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
