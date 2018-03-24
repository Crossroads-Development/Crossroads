package com.Da_Technomancer.crossroads.API.effects.alchemy;

import java.util.Random;

import com.Da_Technomancer.crossroads.API.alchemy.EnumMatterPhase;
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

	protected static final Random RAND = new Random();
	public static final DamageSource ACID_DAMAGE = new DamageSource("chemical").setDamageBypassesArmor();
	
	@Override
	public void doEffect(World world, BlockPos pos, double amount, double temp, EnumMatterPhase phase){
		if((phase == EnumMatterPhase.SOLUTE || phase == EnumMatterPhase.LIQUID) && amount >= .02D){
			for(EntityLivingBase e : world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(pos.add(-1, -1, -1), pos.add(2, 2, 2)), EntitySelectors.IS_ALIVE)){
				e.attackEntityFrom(ACID_DAMAGE, ((float) (amount)));
			}
			
			IBlockState state = world.getBlockState(pos);
			int[] oreDict = OreDictionary.getOreIDs(new ItemStack(state.getBlock()));
			for(int id : oreDict){
				String name = OreDictionary.getOreName(id);
				switch(name){
					case "blockIron":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Items.IRON_INGOT, RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockCopper":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotCopper, RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockTin":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotTin, RAND.nextInt(9) + 1));
						world.setBlockState(pos, Blocks.AIR.getDefaultState());
						return;
					case "blockBronze":
						InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(OreSetup.ingotBronze, RAND.nextInt(9) + 1));
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
