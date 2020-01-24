package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class TimeEffect extends BeamEffect{

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			if(voi){
				FluxUtil.fluxEvent(worldIn, pos);
			}else{
				TileEntity te = worldIn.getTileEntity(pos);
				if(worldIn.rand.nextInt(64) < power){
					if(te instanceof ITickableTileEntity){

						//Don't do extra ticks to beam blocks
						for(Direction side : Direction.values()){
							if(te.getCapability(Capabilities.BEAM_CAPABILITY, side).isPresent()){
								return;
							}
						}
						((ITickableTileEntity) te).tick();
					}

					BlockState state = worldIn.getBlockState(pos);
					if(state.ticksRandomly()){
						state.randomTick(worldIn, pos, worldIn.rand);
					}
				}
			}
		}
	}
}
