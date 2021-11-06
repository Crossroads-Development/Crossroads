package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class EnergizeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			IHeatHandler hitHandler = getHitHandler(worldIn, pos, dir);
			if(voi){
				if(hitHandler != null){
					int mult = CRConfig.beamHeatMult.get();
					hitHandler.addHeat(-Math.min(mult * power, hitHandler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
					//Effect in crystal master axis
				}
			}else{
				if(hitHandler != null){
					int mult = CRConfig.beamHeatMult.get();
					hitHandler.addHeat(Math.min(mult * power, HeatUtil.MAX_TEMP - hitHandler.getTemp()));
					//Effect in crystal master axis
				}else{
					BlockState state = worldIn.getBlockState(pos);
					if(state.isAir()){
						//Set fires
						worldIn.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
					}else if(dir != null){
						//Set a fire w/ offset
						BlockPos offsetPos = pos.relative(dir);
						state = worldIn.getBlockState(offsetPos);
						if(state.isAir()){
							worldIn.setBlockAndUpdate(offsetPos, Blocks.FIRE.defaultBlockState());
						}
					}
				}
			}
		}
	}

	@Nullable
	private static IHeatHandler getHitHandler(Level w, BlockPos pos, Direction side){
		BlockEntity te = w.getBlockEntity(pos);
		if(te == null){
			return null;
		}
		//Try hit face first
		LazyOptional<IHeatHandler> opt = te.getCapability(Capabilities.HEAT_CAPABILITY, side);
		if(opt.isPresent()){
			return opt.orElseThrow(NullPointerException::new);
		}else if(side != null){
			//Try the null side as a fallback
			opt = te.getCapability(Capabilities.HEAT_CAPABILITY, null);
		}
		if(opt.isPresent()){
			return opt.orElseThrow(NullPointerException::new);
		}
		return null;
	}
}
