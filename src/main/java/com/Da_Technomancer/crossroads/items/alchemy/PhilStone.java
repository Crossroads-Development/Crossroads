package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.Blocks;
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
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public boolean onEntityItemUpdate(EntityItem entityItem){
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
		IBlockState state = world.getBlockState(pos);
		if(!state.getBlock().isAir(state, world, pos)){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), 0, 0, 0);
		}
	}
}
