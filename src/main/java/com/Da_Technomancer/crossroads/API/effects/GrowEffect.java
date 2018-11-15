package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class GrowEffect implements IEffect{

	@Override
	public void doEffect(World worldIn, BlockPos pos, double mult){
		double range = Math.sqrt(mult);
		List<EntityLivingBase> ents = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntitySelectors.IS_ALIVE);
		for(EntityLivingBase ent : ents){
			ent.heal((float) (mult / 2D));
		}
		
		for(int i = 0; i < mult; i++){
			//The state must be quarried every loop because some plants could break themselves upon growing
			IBlockState state = worldIn.getBlockState(pos);
			if(!(state.getBlock() instanceof IGrowable)){
				return;
			}
			
			String stateName = state.getBlock().getRegistryName().toString();
			
			for(String blockedID : ModConfig.getConfigStringList(ModConfig.growBlacklist, false)){
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

		private static final DamageSource POTENTIALVOID = new DamageSource("potentialvoid").setMagicDamage().setDamageBypassesArmor();

		@Override
		public void doEffect(World worldIn, BlockPos pos, double mult){
			if(worldIn.getBlockState(pos).getBlock() instanceof IGrowable && worldIn.getBlockState(pos).getBlock() != Blocks.DEADBUSH){
				worldIn.setBlockState(pos, Blocks.DEADBUSH.getDefaultState());
			}
			double range = Math.sqrt(mult);
			List<EntityLivingBase> ents = worldIn.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntitySelectors.IS_ALIVE);
			for(EntityLivingBase ent : ents){
				ent.attackEntityFrom(POTENTIALVOID, (float) mult * 3F / 4F);
			}
		}
	}
}
