package com.Da_Technomancer.crossroads;

import org.apache.logging.log4j.Logger;

import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.command.WorkspaceDimTeleport;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import com.Da_Technomancer.crossroads.items.itemSets.OreSetUp;

import net.minecraft.item.Item;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;

@Mod(modid = Main.MODID, name = Main.MODNAME, version = Main.VERSION, dependencies = "required-after:forge@[13.20.0.2271,]; before:guideapi; after:jei", useMetadata = true)
public final class Main{

	public static final String MODID = "crossroads";
	public static final String MODNAME = "Crossroads";
	public static final String VERSION = "gradVERSION";

	static{
		FluidRegistry.enableUniversalBucket();
	}

	public static Logger logger;

	/**
	 * I am fully aware that this takes up a huge amount of space. It will be removed eventually. 
	 */
	@Mod.EventHandler
	public void onMissingMapping(FMLMissingMappingsEvent event) {
		for (FMLMissingMappingsEvent.MissingMapping mapping : event.get()) {
			logger.info("Repairing missing mapping: " + mapping.resourceLocation.toString());
			switch(mapping.type){
				case BLOCK:
					switch(mapping.resourceLocation.getResourcePath().toLowerCase()){
						case("blocksalt"):
							mapping.remap(ModBlocks.blockSalt);
						break;
						case("candlelilypad"):
							mapping.remap(ModBlocks.candleLilyPad);
						break;
						case("fertilesoil"):
							mapping.remap(ModBlocks.fertileSoil);
						break;
						case("itemchute"):
							mapping.remap(ModBlocks.itemChute);
						break;
						case("blockpurequartz"):
							mapping.remap(ModBlocks.blockPureQuartz);
						break;
						case("multipiston"):
							mapping.remap(ModBlocks.multiPiston);
						break;
						case("multipistonsticky"):
							mapping.remap(ModBlocks.multiPistonSticky);
						break;
						case("multipistonextend"):
							mapping.remap(ModBlocks.multiPistonExtend);
						break;
						case("multipistonextendsticky"):
							mapping.remap(ModBlocks.multiPistonExtendSticky);
						break;
						case("slottedchest"):
							mapping.remap(ModBlocks.slottedChest);
						break;
						case("sortinghopper"):
							mapping.remap(ModBlocks.sortingHopper);
						break;
						case("fatcollector"):
							mapping.remap(ModBlocks.fatCollector);
						break;
						case("fatcongealer"):
							mapping.remap(ModBlocks.fatCongealer);
						break;
						case("fatfeeder"):
							mapping.remap(ModBlocks.fatFeeder);
						break;
						case("fluidtank"):
							mapping.remap(ModBlocks.fluidTank);
						break;
						case("fluidtube"):
							mapping.remap(ModBlocks.fluidTube);
						break;
						case("fluidvoid"):
							mapping.remap(ModBlocks.fluidVoid);
						break;
						case("redstonefluidtube"):
							mapping.remap(ModBlocks.redstoneFluidTube);
						break;
						case("rotarypump"):
							mapping.remap(ModBlocks.rotaryPump);
						break;
						case("steamboiler"):
							mapping.remap(ModBlocks.steamBoiler);
						break;
						case("steamturbine"):
							mapping.remap(ModBlocks.steamTurbine);
						break;
						case("watercentrifuge"):
							mapping.remap(ModBlocks.waterCentrifuge);
						break;
						case("coalheater"):
							mapping.remap(ModBlocks.coalHeater);
						break;
						case("fluidcoolingchamber"):
							mapping.remap(ModBlocks.fluidCoolingChamber);
						break;
						case("insulatedheatexchanger"):
							mapping.remap(ModBlocks.insulHeatExchanger);
						break;
						case("heatexchanger"):
							mapping.remap(ModBlocks.heatExchanger);
						break;
						case("heatingchamber"):
							mapping.remap(ModBlocks.heatingChamber);
						break;
						case("heatingcrucible"):
							mapping.remap(ModBlocks.heatingCrucible);
						break;
						case("saltreactor"):
							mapping.remap(ModBlocks.saltReactor);
						break;
						case("arcaneextractor"):
							mapping.remap(ModBlocks.arcaneExtractor);
						break;
						case("arcanereflector"):
							mapping.remap(ModBlocks.arcaneReflector);
						break;
						case("beaconharness"):
							mapping.remap(ModBlocks.beaconHarness);
						break;
						case("beamsplitter"):
							mapping.remap(ModBlocks.beamSplitter);
						break;
						case("beamsplitterbasic"):
							mapping.remap(ModBlocks.beamSplitterBasic);
						break;
						case("colorchart"):
							mapping.remap(ModBlocks.colorChart);
						break;
						case("crystallineprism"):
							mapping.remap(ModBlocks.crystallinePrism);
						break;
						case("masteraxiscrystal"):
							mapping.remap(ModBlocks.crystalMasterAxis);
						break;
						case("lensholder"):
							mapping.remap(ModBlocks.lensHolder);
						break;
						case("largequartzstabilizer"):
							mapping.remap(ModBlocks.largeQuartzStabilizer);
						break;
						case("smallquartzstabilizer"):
							mapping.remap(ModBlocks.smallQuartzStabilizer);
						break;
						case("itemchuteport"):
							mapping.remap(ModBlocks.itemChutePort);
						break;
						case("largegearmaster"):
							mapping.remap(ModBlocks.largeGearMaster);
						break;
						case("largegearslave"):
							mapping.remap(ModBlocks.largeGearSlave);
						break;
						case("masteraxis"):
							mapping.remap(ModBlocks.masterAxis);
						break;
						case("rotarydrill"):
							mapping.remap(ModBlocks.rotaryDrill);
						break;
						case("sidedgearholder"):
							mapping.remap(ModBlocks.sidedGearHolder);
						break;
						case("togglegeariron"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.IRON));
						break;
						case("togglegeargold"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.GOLD));
						break;
						case("togglegearcopper"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.COPPER));
						break;
						case("togglegeartin"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.TIN));
						break;
						case("togglegearbronze"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.BRONZE));
						break;
						case("togglegearcopshowium"):
							mapping.remap(GearFactory.TOGGLE_GEARS.get(GearTypes.COPSHOWIUM));
						break;
						case("additionaxis"):
							mapping.remap(ModBlocks.additionAxis);
						break;
						case("chunkunlocker"):
							mapping.remap(ModBlocks.chunkUnlocker);
						break;
						case("equalsaxis"):
							mapping.remap(ModBlocks.equalsAxis);
						break;
						case("fluxmanipulator"):
							mapping.remap(ModBlocks.fluxManipulator);
						break;
						case("fluxreaderaxis"):
							mapping.remap(ModBlocks.fluxReaderAxis);
						break;
						case("greaterthanaxis"):
							mapping.remap(ModBlocks.greaterThanAxis);
						break;
						case("lessthanaxis"):
							mapping.remap(ModBlocks.lessThanAxis);
						break;
						case("mechanicalbeamsplitter"):
							mapping.remap(ModBlocks.mechanicalBeamSplitter);
						break;
						case("multiplicationaxis"):
							mapping.remap(ModBlocks.multiplicationAxis);
						break;
						case("ratemanipulator"):
							mapping.remap(ModBlocks.rateManipulator);
						break;
						case("redstoneaxis"):
							mapping.remap(ModBlocks.redstoneAxis);
						break;
						case("squarerootaxis"):
							mapping.remap(ModBlocks.squareRootAxis);
						break;
						case("blockcopper"):
							mapping.remap(OreSetUp.blockCopper);
						break;
						case("blocktin"):
							mapping.remap(OreSetUp.blockTin);
						break;
						case("blockbronze"):
							mapping.remap(OreSetUp.blockBronze);
						break;
						case("blockruby"):
							mapping.remap(OreSetUp.blockRuby);
						break;
						case("orecopper"):
							mapping.remap(OreSetUp.oreCopper);
						break;
						case("oretin"):
							mapping.remap(OreSetUp.oreTin);
						break;
						case("orenativecopper"):
							mapping.remap(OreSetUp.oreNativeCopper);
						break;
						case("oreruby"):
							mapping.remap(OreSetUp.oreRuby);
						break;

						default:
							String oldMap = mapping.resourceLocation.getResourcePath().toLowerCase();
							if(oldMap.contains("redstoneheatcable")){
								mapping.remap(HeatCableFactory.REDSTONE_HEAT_CABLES.get(oldMap.contains("copper") ? HeatConductors.COPPER : oldMap.contains("iron") ? HeatConductors.IRON : oldMap.contains("quartz") ? HeatConductors.QUARTZ : HeatConductors.DIAMOND).get(oldMap.contains("wool") ? HeatInsulators.WOOL : oldMap.contains("slime") ? HeatInsulators.SLIME : oldMap.contains("dirt") ? HeatInsulators.DIRT : oldMap.contains("ice") ? HeatInsulators.ICE : HeatInsulators.OBSIDIAN));
							}else if(oldMap.contains("heatcable")){
								mapping.remap(HeatCableFactory.HEAT_CABLES.get(oldMap.contains("copper") ? HeatConductors.COPPER : oldMap.contains("iron") ? HeatConductors.IRON : oldMap.contains("quartz") ? HeatConductors.QUARTZ : HeatConductors.DIAMOND).get(oldMap.contains("wool") ? HeatInsulators.WOOL : oldMap.contains("slime") ? HeatInsulators.SLIME : oldMap.contains("dirt") ? HeatInsulators.DIRT : oldMap.contains("ice") ? HeatInsulators.ICE : HeatInsulators.OBSIDIAN));
							}
							break;
					}
					break;
				case ITEM:
					switch(mapping.resourceLocation.getResourcePath().toLowerCase()){
						case("blocksalt"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.blockSalt));
						break;
						case("candlelilypad"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.candleLilyPad));
						break;
						case("fertilesoil"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fertileSoil));
						break;
						case("itemchute"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.itemChute));
						break;
						case("blockpurequartz"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.blockPureQuartz));
						break;
						case("multipiston"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.multiPiston));
						break;
						case("multipistonsticky"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.multiPistonSticky));
						break;
						case("multipistonextend"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.multiPistonExtend));
						break;
						case("multipistonextendsticky"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.multiPistonExtendSticky));
						break;
						case("slottedchest"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.slottedChest));
						break;
						case("sortinghopper"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.sortingHopper));
						break;
						case("fatcollector"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fatCollector));
						break;
						case("fatcongealer"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fatCongealer));
						break;
						case("fatfeeder"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fatFeeder));
						break;
						case("fluidtank"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluidTank));
						break;
						case("fluidtube"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluidTube));
						break;
						case("fluidvoid"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluidVoid));
						break;
						case("redstonefluidtube"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.redstoneFluidTube));
						break;
						case("rotarypump"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.rotaryPump));
						break;
						case("steamboiler"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.steamBoiler));
						break;
						case("steamturbine"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.steamTurbine));
						break;
						case("watercentrifuge"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.waterCentrifuge));
						break;
						case("coalheater"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.coalHeater));
						break;
						case("fluidcoolingchamber"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluidCoolingChamber));
						break;
						case("insulatedheatexchanger"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.insulHeatExchanger));
						break;
						case("heatexchanger"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.heatExchanger));
						break;
						case("heatingchamber"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.heatingChamber));
						break;
						case("heatingcrucible"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.heatingCrucible));
						break;
						case("saltreactor"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.saltReactor));
						break;
						case("arcaneextractor"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.arcaneExtractor));
						break;
						case("arcanereflector"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.arcaneReflector));
						break;
						case("beaconharness"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.beaconHarness));
						break;
						case("beamsplitter"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.beamSplitter));
						break;
						case("beamsplitterbasic"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.beamSplitterBasic));
						break;
						case("colorchart"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.colorChart));
						break;
						case("crystallineprism"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.crystallinePrism));
						break;
						case("masteraxiscrystal"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.crystalMasterAxis));
						break;
						case("lensholder"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.lensHolder));
						break;
						case("largequartzstabilizer"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.largeQuartzStabilizer));
						break;
						case("smallquartzstabilizer"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.smallQuartzStabilizer));
						break;
						case("itemchuteport"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.itemChutePort));
						break;
						case("masteraxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.masterAxis));
						break;
						case("rotarydrill"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.rotaryDrill));
						break;
						case("togglegeariron"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.IRON)));
						break;
						case("togglegeargold"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.GOLD)));
						break;
						case("togglegearcopper"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.COPPER)));
						break;
						case("togglegeartin"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.TIN)));
						break;
						case("togglegearbronze"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.BRONZE)));
						break;
						case("togglegearcopshowium"):
							mapping.remap(Item.getItemFromBlock(GearFactory.TOGGLE_GEARS.get(GearTypes.COPSHOWIUM)));
						break;
						case("additionaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.additionAxis));
						break;
						case("chunkunlocker"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.chunkUnlocker));
						break;
						case("equalsaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.equalsAxis));
						break;
						case("fluxmanipulator"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluxManipulator));
						break;
						case("fluxreaderaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.fluxReaderAxis));
						break;
						case("greaterthanaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.greaterThanAxis));
						break;
						case("lessthanaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.lessThanAxis));
						break;
						case("mechanicalbeamsplitter"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.mechanicalBeamSplitter));
						break;
						case("multiplicationaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.multiplicationAxis));
						break;
						case("ratemanipulator"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.rateManipulator));
						break;
						case("redstoneaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.redstoneAxis));
						break;
						case("squarerootaxis"):
							mapping.remap(Item.getItemFromBlock(ModBlocks.squareRootAxis));
						break;
						case("blockcopper"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.blockCopper));
						break;
						case("blocktin"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.blockTin));
						break;
						case("blockbronze"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.blockBronze));
						break;
						case("blockruby"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.blockRuby));
						break;
						case("orecopper"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.oreCopper));
						break;
						case("oretin"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.oreTin));
						break;
						case("orenativecopper"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.oreNativeCopper));
						break;
						case("oreruby"):
							mapping.remap(Item.getItemFromBlock(OreSetUp.oreRuby));
						break;
						case("ingotcopper"):
							mapping.remap(OreSetUp.ingotCopper);
						break;
						case("ingottin"):
							mapping.remap(OreSetUp.ingotTin);
						break;
						case("ingotbronze"):
							mapping.remap(OreSetUp.ingotBronze);
						break;
						case("gemruby"):
							mapping.remap(OreSetUp.gemRuby);
						break;
						case("nuggetcopper"):
							mapping.remap(OreSetUp.nuggetCopper);
						break;
						case("nuggettin"):
							mapping.remap(OreSetUp.nuggetTin);
						break;
						case("nuggetbronze"):
							mapping.remap(OreSetUp.nuggetBronze);
						break;
						case("geariron"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.IRON));
						break;
						case("geargold"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.GOLD));
						break;
						case("gearcopper"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.COPPER));
						break;
						case("geartin"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.TIN));
						break;
						case("gearbronze"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.BRONZE));
						break;
						case("gearcopshowium"):
							mapping.remap(GearFactory.BASIC_GEARS.get(GearTypes.COPSHOWIUM));
						break;
						case("largegeariron"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.IRON));
						break;
						case("largegeargold"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.GOLD));
						break;
						case("largegearcopper"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.COPPER));
						break;
						case("largegeartin"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.TIN));
						break;
						case("largegearbronze"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.BRONZE));
						break;
						case("largegearcopshowium"):
							mapping.remap(GearFactory.LARGE_GEARS.get(GearTypes.COPSHOWIUM));
						break;
						case("debuggearwriter"):
							mapping.remap(ModItems.debugGearWriter);
						break;
						case("debugheatwriter"):
							mapping.remap(ModItems.debugHeatWriter);
						break;
						case("handcrank"):
							mapping.remap(ModItems.handCrank);
						break;
						case("dustcopper"):
							mapping.remap(ModItems.dustCopper);
						break;
						case("dustsalt"):
							mapping.remap(ModItems.dustSalt);
						break;
						case("obsidiancuttingkit"):
							mapping.remap(ModItems.obsidianKit);
						break;
						case("mashedpotato"):
							mapping.remap(ModItems.mashedPotato);
						break;
						case("fluidgauge"):
							mapping.remap(ModItems.fluidGauge);
						break;
						case("magentabread"):
							mapping.remap(ModItems.magentaBread);
						break;
						case("edibleblob"):
							mapping.remap(ModItems.edibleBlob);
						break;
						case("diamondwire"):
							mapping.remap(ModItems.diamondWire);
						break;
						case("rainidol"):
							mapping.remap(ModItems.rainIdol);
						break;
						case("purequartz"):
							mapping.remap(ModItems.pureQuartz);
						break;
						case("luminescentquartz"):
							mapping.remap(ModItems.luminescentQuartz);
						break;
						case("lensarray"):
							mapping.remap(ModItems.lensArray);
						break;
						case("invisitem"):
							mapping.remap(ModItems.invisItem);
						break;
						case("squidhelmet"):
							mapping.remap(ModItems.squidHelmet);
						break;
						case("pigzombiechestplate"):
							mapping.remap(ModItems.pigZombieChestplate);
						break;
						case("cowleggings"):
							mapping.remap(ModItems.cowLeggings);
						break;
						case("chickenboots"):
							mapping.remap(ModItems.chickenBoots);
						break;
						case("chaosrod"):
							mapping.remap(ModItems.chaosRod);
						break;
						case("voidcrystal"):
							mapping.remap(ModItems.voidCrystal);
						break;
						case("modulegoggles"):
							mapping.remap(ModItems.moduleGoggles);
						break;
						case("stafftechnomancy"):
							mapping.remap(ModItems.staffTechnomancy);
						break;

						default:
							String oldMap = mapping.resourceLocation.getResourcePath().toLowerCase();
							if(oldMap.contains("redstoneheatcable")){
								mapping.remap(Item.getItemFromBlock(HeatCableFactory.REDSTONE_HEAT_CABLES.get(oldMap.contains("copper") ? HeatConductors.COPPER : oldMap.contains("iron") ? HeatConductors.IRON : oldMap.contains("quartz") ? HeatConductors.QUARTZ : HeatConductors.DIAMOND).get(oldMap.contains("wool") ? HeatInsulators.WOOL : oldMap.contains("slime") ? HeatInsulators.SLIME : oldMap.contains("dirt") ? HeatInsulators.DIRT : oldMap.contains("ice") ? HeatInsulators.ICE : HeatInsulators.OBSIDIAN)));
							}else if(oldMap.contains("heatcable")){
								mapping.remap(Item.getItemFromBlock(HeatCableFactory.HEAT_CABLES.get(oldMap.contains("copper") ? HeatConductors.COPPER : oldMap.contains("iron") ? HeatConductors.IRON : oldMap.contains("quartz") ? HeatConductors.QUARTZ : HeatConductors.DIAMOND).get(oldMap.contains("wool") ? HeatInsulators.WOOL : oldMap.contains("slime") ? HeatInsulators.SLIME : oldMap.contains("dirt") ? HeatInsulators.DIRT : oldMap.contains("ice") ? HeatInsulators.ICE : HeatInsulators.OBSIDIAN)));
							}
							break;
					}
					break;
			}
		}
	}

	@SidedProxy(clientSide = "com.Da_Technomancer.crossroads.ClientProxy", serverSide = "com.Da_Technomancer.crossroads.ServerProxy")
	public static CommonProxy proxy;

	@Mod.Instance
	public static Main instance;

	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event){
		logger = event.getModLog();
		proxy.preInit(event);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e){
		proxy.init(e);
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent e){
		proxy.postInit(e);
	}

	@Mod.EventHandler
	public void serverLoading(FMLServerStartingEvent e){
		e.registerServerCommand(new WorkspaceDimTeleport());
	}

	@Mod.EventHandler
	public void serverStarted(FMLServerStartedEvent e){
		CommonProxy.masterKey = 1;
		ModDimensions.loadDims();
		
		//For singleplayer.
		ModConfig.syncPropNBT = ModConfig.nbtToSyncConfig();
	}

	@Mod.EventHandler
	public void serverEnded(FMLServerStoppingEvent e){
		ForgeChunkManager.releaseTicket(EventHandlerCommon.loadingTicket);
		EventHandlerCommon.loadingTicket = null;
	}
}