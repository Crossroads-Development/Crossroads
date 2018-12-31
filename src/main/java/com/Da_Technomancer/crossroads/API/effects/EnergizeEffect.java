package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergizeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		TileEntity te = worldIn.getTileEntity(pos);
		IHeatHandler heatHandler;
		if(te != null && (heatHandler = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)) != null){
			heatHandler.addHeat(mult);
			//Effect in crystal master axis
		}
	}

	public static class VoidEnergizeEffect implements IEffect{
		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			TileEntity te = worldIn.getTileEntity(pos);
			IHeatHandler heatHandler;
			if(te != null && (heatHandler = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)) != null){
				heatHandler.addHeat(-Math.min(mult, heatHandler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
				//Effect in crystal master axis
			}
		}
	}
}
