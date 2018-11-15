package com.Da_Technomancer.crossroads.blocks.magic;

import com.Da_Technomancer.crossroads.API.templates.BeamBlock;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class ArcaneReflector extends BeamBlock{

	public ArcaneReflector(){
		super("arcane_reflector");
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta){
		return new ArcaneReflectorTileEntity();
	}
}
