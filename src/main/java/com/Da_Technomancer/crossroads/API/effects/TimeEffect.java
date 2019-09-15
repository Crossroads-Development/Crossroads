package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
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
					if(te.hasCapability(Capabilities.BEAM_CAPABILITY, side)){
						return;
					}
				}
				((ITickableTileEntity) te).update();
			}

			if(worldIn.getBlockState(pos).getBlock().getTickRandomly()){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), worldIn.rand);
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
