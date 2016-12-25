package com.Da_Technomancer.crossroads.items;

import java.util.ArrayList;
import java.util.List;

import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModItems{

	public static final CreativeTabs tabCrossroads = new CreativeTabs(Main.MODID){
		@Override
		public Item getTabIconItem(){
			return GearFactory.basicGears.get(GearTypes.BRONZE);
		}
	};

	@Deprecated
	public static BasicItem metalScrap;
	public static BasicItem dustSalt;
	public static BasicItem mashedPotato;
	public static HandCrank handCrank;
	public static ObsidianCuttingKit obsidianKit;
	public static BasicItem dustCopper;
	public static Thermometer thermometer;
	public static FluidGauge fluidGauge;
	public static Speedometer speedometer;
	public static OmniMeter omnimeter;
	public static DebugReader debugReader;
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

	private static ArrayList<Item> modelQue = new ArrayList<Item>();

	public static void itemAddQue(Item item){
		modelQue.add(item);
	}

	public static final void init(){
		metalScrap = new BasicItem("metalScrap"){
			@Override
			@SideOnly(Side.CLIENT)
			public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
				tooltip.add("THIS ITEM IS BEING REMOVED! PLEASE USE IT UP!");
			}
		};
		itemAddQue(new DebugGearWriter());
		itemAddQue(debugReader = new DebugReader());
		itemAddQue(handCrank = new HandCrank());
		itemAddQue(new DebugHeatWriter());
		dustCopper = new BasicItem("dustCopper", "dustCopper");
		dustSalt = new BasicItem("dustSalt", "dustSalt");
		itemAddQue(obsidianKit = new ObsidianCuttingKit());
		mashedPotato = new BasicItem("mashedPotato");
		itemAddQue(thermometer = new Thermometer());
		itemAddQue(fluidGauge = new FluidGauge());
		itemAddQue(speedometer = new Speedometer());
		itemAddQue(omnimeter = new OmniMeter());
		itemAddQue(vacuum = new Vacuum());
		itemAddQue(magentaBread = new MagentaBread());
		itemAddQue(itemCandleLilypad = new ItemCandleLily());
		itemAddQue(edibleBlob = new EdibleBlob());
		itemAddQue(diamondWire = new BasicItem("diamondWire", "wireDiamond"));
		itemAddQue(rainIdol = new RainIdol());
		pureQuartz = new BasicItem("pureQuartz", "gemQuartz");
		luminescentQuartz = new BasicItem("luminescentQuartz");
		lensArray = new BasicItem("lensArray");
		invisItem = new BasicItem("invisItem", null, false);
		itemAddQue(squidHelmet = new SquidHelmet());
		itemAddQue(pigZombieChestplate = new PigZombieChestsplate());
		itemAddQue(cowLeggings = new CowLeggings());
		itemAddQue(chickenBoots = new ChickenBoots());
		itemAddQue(chaosRod = new ChaosRod());
		voidCrystal = new BasicItem("voidCrystal");
	}

	@SideOnly(Side.CLIENT)
	public static void initModels(){
		// Any items that need models initialized without metadata other than 0,
		// add it to modelQue. If it has metadata, add it manually.

		for(Item modeling : modelQue){
			register(modeling, 0);
		}
	}

	@SideOnly(Side.CLIENT)
	private static void register(Item item, int subtype){
		ModelLoader.setCustomModelResourceLocation(item, subtype, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
}