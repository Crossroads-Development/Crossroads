package com.Da_Technomancer.crossroads.integration.create;

import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.heat.HeatSinkTileEntity;
import com.simibubi.create.content.contraptions.fluids.tank.BoilerHeaters;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreateHeaterProxy{

	protected static void registerHeatSources(){
		BoilerHeaters.registerHeater(new ResourceLocation(Crossroads.MODID, "heat_sink"), new HeatSinkHeater());
	}

	private static class HeatSinkHeater implements BoilerHeaters.Heater{

		@Override
		public float getActiveHeat(Level level, BlockPos pos, BlockState state){
			if(level.getBlockEntity(pos) instanceof HeatSinkTileEntity hte){
				return hte.getCreateIntegrationHeatTier();
			}
			return -1;
		}
	}
}
