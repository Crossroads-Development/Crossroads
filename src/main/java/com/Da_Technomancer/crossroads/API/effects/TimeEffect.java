package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TimeEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof ITickable && worldIn.rand.nextInt(64) < mult){
			for(EnumFacing dir : EnumFacing.values()){
				if(te.hasCapability(Capabilities.MAGIC_CAPABILITY, dir)){
					return;
				}
			}

			//Each tick the TileEntity is queried again because some TileEntities destroy themselves on tick.
			((ITickable) te).update();
		}
		if(worldIn.getBlockState(pos).getBlock().getTickRandomly()){
			for(int i = 0; i < mult * BeamManager.BEAM_TIME; i++){
				worldIn.getBlockState(pos).getBlock().randomTick(worldIn, pos, worldIn.getBlockState(pos), worldIn.rand);
			}
		}
	}

	public static class VoidTimeEffect implements IEffect{

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			FluxUtil.fluxEvent(worldIn, pos, (int) mult);
		}
	}
}
