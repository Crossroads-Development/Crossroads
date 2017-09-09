package com.Da_Technomancer.crossroads.API.effects.alchemy;

import com.Da_Technomancer.crossroads.API.alchemy.MatterPhase;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetup;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class AquaRegiaAlchemyEffect implements IAlchEffect{
	
	@Override
	public void doEffect(World world, BlockPos pos, double amount, MatterPhase phase){
		if(amount >= .1D){
			for(EntityLiving e : world.getEntitiesWithinAABB(EntityLiving.class, new AxisAlignedBB(pos, pos.add(1, 1, 1)), EntitySelectors.IS_ALIVE)){
				e.attackEntityFrom(AcidAlchemyEffect.ACID_DAMAGE, ((float) (amount * 10D)));
			}
			
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() == Blocks.BEDROCK){
				InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Blocks.BEDROCK, 1));
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
				return;
			}
			
			int[] oreDict = OreDictionary.getOreIDs(new ItemStack(state.getBlock()));
			for(int id : oreDict){
				String name = OreDictionary.getOreName(id);
				switch(name){
					case "blockIron":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, AcidAlchemyEffect.RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockCopper":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotCopper, AcidAlchemyEffect.RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockTin":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotTin, AcidAlchemyEffect.RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockBronze":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotBronze, AcidAlchemyEffect.RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockGold":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.GOLD_INGOT, AcidAlchemyEffect.RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					default:
						break;
				}
			}
			
			//TODO think of a few more acid effects
		}
	}
}
