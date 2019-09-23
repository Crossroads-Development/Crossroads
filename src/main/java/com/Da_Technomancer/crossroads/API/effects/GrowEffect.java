package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.CrossroadsConfig;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class GrowEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
		double range = Math.sqrt(mult);
		List<LivingEntity> ents = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntityPredicates.IS_ALIVE);
		for(LivingEntity ent : ents){
			ent.heal((float) (mult / 2D));
		}

		for(int i = 0; i < mult; i++){
			//The state must be quarried every loop because some plants could break themselves upon growing
			BlockState state = worldIn.getBlockState(pos);
			if(!(state.getBlock() instanceof IGrowable)){
				return;
			}
			
			String stateName = state.getBlock().getRegistryName().toString();

			for(String blockedID : CrossroadsConfig.getConfigStringList(CrossroadsConfig.growBlacklist, false)){
				if(blockedID.equals(stateName)){
					return;
				}
			}
			IGrowable growable = (IGrowable) state.getBlock();
			if(growable.canGrow(worldIn, pos, state, false)){
				growable.grow(worldIn, worldIn.rand, pos, state);
			}
		}
	}

	public static class KillEffect implements IEffect{

		private static final DamageSource POTENTIAL_VOID = new DamageSource("potentialvoid").setMagicDamage().setDamageBypassesArmor();

		@Override
		public void doEffect(World worldIn, BlockPos pos, int mult, Direction dir){
			BlockState state = worldIn.getBlockState(pos);
			if(state.getBlock() instanceof IGrowable && state.getBlock() != Blocks.DEAD_BUSH){
				if(state.getBlock() == Blocks.GRASS){
					worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
				}else{
					worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
				}
			}
			double range = Math.sqrt(mult);
			List<LivingEntity> ents = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range + 1, pos.getY() + range + 1, pos.getZ() + range + 1), EntityPredicates.IS_ALIVE);
			for(LivingEntity ent : ents){
				ent.attackEntityFrom(POTENTIAL_VOID, (float) mult * 3F / 4F);
			}
		}
	}
}
