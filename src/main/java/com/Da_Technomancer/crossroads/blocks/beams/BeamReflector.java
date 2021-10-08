package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamReflectorTileEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.BlockGetter;

public class BeamReflector extends BeamBlock{

	public BeamReflector(){
		super("beam_reflector");
	}

	@Override
	public BlockEntity newBlockEntity(BlockGetter worldIn){
		return new BeamReflectorTileEntity();
	}
}
