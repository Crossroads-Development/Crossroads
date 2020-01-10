package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.items.alchemy.*;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.technomancy.*;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public final class CRItems{

	public static final ItemGroup TAB_CROSSROADS = new ItemGroup(Crossroads.MODID){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(omnimeter, 1);
		}
	};

	public static final ItemGroup TAB_HEAT_CABLE = new ItemGroup("heat_cable"){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(HeatCableFactory.HEAT_CABLES.get(HeatInsulators.WOOL), 1);
		}
	};

	public static final ItemGroup TAB_GEAR = new ItemGroup("gear"){
		@Override
		public ItemStack createIcon(){
			return new ItemStack(GearFactory.gearTypes.get(GearFactory.findMaterial("Copper")).getSmallGear(), 1);
		}
	};

	public static final Item.Properties itemProp = new Item.Properties().group(TAB_CROSSROADS);
	
	public static CheatWandRotary debugGearWriter;
	public static CheatWandHeat debugHeatWriter;
	public static Item dustSalt;
//	public static MashedPotato mashedPotato;
	public static HandCrank handCrank;
	public static OmniMeter omnimeter;
	public static Vacuum vacuum;
	public static MagentaBread magentaBread;
	public static EdibleBlob edibleBlob;
	public static RainIdol rainIdol;
	public static Item pureQuartz;
	public static Item luminescentQuartz;
	public static Item lensArray;
	public static SquidHelmet squidHelmet;
	public static PigZombieChestsplate pigZombieChestplate;
	public static CowLeggings cowLeggings;
	public static ChickenBoots chickenBoots;
	public static ChaosRod chaosRod;
	public static ModuleGoggles moduleGoggles;
	public static StaffTechnomancy staffTechnomancy;
	public static BeamCage beamCage;
//	public static PrototypePistol pistol;
//	public static PrototypeWatch watch;
	public static Item adamant;
	public static Item sulfur;
	public static Item vanadiumOxide;
	public static PhilStone philosopherStone;
	public static PhilStone practitionerStone;
	public static Item alchCrystal;
	public static Item alchemySalt;
	public static Phial phialGlass;
	public static FlorenceFlask florenceFlaskGlass;
	public static Shell shellGlass;
	public static Phial phialCrystal;
	public static FlorenceFlask florenceFlaskCrystal;
	public static Shell shellCrystal;
	public static LiechWrench liechWrench;
	public static LeydenJar leydenJar;
	public static Nitroglycerin nitroglycerin;
	public static PoisonVodka poisonVodka;
	public static DoublePoisonVodka doublePoisonVodka;
	public static Item solidQuicksilver;
	public static Item solidFusas;
	public static Item solidEldrine;
	public static Item solidStasisol;
	public static Item solidDensus;
	public static Item solidAntiDensus;
	public static Item solidFortis;
	public static Item solidVitriol;
	public static Item solidMuriatic;
	public static Item solidRegia;
	public static Item solidCavorite;
	public static Item solidChlorine;
	public static Item solidSO2;
	public static Item bedrockDust;
	public static FlyingMachine flyingMachine;
	public static TeslaRay teslaRay;
	public static Slag slag;
	//	public static LinkingTool linkingTool;
	public static DampingPowder dampingPowder;

	public static final ArrayList<Item> toRegister = new ArrayList<>();

	public static void init(){
		debugGearWriter = new CheatWandRotary();
		handCrank = new HandCrank();
		debugHeatWriter = new CheatWandHeat();
		toRegister.add(dustSalt = new Item(itemProp).setRegistryName("dust_salt"));
//		mashedPotato = new MashedPotato();
		omnimeter = new OmniMeter();
		vacuum = new Vacuum();
		magentaBread = new MagentaBread();
		edibleBlob = new EdibleBlob();
		rainIdol = new RainIdol();
		toRegister.add(pureQuartz = new Item(itemProp).setRegistryName("pure_quartz"));
		toRegister.add(luminescentQuartz = new Item(itemProp).setRegistryName("luminescent_quartz"));
		toRegister.add(lensArray = new Item(itemProp).setRegistryName("lens_array"));
		squidHelmet = new SquidHelmet();
		pigZombieChestplate = new PigZombieChestsplate();
		cowLeggings = new CowLeggings();
		chickenBoots = new ChickenBoots();
		chaosRod = new ChaosRod();
		moduleGoggles = new ModuleGoggles();
		staffTechnomancy = new StaffTechnomancy();
		beamCage = new BeamCage();
//		pistol = new PrototypePistol();
//		watch = new PrototypeWatch();
		toRegister.add(adamant = new Item(itemProp).setRegistryName("adamant"));
		toRegister.add(sulfur = new Item(itemProp).setRegistryName("sulfur"));
		toRegister.add(vanadiumOxide = new Item(itemProp).setRegistryName("vanadium_oxide"));
		philosopherStone = new PhilStone(false);
		practitionerStone = new PhilStone(true);
		toRegister.add(alchCrystal = new Item(itemProp).setRegistryName("alch_crystal"));
		toRegister.add(alchemySalt = new Item(itemProp).setRegistryName("waste_salt"));
		phialGlass = new Phial(false);
		florenceFlaskGlass = new FlorenceFlask(false);
		shellGlass = new Shell(false);
		phialCrystal = new Phial(true);
		florenceFlaskCrystal = new FlorenceFlask(true);
		shellCrystal = new Shell(true);
		liechWrench = new LiechWrench();
		leydenJar = new LeydenJar();
		nitroglycerin = new Nitroglycerin();
		poisonVodka = new PoisonVodka();
		doublePoisonVodka = new DoublePoisonVodka();
		toRegister.add(solidQuicksilver = new Item(itemProp).setRegistryName("solid_quicksilver"));
		toRegister.add(solidFusas = new Item(itemProp).setRegistryName("solid_fusas"));
		toRegister.add(solidEldrine = new Item(itemProp).setRegistryName("solid_eldrine"));
		toRegister.add(solidStasisol = new Item(itemProp).setRegistryName("solid_stasisol"));
		toRegister.add(solidDensus = new Item(itemProp).setRegistryName("solid_densus"));
		toRegister.add(solidAntiDensus = new Item(itemProp).setRegistryName("solid_anti_densus"));
		toRegister.add(solidFortis = new Item(itemProp).setRegistryName("solid_fortis"));
		toRegister.add(solidVitriol = new Item(itemProp).setRegistryName("solid_vitriol"));
		toRegister.add(solidMuriatic = new Item(itemProp).setRegistryName("solid_muriatic"));
		toRegister.add(solidRegia = new Item(itemProp).setRegistryName("solid_regia"));
		toRegister.add(solidCavorite = new Item(itemProp).setRegistryName("solid_cavorite"));
		toRegister.add(solidSO2 = new Item(itemProp).setRegistryName("solid_sulfur_dioxide"));
		toRegister.add(solidChlorine = new Item(itemProp).setRegistryName("solid_chlorine"));
		toRegister.add(bedrockDust = new Item(itemProp).setRegistryName("dust_bedrock"));
		flyingMachine = new FlyingMachine();
		teslaRay = new TeslaRay();
		slag = new Slag();
//		linkingTool = new LinkingTool();
		dampingPowder = new DampingPowder();
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
		Minecraft.getInstance().getItemColors().register((ItemStack stack, int layer) -> layer == 0 ? AbstractGlassware.getColorRGB(stack) : -1, phialGlass, florenceFlaskGlass, shellGlass, phialCrystal, florenceFlaskCrystal, shellCrystal);
	}
}