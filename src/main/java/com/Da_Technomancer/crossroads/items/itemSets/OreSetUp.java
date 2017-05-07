package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.Random;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.BasicBlock;
import com.Da_Technomancer.crossroads.items.BasicItem;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class OreSetUp{

	public static BasicItem ingotTin;
	public static BasicItem nuggetTin;
	public static BasicBlock blockTin;
	public static BasicBlock oreTin;

	public static BasicItem ingotCopper;
	public static BasicItem nuggetCopper;
	public static BasicBlock blockCopper;
	public static BasicBlock oreCopper;
	public static BasicBlock oreNativeCopper;

	public static BasicItem ingotBronze;
	public static BasicItem nuggetBronze;
	public static BasicBlock blockBronze;

	public static BasicItem gemRuby;
	public static BasicBlock blockRuby;
	public static BasicBlock oreRuby;

	public static BasicItem ingotCopshowium;
	public static BasicItem nuggetCopshowium;
	public static BasicBlock blockCopshowium;

	public static void init(){
		boolean oreDict = ModConfig.registerOres.getBoolean();
		ingotTin = new BasicItem("ingot_tin", oreDict ? "ingotTin" : null);
		blockTin = new BasicBlock("block_tin", Material.IRON, 2, "pickaxe", 5, SoundType.METAL, oreDict ? "blockTin" : null){
			@Override
			public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
				return true;
			}
		};
		nuggetTin = new BasicItem("nugget_tin", oreDict ? "nuggetTin" : null);
		oreTin = new BasicBlock("ore_tin", Material.ROCK, 2, "pickaxe", 3, null, oreDict ? "oreTin" : null);

		ingotCopper = new BasicItem("ingot_copper", oreDict ? "ingotCopper" : null);
		blockCopper = new BasicBlock("block_copper", Material.IRON, 2, "pickaxe", 5, SoundType.METAL, oreDict ? "blockCopper" : null){
			@Override
			public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
				return true;
			}
		};
		nuggetCopper = new BasicItem("nugget_copper", oreDict ? "nuggetCopper" : null);
		oreCopper = new BasicBlock("ore_copper", Material.ROCK, 2, "pickaxe", 3, null, oreDict ? "oreCopper" : null);
		oreNativeCopper = new BasicBlock("ore_native_copper", Material.ROCK, 1, "pickaxe", 3){
			@Override
			public int quantityDropped(Random random){
				return 3;
			}

			@Override
			public Item getItemDropped(IBlockState state, Random rand, int fortune){
				return nuggetCopper;
			}
		};

		ingotBronze = new BasicItem("ingot_bronze", oreDict ? "ingotBronze" : null);
		blockBronze = new BasicBlock("block_bronze", Material.IRON, 2, "pickaxe", 5, SoundType.METAL, oreDict ? "blockBronze" : null){
			@Override
			public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
				return true;
			}
		};
		nuggetBronze = new BasicItem("nugget_bronze", oreDict ? "nuggetBronze" : null);

		gemRuby = new BasicItem("gem_ruby", "gemRuby");
		blockRuby = new BasicBlock("block_ruby", Material.ROCK, 3, "pickaxe", 5, null, "blockRuby"){
			@Override
			public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
				return true;
			}
		};
		oreRuby = new BasicBlock("ore_ruby", Material.ROCK, 3, "pickaxe", 3, null, "oreRuby"){
			@Override
			public int quantityDroppedWithBonus(int fortune, Random random){
				if(fortune > 0){
					return Math.max(random.nextInt(fortune + 2) - 1, 0) + 1;
				}
				return 1;
			}

			@Override
			public Item getItemDropped(IBlockState state, Random rand, int fortune){
				return gemRuby;
			}
		};

		ingotCopshowium = new BasicItem("ingot_copshowium", "ingotCopshowium");
		blockCopshowium = new BasicBlock("block_copshowium", Material.IRON, 2, "pickaxe", 5, SoundType.METAL, "blockCopshowium"){
			@Override
			public boolean isBeaconBase(IBlockAccess worldObj, BlockPos pos, BlockPos beacon){
				return true;
			}
		};
		nuggetCopshowium = new BasicItem("nugget_copshowium", "nuggetCopshowium");

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(nuggetTin, 9), "ingotTin"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ingotTin, 9), "blockTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ingotTin, 1), "***", "***", "***", '*', "nuggetTin"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockTin, 1), "***", "***", "***", '*', "ingotTin"));
		GameRegistry.addSmelting(new ItemStack(oreTin, 1), new ItemStack(ingotTin, 1), .7F);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(nuggetCopper, 9), "ingotCopper"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ingotCopper, 9), "blockCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ingotCopper, 1), "***", "***", "***", '*', "nuggetCopper"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCopper, 1), "***", "***", "***", '*', "ingotCopper"));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(nuggetBronze, 9), "ingotBronze"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ingotBronze, 9), "blockBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ingotBronze, 1), "***", "***", "***", '*', "nuggetBronze"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockBronze, 1), "***", "***", "***", '*', "ingotBronze"));

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(gemRuby, 4), "blockRuby"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockRuby, 1), "**", "**", '*', "gemRuby"));
		GameRegistry.addSmelting(new ItemStack(oreRuby, 1), new ItemStack(gemRuby, 1), 1F);

		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(nuggetCopshowium, 9), "ingotCopshowium"));
		GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(ingotCopshowium, 9), "blockCopshowium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(ingotCopshowium, 1), "***", "***", "***", '*', "nuggetCopshowium"));
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(blockCopshowium, 1), "***", "***", "***", '*', "ingotCopshowium"));
	}
}
