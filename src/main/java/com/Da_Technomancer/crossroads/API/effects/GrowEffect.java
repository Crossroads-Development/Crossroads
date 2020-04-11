package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IGrowable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class GrowEffect extends BeamEffect{

	//Crop types can be blacklisted from growth through the beam using the grow_blacklist tag. Intended for things like magical crops
	private static final Tag<Block> growBlacklist = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "growth_blacklist"));
	private static final DamageSource POTENTIAL_VOID = new DamageSource("potentialvoid").setMagicDamage().setDamageBypassesArmor();

	@Override
	public void doBeamEffect(EnumBeamAlignments align, boolean voi, int power, World worldIn, BlockPos pos, @Nullable Direction dir){
		if(!performTransmute(align, voi, power, worldIn, pos)){
			double range = Math.sqrt(power) / 2D;
			if(voi){
				BlockState state = worldIn.getBlockState(pos);
				if(state.getBlock() instanceof IGrowable && state.getBlock() != Blocks.DEAD_BUSH){
					if(state.getBlock() == Blocks.GRASS_BLOCK){
						worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
					}else{
						worldIn.setBlockState(pos, Blocks.DEAD_BUSH.getDefaultState());
					}
				}

				List<LivingEntity> ents = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range + 1, pos.getY() + range + 1, pos.getZ() + range + 1), EntityPredicates.IS_ALIVE);
				for(LivingEntity ent : ents){
					if(ent.isEntityUndead()){
						ent.heal(power * 3F / 4F);
					}else{
						ent.attackEntityFrom(POTENTIAL_VOID, power * 3F / 4F);
					}
				}
			}else{
				List<LivingEntity> ents = worldIn.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX() - range, pos.getY() - range, pos.getZ() - range, pos.getX() + range, pos.getY() + range, pos.getZ() + range), EntityPredicates.IS_ALIVE);
				for(LivingEntity ent : ents){
					if(ent.isEntityUndead()){
						ent.attackEntityFrom(POTENTIAL_VOID, power / 2F);
					}else{
						ent.heal(power / 2F);
					}
				}

				for(int i = 0; i < power; i++){
					//The state must be quarried every loop because some plants could break themselves upon growing
					BlockState state = worldIn.getBlockState(pos);
					if(!(state.getBlock() instanceof IGrowable)){
						return;
					}

					if(growBlacklist.contains(state.getBlock())){
						return;
					}
					IGrowable growable = (IGrowable) state.getBlock();
					if(growable.canGrow(worldIn, pos, state, false)){
						growable.grow(worldIn, worldIn.rand, pos, state);
					}
				}
			}
		}
	}
}
