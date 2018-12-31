package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
		TileEntity te = worldIn.getTileEntity(pos);
		if(worldIn.rand.nextInt(64) < mult){
			if(te instanceof ITickable){

				//Don't do extra ticks to beam blocks
				for(EnumFacing side : EnumFacing.values()){
					if(te.hasCapability(Capabilities.BEAM_CAPABILITY, side)){
						return;
					}
				}
				((ITickable) te).update();
			}

			if(worldIn.getBlockState(pos).getBlock().getTickRandomly()){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), worldIn.rand);
			}
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, EnumFacing dir){
			FluxUtil.fluxEvent(worldIn, pos, mult);
		}
	}
}
