package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.BasicBlock;
import com.Da_Technomancer.crossroads.items.BasicItem;

import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public final class OreSetUp{

	public static void init(){

		addOre("Copper", true, 2, true, false);
		addOre("Tin", true, 2);
		addOre("Ruby", false, 3);
		addOre("Bronze", true, 2, false);
		// You might notice that oreRuby is never in the US localization, but it
		// still works. That is because tile.oreRuby.name is built into
		// minecraft localization because emeralds were originally going to be
		// rubies, but they never removed the localization.

	}

	public static void addOre(String name, boolean metal, int miningLevel){
		addOre(name, metal, miningLevel, true);
	}

	public static void addOre(String name, boolean metal, int miningLevel, boolean makeOre){
		addOre(name, metal, miningLevel, makeOre, makeOre);
	}

	public static void addOre(String name, boolean metal, int miningLevel, boolean makeOre, boolean smeltable){

		if(metal){

			// creates an ore block, metal block, ingot and nugget, and adds
			// conversion recipes.

			new BasicBlock("block" + name, Material.ROCK, miningLevel, "pickaxe", 5, null, "block" + name, false);

			new BasicItem("ingot" + name, "ingot" + name);
			new BasicItem("nugget" + name, "nugget" + name);

			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":nugget" + name), 9), "#", '#', Item.getByNameOrId(Main.MODID + ":ingot" + name));
			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":ingot" + name), 9), "#", '#', Item.getByNameOrId(Main.MODID + ":block" + name));

			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":block" + name), 1), "###", "###", "###", '#', Item.getByNameOrId(Main.MODID + ":ingot" + name));
			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":ingot" + name), 1), "###", "###", "###", '#', Item.getByNameOrId(Main.MODID + ":nugget" + name));

			if(makeOre){
				new BasicBlock("ore" + name, Material.ROCK, miningLevel, "pickaxe", 3, null, "ore" + name, false);
				if(smeltable){
					GameRegistry.addSmelting(new ItemStack(Item.getByNameOrId(Main.MODID + ":ore" + name), 1), new ItemStack(Item.getByNameOrId(Main.MODID + ":ingot" + name), 1), .7F);
				}
			}
		}else{

			// creates ore block, gem block, and gem, and adds conversion
			// recipes.

			new BasicItem("gem" + name, "gem" + name);
			new BasicBlock("block" + name, Material.ROCK, miningLevel, "pickaxe", 5, null, "block" + name, false);

			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":gem" + name), 9), "#", '#', Item.getByNameOrId(Main.MODID + ":block" + name));
			GameRegistry.addRecipe(new ItemStack(Item.getByNameOrId(Main.MODID + ":block" + name), 1), "###", "###", "###", '#', Item.getByNameOrId(Main.MODID + ":gem" + name));
			if(makeOre){
				new BasicBlock("ore" + name, Material.ROCK, miningLevel, "pickaxe", 3, Item.getByNameOrId(Main.MODID + ":gem" + name), "ore" + name, true);
				if(smeltable){
					GameRegistry.addSmelting(new ItemStack(Item.getByNameOrId(Main.MODID + ":ore" + name), 1), new ItemStack(Item.getByNameOrId(Main.MODID + ":gem" + name), 1), 1F);
				}
			}
		}

	}

}
