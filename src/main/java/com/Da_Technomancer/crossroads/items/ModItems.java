package com.Da_Technomancer.crossroads.items;


import java.util.ArrayList;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.tileentities.fluid.RotaryPumpTileEntity;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems {

	public static final CreativeTabs tabCrossroads = new CreativeTabs(Main.MODID) {
	    @Override public Item getTabIconItem() {
	        return GearFactory.basicGears.get(GearTypes.BRONZE);
	    }
	};
	
	public static Item metalScrap;
	public static Item dustSalt;
	public static Item mashedPotato;
	public static HandCrank handCrank;
	public static Item axle;
	public static ObsidianCuttingKit obsidianKit;
	public static Item dustCopper;
	public static Thermometer thermometer;
	public static FluidGauge fluidGauge;
	public static Speedometer speedometer;
	public static OmniMeter omnimeter;
	public static DebugReader debugReader;
	public static Vacuum vacuum;
	public static MagentaBread magentaBread;
	public static ChickenBoots chickenBoots;
	public static ItemCandleLily itemCandleLilypad;
	public static EdibleBlob edibleBlob;
	
	private static ArrayList<Item> modelQue = new ArrayList<Item>();
	
	public static void itemAddQue(Item item){
		modelQue.add(item);
	}

	public static final void init() {
		//anything I need to manually initialize for some reason
		metalScrap = new BasicItem("metalScrap");
		itemAddQue(new DebugGearWriter());
		itemAddQue(debugReader = new DebugReader());
		itemAddQue(handCrank = new HandCrank());
		itemAddQue(new DebugHeatWriter());
		dustCopper = new BasicItem("dustCopper", "dustCopper");
		dustSalt = new BasicItem("dustSalt", "dustSalt");
		axle = new BasicItem("axle", "stickIron");
		itemAddQue(obsidianKit = new ObsidianCuttingKit());
		mashedPotato = new BasicItem("mashedPotato");
		itemAddQue(thermometer = new Thermometer());
		itemAddQue(fluidGauge = new FluidGauge());
		itemAddQue(speedometer = new Speedometer());
		itemAddQue(omnimeter = new OmniMeter());
		itemAddQue(vacuum = new Vacuum());
		itemAddQue(magentaBread = new MagentaBread());
		itemAddQue(chickenBoots = new ChickenBoots());
		itemAddQue(itemCandleLilypad = new ItemCandleLily());
		itemAddQue(edibleBlob = new EdibleBlob());
	}

	@SuppressWarnings("deprecation")
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		//Any items that need models initialized without metadata other than 0, add it to modelQue. If it has metadata, add it manually.

		for(Item modeling: modelQue){
			register(modeling, 0);
		}

		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.rotaryPump), 0, RotaryPumpTileEntity.class);
		ForgeHooksClient.registerTESRItemStack(Item.getItemFromBlock(ModBlocks.steamTurbine), 0, RotaryPumpTileEntity.class);
	}
	
	@SideOnly(Side.CLIENT)
	private static void register(Item item, int subtype) {
		ModelLoader.setCustomModelResourceLocation(item, subtype, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}