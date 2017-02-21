package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems{

	public static final CreativeTabs tabCrossroads = new CreativeTabs(Main.MODID){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(omnimeter, 1);
		}
	};
	public static final CreativeTabs tabHeatCable = new CreativeTabs("heatCable"){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatConductors.COPPER).get(HeatInsulators.WOOL), 1);
		}
	};
	public static final CreativeTabs tabGear = new CreativeTabs("gear"){
		@Override
		public ItemStack getTabIconItem(){
			return new ItemStack(GearFactory.BASIC_GEARS.get(GearTypes.BRONZE));
		}
	};
	protected static final ArmorMaterial BOBO = EnumHelper.addArmorMaterial("BOBO", Main.MODID + ":bobo", 0, new int[4], 0, SoundEvents.ENTITY_HORSE_DEATH, 0F);
	protected static final ArmorMaterial TECHNOMANCY = EnumHelper.addArmorMaterial("TECHNOMANCY", "chain", 0, new int[]{0, 0, 0, 2}, 0, SoundEvents.BLOCK_ANVIL_USE, 0);

	public static DebugGearWriter debugGearWriter;
	public static DebugHeatWriter debugHeatWriter;
	public static BasicItem dustSalt;
	public static MashedPotato mashedPotato;
	public static HandCrank handCrank;
	public static ObsidianCuttingKit obsidianKit;
	public static BasicItem dustCopper;
	public static Thermometer thermometer;
	public static FluidGauge fluidGauge;
	public static Speedometer speedometer;
	public static OmniMeter omnimeter;
	public static Vacuum vacuum;
	public static MagentaBread magentaBread;
	public static ItemCandleLily itemCandleLilypad;
	public static EdibleBlob edibleBlob;
	public static BasicItem diamondWire;
	public static RainIdol rainIdol;
	public static BasicItem pureQuartz;
	public static BasicItem luminescentQuartz;
	public static BasicItem lensArray;
	public static BasicItem invisItem;
	public static SquidHelmet squidHelmet;
	public static PigZombieChestsplate pigZombieChestplate;
	public static CowLeggings cowLeggings;
	public static ChickenBoots chickenBoots;
	public static ChaosRod chaosRod;
	public static BasicItem voidCrystal;
	public static ModuleGoggles moduleGoggles;
	public static StaffTechnomancy staffTechnomancy;

	private static ArrayList<Triple<Item, Integer, ModelResourceLocation>> modelQue = new ArrayList<Triple<Item, Integer, ModelResourceLocation>>();

	public static void itemAddQue(Item item){
		modelQue.add(Triple.of(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory")));
	}
	
	public static void itemAddQue(Item item, int meta, ModelResourceLocation location){
		modelQue.add(Triple.of(item, meta, location));
	}

	public static final void init(){
		itemAddQue(debugGearWriter = new DebugGearWriter());
		itemAddQue(handCrank = new HandCrank());
		itemAddQue(debugHeatWriter = new DebugHeatWriter());
		dustCopper = new BasicItem("dust_copper", "dustCopper");
		dustSalt = new BasicItem("dust_salt", "dustSalt");
		itemAddQue(obsidianKit = new ObsidianCuttingKit());
		itemAddQue(mashedPotato = new MashedPotato());
		itemAddQue(thermometer = new Thermometer());
		itemAddQue(fluidGauge = new FluidGauge());
		itemAddQue(speedometer = new Speedometer());
		itemAddQue(omnimeter = new OmniMeter());
		itemAddQue(vacuum = new Vacuum());
		itemAddQue(magentaBread = new MagentaBread());
		itemAddQue(itemCandleLilypad = new ItemCandleLily());
		itemAddQue(edibleBlob = new EdibleBlob());
		itemAddQue(diamondWire = new BasicItem("diamond_wire", "wireDiamond"));
		itemAddQue(rainIdol = new RainIdol());
		pureQuartz = new BasicItem("pure_quartz", "gemQuartz");
		luminescentQuartz = new BasicItem("luminescent_quartz");
		lensArray = new BasicItem("lens_array");
		invisItem = new BasicItem("invis_item", null, false);
		itemAddQue(squidHelmet = new SquidHelmet());
		itemAddQue(pigZombieChestplate = new PigZombieChestsplate());
		itemAddQue(cowLeggings = new CowLeggings());
		itemAddQue(chickenBoots = new ChickenBoots());
		itemAddQue(chaosRod = new ChaosRod());
		voidCrystal = new BasicItem("void_crystal");
		itemAddQue(moduleGoggles = new ModuleGoggles());
		itemAddQue(staffTechnomancy = new StaffTechnomancy());
	}

	@SideOnly(Side.CLIENT)
	public static void initModels(){
		for(Triple<Item, Integer, ModelResourceLocation> modeling : modelQue){
			ModelLoader.setCustomModelResourceLocation(modeling.getLeft(), modeling.getMiddle(), modeling.getRight());
		}
	}
}