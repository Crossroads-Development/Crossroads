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

	private static final double MULT = 25;

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			IHeatHandler hitHandler = getHitHandler(worldIn, pos, dir);
			if(voi){
				if(hitHandler != null){
					hitHandler.addHeat(-Math.min(MULT * power, hitHandler.getTemp() - HeatUtil.ABSOLUTE_ZERO));
					//Effect in crystal master axis
				}
			}else{
				if(hitHandler != null){
					hitHandler.addHeat(MULT * power);
					//Effect in crystal master axis
				}else{
					BlockState state = worldIn.getBlockState(pos);
					if(state.getBlock().isAir(state, worldIn, pos)){
						//Set fires
						worldIn.setBlockState(pos, Blocks.FIRE.getDefaultState());
					}else if(dir != null){
						//Set a fire w/ offset
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

	@Nullable
	private static IHeatHandler getHitHandler(World w, BlockPos pos, Direction side){
		TileEntity te = w.getTileEntity(pos);
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
