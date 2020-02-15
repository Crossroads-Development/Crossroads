package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.BasicBlock;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public final class OreSetup{

	public static Item ingotTin;
	public static Item nuggetTin;
	public static BasicBlock blockTin;
	public static BasicBlock oreTin;

	public static Item ingotCopper;
	public static Item nuggetCopper;
	public static BasicBlock blockCopper;
	public static BasicBlock oreCopper;

	public static Item ingotBronze;
	public static Item nuggetBronze;
	public static BasicBlock blockBronze;

	public static Item gemRuby;
	public static BasicBlock blockRuby;
	public static BasicBlock oreRuby;

	public static Item ingotCopshowium;
	public static Item nuggetCopshowium;
	public static BasicBlock blockCopshowium;

	public static Item voidCrystal;
	public static BasicBlock oreVoid;

	private static final HashMap<String, OreProfile> metalTypes = new HashMap<>();
	private static OreProfile DEFAULT;
	
	public static OreProfile findMaterial(String id){
		return metalTypes.getOrDefault(id, getDefaultMaterial());
	}

	public static Collection<OreProfile> getMaterials(){
		return metalTypes.values();
	}

	public static void registerMaterial(OreProfile mat){
		metalTypes.put(mat.getId(), mat);
	}

	/**
	 * Fallback material if a material fails to load
	 * @return The Iron OreProfile
	 */
	public static OreProfile getDefaultMaterial(){
		return DEFAULT;
	}

	protected static void init(){
		//Register CR metal ores, blocks, ingots, nuggets manually
		ingotTin = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("ingot_tin");
		blockTin = new BasicBlock("block_tin", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetTin = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("nugget_tin");
		oreTin = new BasicBlock("ore_tin", Block.Properties.create(Material.ROCK).hardnessAndResistance(3));

		ingotCopper = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("ingot_copper");
		blockCopper = new BasicBlock("block_copper", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetCopper = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("nugget_copper");
		oreCopper = new BasicBlock("ore_copper", Block.Properties.create(Material.ROCK).hardnessAndResistance(3));

		ingotBronze = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("ingot_bronze");
		blockBronze = new BasicBlock("block_bronze", Block.Properties.create(Material.IRON).hardnessAndResistance(5));
		nuggetBronze = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("nugget_bronze");

		gemRuby = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("gem_ruby");
		blockRuby = new BasicBlock("block_ruby", Block.Properties.create(Material.ROCK).hardnessAndResistance(5));
		oreRuby = new BasicBlock("ore_ruby", Block.Properties.create(Material.ROCK).hardnessAndResistance(3).harvestLevel(3));

		ingotCopshowium = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("ingot_copshowium");
		blockCopshowium = new BasicBlock("block_copshowium", Block.Properties.create(Material.IRON).harvestLevel(5));
		nuggetCopshowium = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("nugget_copshowium");

		voidCrystal = new Item(new Item.Properties().group(CRItems.TAB_CROSSROADS)).setRegistryName("void_crystal");
		oreVoid = new BasicBlock("ore_void", Block.Properties.create(Material.ROCK).harvestLevel(3));
		
		loadConfig();
	}
	
	public static void loadConfig(){
		//Clear any previously registered OreProfile if shouldReload is true. Otherwise, leave them registered
		//The old OreProfiles are wiped to allow them being defined by the server config, which could differ between the client and server
		ArrayList<String> toRemove = new ArrayList<>(metalTypes.size());
		for(OreProfile prof : metalTypes.values()){
			if(prof.wipeOnReload()){
				toRemove.add(prof.id);
			}
		}
		toRemove.forEach(metalTypes::remove);

		List<? extends String> rawInput = CRConfig.processableOres.get();

		for(String raw : rawInput){
			int spaceIndex = raw.length() - 7;
			String metal = raw.substring(0, spaceIndex);
			Color col;

			String colorString = '#' + raw.substring(spaceIndex + 1);
			try{
				col = Color.decode(colorString);
			}catch(NumberFormatException e){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col = Color.getHSBColor((float) Math.random(), 1F, 1F);
			}

			registerMaterial(new OreProfile(metal, col));
		}

		DEFAULT = metalTypes.get("iron");
		//If the config was modified to prevent iron being registered, crash
		if(DEFAULT == null){
			IllegalArgumentException e = new IllegalArgumentException("Default Ore Profile not registered!");
			Crossroads.logger.error("Config Modified to prevent registering default ore profile (iron)", e);
			throw e;
		}
	}

	public static class OreProfile{

		private final String id;
		private final Color color;
		private final boolean shouldReload;

		protected OreProfile(String nameIn, Color matColor){
			this(nameIn, matColor, true);
		}

		protected OreProfile(String nameIn, Color matColor, boolean shouldReload){
			id = nameIn;
			color = matColor;
			this.shouldReload = shouldReload;
		}

		public boolean wipeOnReload(){
			return shouldReload;
		}

		public String getId(){
			return id;
		}

		public Color getColor(){
			return color;
		}

		@OnlyIn(Dist.CLIENT)
		public String getName(){
			return MiscUtil.localize("material." + id);
		}
	}
}
