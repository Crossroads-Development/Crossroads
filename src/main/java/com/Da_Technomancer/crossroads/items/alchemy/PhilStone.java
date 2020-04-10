package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PhilStone extends Item{

	public PhilStone(boolean pracStone){
		super(new Properties().group(CRItems.TAB_CROSSROADS));
		String name = pracStone ? "prac_stone" : "phil_stone";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity){
		if(entity.onGround){
			AxisAlignedBB entityBox = entity.getBoundingBox();
			clearBlock(entity.world, new BlockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entity.world, new BlockPos(entityBox.maxX, entityBox.minY - 0.05D, entityBox.minZ));
			clearBlock(entity.world, new BlockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.maxZ));
			clearBlock(entity.world, new BlockPos(entityBox.minX, entityBox.minY - 0.05D, entityBox.minZ));
		}
		return false;
	}

	private static void clearBlock(World world, BlockPos pos){
		BlockState state = world.getBlockState(pos);
		//Able to break any non-indestructible block, and also bedrock
		if(!state.getBlock().isAir(state, world, pos) && (state.getBlockHardness(world, pos) >= 0 || state.getBlock() == Blocks.BEDROCK)){
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			world.addParticle(ParticleTypes.SMOKE, pos.getX() + world.rand.nextDouble(), pos.getY() + world.rand.nextDouble(), pos.getZ() + world.rand.nextDouble(), 0, 0, 0);
		}
	}
}
