package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class EnergizeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			TileEntity te = worldIn.getTileEntity(pos);
			if(voi){
				LazyOptional<IHeatHandler> heatOpt;
				if(te != null && (heatOpt = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)).isPresent()){
					IHeatHandler handler = heatOpt.orElseThrow(NullPointerException::new);
					handler.addHeat(-Math.min(power, handler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
					//Effect in crystal master axis
				}
			}else{
				LazyOptional<IHeatHandler> heatHandler;
				if(te != null && (heatHandler = te.getCapability(Capabilities.HEAT_CAPABILITY, dir)).isPresent()){
					heatHandler.orElseThrow(NullPointerException::new).addHeat(power);
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
		}
	}
}
