package com.Da_Technomancer.crossroads.API.effects.alchemy;


import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.google.common.base.Predicate;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.Blocks;
import net.minecraft.block.VineBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.monster.SlimeEntity;
import net.minecraft.item.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SaltAlchemyEffect implements IAlchEffect{

	private static final Predicate<Entity> FILTER = (Entity e) -> (e instanceof SlimeEntity || e instanceof CreeperEntity) && EntityPredicates.IS_ALIVE.apply(e);

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		float range = 0.5F;
		for(MobEntity e : world.getEntitiesWithinAABB(MobEntity.class, new AxisAlignedBB(pos.getX() + 0.5F - range, pos.getY() + 0.5F - range, pos.getZ() + 0.5F - range, pos.getX() + 0.5F + range, pos.getY() + 0.5F + range, pos.getZ() + 0.5F + range), FILTER)){
			if(e instanceof SlimeEntity){
				e.remove();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.SLIME_BALL, ((SlimeEntity) e).getSlimeSize() + 1));
			}else{
				e.remove();
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.DEADBUSH, 1));
			}
		}

		BlockState state = world.getBlockState(pos);
		if(state.getMaterial() == Material.PLANTS || ((state.getMaterial() == Material.VINE && !(state.getBlock() instanceof VineBlock)))){
			world.setBlockState(pos, Blocks.DEADBUSH.getDefaultState());
		}else if(state.getBlock() == Blocks.GRASS){
			world.setBlockState(pos, Blocks.DIRT.getDefaultState().with(BlockDirt.VARIANT, BlockDirt.DirtType.COARSE_DIRT));
		}
	}
}
