package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentMap;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags;
import net.minecraftforge.oredict.OreDictionary;

public class AcidAlchemyEffect implements IAlchEffect{

	public static final DamageSource ACID_DAMAGE = new DamageSource("chemical");

	protected int getDamage(){
		return 8;
	}

	protected boolean isRegia(){
		return false;
	}

	@Override
	public void doEffect(World world, BlockPos pos, int amount, EnumMatterPhase phase, ReagentMap reags){
		for(LivingEntity e : world.getEntitiesWithinAABB(LivingEntity.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 1F, pos.getZ() + 1F), EntityPredicates.IS_ALIVE)){
			e.attackEntityFrom(ACID_DAMAGE, getDamage());
		}

		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.BEDROCK && isRegia()){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(CRItems.bedrockDust, 1));
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}

		ItemStack itemForm = new ItemStack(state.getBlock());
		if(itemForm.isEmpty()){
			return;
		}

		Block block = state.getBlock();
		if(Tags.Blocks.STORAGE_BLOCKS_IRON.contains(block)){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, world.rand.nextInt(9) + 1));
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}
		if(Tags.Blocks.STORAGE_BLOCKS_GOLD.contains(block)){
			if(isRegia()){
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, world.rand.nextInt(9) + 1));
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
			return;
		}
		//TODO cases for tin, copper, bronze

		int[] oreDict = OreDictionary.getOreIDs(new ItemStack(state.getBlock()));
		for(int id : oreDict){
			String name = OreDictionary.getOreName(id);
			switch(name){
				case "blockCopper":
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotCopper, world.rand.nextInt(9) + 1));
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					return;
				case "blockTin":
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotTin, world.rand.nextInt(9) + 1));
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					return;
				case "blockBronze":
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotBronze, world.rand.nextInt(9) + 1));
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					return;
				default:
					break;
			}
		}
	}
}
