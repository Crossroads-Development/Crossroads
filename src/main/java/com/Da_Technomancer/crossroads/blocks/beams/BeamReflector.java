package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.beams.BeamReflectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;

public class BeamReflector extends BeamBlock{

	public BeamReflector(){
		super("beam_reflector");
	}

	@Override
	public TileEntity newBlockEntity(IBlockReader worldIn){
		return new BeamReflectorTileEntity();
	}
}
