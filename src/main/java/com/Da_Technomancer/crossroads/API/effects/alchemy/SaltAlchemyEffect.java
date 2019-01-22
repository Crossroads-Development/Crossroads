package com.Da_Technomancer.crossroads.API.effects.alchemy;


import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
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
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		float range = 0.5F;
		for(EntityLiving e : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos.getX() + 0.5F - range, pos.getY() + 0.5F - range, pos.getZ() + 0.5F - range, pos.getX() + 0.5F + range, pos.getY() + 0.5F + range, pos.getZ() + 0.5F + range), FILTER)){
			if(e instanceof EntitySlime){
				e.setDead();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SLIME_BALL, ((EntitySlime) e).getSlimeSize() + 1));
			}else{
				e.setDead();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.DEADBUSH, 1));
			}
		}

		IBlockState state = world.getBlockState(pos);
		if(state.getMaterial() == Material.PLANTS || ((state.getMaterial() == Material.VINE && !(state.getBlock() instanceof BlockVine)))){
			world.setBlockState(pos, Blocks.DEADBUSH.getDefaultState());
		}else if(state.getBlock() == Blocks.GRASS){
			world.setBlockState(pos, Blocks.DIRT.getDefaultState().withProperty(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
		}
	}
}
