package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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
		for(EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1F, pos.getY() + 1F, pos.getZ() + 1F), EntitySelectors.IS_ALIVE)){
			e.attackEntityFrom(ACID_DAMAGE, getDamage());
		}

		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == Blocks.BEDROCK && isRegia()){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ModConfig.getConfigBool(ModConfig.bedrockDust, false) ? new ItemStack(ModItems.bedrockDust, 1) : new ItemStack(Blocks.BEDROCK, 1));
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
			return;
		}

		ItemStack itemForm = new ItemStack(state.getBlock());
		if(itemForm.isEmpty()){
			return;
		}
		int[] oreDict = OreDictionary.getOreIDs(new ItemStack(state.getBlock()));
		for(int id : oreDict){
			String name = OreDictionary.getOreName(id);
			switch(name){
				case "blockIron":
					InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, world.rand.nextInt(9) + 1));
					world.setBlockState(pos, Blocks.AIR.getDefaultState());
					return;
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
				case "blockGold":
					if(isRegia()){
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, world.rand.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
					}
					return;
				default:
					break;
			}
		}
	}
}
