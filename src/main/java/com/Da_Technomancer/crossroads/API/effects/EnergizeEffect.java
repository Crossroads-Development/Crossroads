package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;

import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergizeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		if(worldIn.getTileEntity(pos) != null){
			TileEntity te = worldIn.getTileEntity(pos);
			if(te.hasCapability(Capabilities.HEAT_CAPABILITY, null)){
				te.getCapability(Capabilities.HEAT_CAPABILITY, null).addHeat(mult);
			}
			//Effect in crystal master axis
		}
	}

	public static class VoidEnergizeEffect implements IEffect{
		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getTileEntity(pos) != null){
				TileEntity te = worldIn.getTileEntity(pos);
				if(te.hasCapability(Capabilities.HEAT_CAPABILITY, null) && te.getCapability(Capabilities.HEAT_CAPABILITY, null).getTemp() >= HeatUtil.ABSOLUTE_ZERO + mult){
					te.getCapability(Capabilities.HEAT_CAPABILITY, null).addHeat(-mult);
				}
				//Effect in crystal master axis
			}
		}
	}
}
