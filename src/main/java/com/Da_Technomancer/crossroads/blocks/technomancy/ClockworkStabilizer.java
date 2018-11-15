package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.technomancy.ClockworkStabilizerTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ClockworkStabilizer extends BeamBlock{

	public ClockworkStabilizer(){
		super("clock_stabilizer");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ClockworkStabilizerTileEntity();
	}
}
