package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

import javax.annotation.Nullable;

public class TimeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, Level worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				FluxUtil.fluxEvent(worldIn, pos);
			}else{
				BlockEntity te = worldIn.getBlockEntity(pos);
				if(worldIn.random.nextInt(64) < power){
					if(te instanceof ITickableTileEntity){
						//TODO convert to the new ticker system
						//Don't do extra ticks to beam blocks
						for(Direction side : Direction.values()){
							if(te.getCapability(Capabilities.BEAM_CAPABILITY, side).isPresent()){
								return;
							}
						}
						((ITickableTileEntity) te).tick();
					}

					BlockState state = worldIn.getBlockState(pos);
					if(state.isRandomlyTicking()){
						state.randomTick((ServerLevel) worldIn, pos, worldIn.random);
					}
				}
			}
		}
	}
}
