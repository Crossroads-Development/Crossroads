package com.Da_Technomancer.crossroads.integration.create;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.heat.HeatSinkTileEntity;
import com.simibubi.create.api.boiler.BoilerHeater;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class CreateHeaterProxy{

	protected static void registerHeatSources(){
		BoilerHeater.REGISTRY.register(CRBlocks.heatSink, new HeatSinkHeater());
	}

	private static class HeatSinkHeater implements BoilerHeater {

		@Override
		public float getHeat(Level level, BlockPos pos, BlockState state){
			if(level.getBlockEntity(pos) instanceof HeatSinkTileEntity hte){
				return hte.getCreateIntegrationHeatTier();
			}
			return -1;
		}
	}
}
