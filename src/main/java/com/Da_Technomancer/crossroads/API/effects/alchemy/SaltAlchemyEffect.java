package com.Da_Technomancer.crossroads.API.effects.alchemy;


import com.Da_Technomancer.crossroads.API.alchemy.MatterPhase;
import com.google.common.base.Predicate;

import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockVine;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SaltAlchemyEffect implements IAlchEffect{

	private static final Predicate<Entity> FILTER = (Entity e) -> (e instanceof EntitySlime || e instanceof EntityCreeper) && EntitySelectors.IS_ALIVE.apply(e);

	@Override
	public void doEffect(World world, BlockPos pos, double amount, MatterPhase phase){
		int radius = Math.min(1, (int) amount / 100);
		for(EntityLiving e : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.add(-radius, -radius, -radius), pos.add(radius, radius, radius)), FILTER)){
			if(e instanceof EntitySlime){
				e.setDead();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SLIME_BALL, ((EntitySlime) e).getSlimeSize() + 1));
			}else{
				e.setDead();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.DEADBUSH, 1));
			}
		}

		for(int x = -radius; x <= radius; x++){
			for(int y = -radius; y <= radius; y++){
				for(int z = -radius; z <= radius; z++){
					BlockPos killPos = pos.add(x, y, z);
					IBlockState state = world.getBlockState(killPos);
					if(state.getMaterial() == Material.PLANTS || ((state.getMaterial() == Material.VINE && !(state.getBlock() instanceof BlockVine)))){
						world.setBlockState(killPos, Blocks.DEADBUSH.getDefaultState());
					}else if(state.getBlock() == Blocks.GRASS){
						world.setBlockState(killPos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
					}
				}
			}
		}
	}
}
