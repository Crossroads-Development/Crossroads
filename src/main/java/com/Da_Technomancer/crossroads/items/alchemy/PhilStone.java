package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CrossroadsItems;

import net.minecraft.block.BlockState;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhilStone extends Item{

	public PhilStone(boolean pracStone){
		String name = pracStone ? "prac_stone" : "phil_stone";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	public boolean onEntityItemUpdate(ItemEntity entityItem){
		if(entityItem.onGround){
			AxisAlignedBB entityBox = entityItem.getEntityBoundingBox();
			clearBlock(entityItem.world, new BlockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entityItem.world, new BlockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.minZ));
			clearBlock(entityItem.world, new BlockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entityItem.world, new BlockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.minZ));
		}
		return false;
	}

	private static void clearBlock(World world, BlockPos pos){
		BlockState state = world.getBlockState(pos);
		if(!state.getBlock().isAir(state, world, pos)){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), 0, 0, 0);
		}
	}
}
