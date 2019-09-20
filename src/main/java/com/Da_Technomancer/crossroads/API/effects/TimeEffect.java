package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		TileEntity te = worldIn.getTileEntity(pos);
		if(worldIn.rand.nextInt(64) < mult){
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

			if(state.getBlock().ticksRandomly(state)){
				state.getBlock().randomTick(state, worldIn, pos, worldIn.rand);
			}
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			FluxUtil.fluxEvent(worldIn, pos, worldIn.rand.nextInt(mult) + 1);
		}
	}
}
