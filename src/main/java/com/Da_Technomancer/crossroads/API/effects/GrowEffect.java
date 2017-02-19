package com.Da_Technomancer.crossroads.API.effects;

import java.util.List;

import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class GrowEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		for(int i = 0; i < mult; i++){
			IBlockState state = worldIn.getBlockState(pos);
			if(!(state.getBlock() instanceof IGrowable)){
				return;
			}
			IGrowable igrowable = (IGrowable) state.getBlock();
			if(igrowable.canGrow(worldIn, pos, state, false)){
				igrowable.grow(worldIn, worldIn.rand, pos, state);
			}
		}
	}
	
	public static class KillEffect implements IEffect{

		private static final DamageSource POTENTIALVOID = new DamageSource("potentialvoid").setMagicDamage().setDamageBypassesArmor();
		
		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getBlockState(pos).getBlock() instanceof IGrowable && worldIn.getBlockState(pos).getBlock() != Blocks.DEADBUSH){
				worldIn.setBlockState(pos, Blocks.DEADBUSH.getDefaultState());
			}
			int range = (int) Math.min(8, mult);
			List<EntityLivingBase> ents = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntitySelectors.IS_ALIVE);
			if(ents != null){
				for(EntityLivingBase ent : ents){
					ent.attackEntityFrom(POTENTIALVOID, (float) Math.min(128, mult));
				}
			}
		}
	}
}
