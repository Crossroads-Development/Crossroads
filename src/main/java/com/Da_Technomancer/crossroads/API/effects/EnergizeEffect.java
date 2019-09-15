package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergizeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		TileEntity te = worldIn.getTileEntity(pos);
		IHeatHandler heatHandler;
		if(te != null && (heatHandler = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)) != null){
			heatHandler.addHeat(mult);
			//Effect in crystal master axis
		}else{
			BlockState state = worldIn.getBlockState(pos);
			if(state.getBlock().isAir(state, worldIn, pos)){
				worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
			}else{
				BlockPos offsetPos = pos.offset(dir);
				state = worldIn.getBlockState(offsetPos);
				if(state.getBlock().isAir(state, worldIn, offsetPos)){
					worldIn.setBlockState(offsetPos, Blocks.FIRE.getDefaultState());
				}
			}
		}
	}

	public static class VoidEnergizeEffect implements IEffect{
		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			TileEntity te = worldIn.getTileEntity(pos);
			IHeatHandler heatHandler;
			if(te != null && (heatHandler = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)) != null){
				heatHandler.addHeat(-Math.min(mult, heatHandler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
				//Effect in crystal master axis
			}
		}
	}
}
