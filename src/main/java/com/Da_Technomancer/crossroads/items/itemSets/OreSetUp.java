package com.Da_Technomancer.crossroads.items.itemSets;

import java.util.Random;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.blocks.BasicBlock;
import com.Da_Technomancer.crossroads.items.BasicItem;

import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class OreSetup{

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

	public static void init(){
		ingotTin = new BasicItem("ingotTin", "ingotTin");
		blockTin = new BasicBlock("blockTin", Material.IRON, 2, "pickaxe", 5, "blockTin").setSoundType(SoundType.METAL);
		nuggetTin = new BasicItem("nuggetTin", "nuggetTin");
		oreTin = new BasicBlock("oreTin", Material.ROCK, 2, "pickaxe", 3, "oreTin");

		ingotCopper = new BasicItem("ingotCopper", "ingotCopper");
		blockCopper = new BasicBlock("blockCopper", Material.IRON, 2, "pickaxe", 5, "blockCopper").setSoundType(SoundType.METAL);
		nuggetCopper = new BasicItem("nuggetCopper", "nuggetCopper");
		oreCopper = new BasicBlock("oreCopper", Material.ROCK, 2, "pickaxe", 3, "oreCopper");
		oreNativeCopper = new BasicBlock("oreNativeCopper", Material.ROCK, 1, "pickaxe", 3){
			@Override
			public int quantityDropped(Random random){
				return 3;
			}

			@Override
			@Nullable
			public Item getItemDropped(IBlockState state, Random rand, int fortune){
				return nuggetCopper;
			}
		};

		ingotBronze = new BasicItem("ingotBronze", "ingotBronze");
		blockBronze = new BasicBlock("blockBronze", Material.IRON, 2, "pickaxe", 5, "blockBronze").setSoundType(SoundType.METAL);
		nuggetBronze = new BasicItem("nuggetBronze", "nuggetBronze");

		gemRuby = new BasicItem("gemRuby", "gemRuby");
		blockRuby = new BasicBlock("blockRuby", Material.ROCK, 3, "pickaxe", 5, "blockRuby");
		oreRuby = new BasicBlock("oreRuby", Material.ROCK, 3, "pickaxe", 3, "oreRuby"){
			@Override
			public int quantityDroppedWithBonus(int fortune, Random random){
				if(fortune > 0){
					return Math.max(random.nextInt(fortune + 2) - 1, 0) + 1;
				}
				return 1;
			}

			@Override
			@Nullable
			public Item getItemDropped(IBlockState state, Random rand, int fortune){
				return gemRuby;
			}
		};

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
	}
}
