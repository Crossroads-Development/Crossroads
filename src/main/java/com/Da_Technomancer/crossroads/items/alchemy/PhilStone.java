package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class PhilStone extends Item{

	public PhilStone(boolean pracStone){
		super(new Properties());
		String name = pracStone ? "prac_stone" : "phil_stone";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity){
		if(entity.onGround()){
			AABB entityBox = entity.getBoundingBox();
			clearBlock(entity.level(), MiscUtil.blockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entity.level(), MiscUtil.blockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.minZ));
			clearBlock(entity.level(), MiscUtil.blockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entity.level(), MiscUtil.blockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.minZ));
		}
		return false;
	}

	private static void clearBlock(Level world, BlockPos pos){
		BlockState state = world.getBlockState(pos);
		//Able to break any non-indestructible block, and also bedrock
		if(!state.isAir() && (state.getDestroySpeed(world, pos) >= 0 || state.getBlock() == Blocks.BEDROCK)){
			world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
			world.addParticle(ParticleTypes.SMOKE, pos.getX() + world.random.nextDouble(), pos.getY() + world.random.nextDouble(), pos.getZ() + world.random.nextDouble(), 0, 0, 0);
		}
	}
}
