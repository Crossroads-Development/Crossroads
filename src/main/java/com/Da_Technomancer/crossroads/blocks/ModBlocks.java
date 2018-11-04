package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.blocks.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.fluid.*;
import com.Da_Technomancer.crossroads.blocks.heat.*;
import com.Da_Technomancer.crossroads.blocks.magic.*;
import com.Da_Technomancer.crossroads.blocks.rotary.*;
import com.Da_Technomancer.crossroads.blocks.technomancy.*;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class ModBlocks{

	public static Mechanism sextupleGear;
	public static MasterAxis masterAxis;
	public static FluidTube fluidTube;
	public static HeatingCrucible heatingCrucible;
	public static Millstone millstone;
	public static SteamBoiler steamBoiler;
	public static BlockSalt blockSalt;
	public static FluidVoid fluidVoid;
	public static RotaryPump rotaryPump;
	public static SteamTurbine steamTurbine;
	public static HeatSink heatSink;
	public static FluidTank fluidTank;
	public static Firebox firebox;
	public static Smelter smelter;
	public static SaltReactor saltReactor;
	public static FluidCoolingChamber fluidCoolingChamber;
	public static LargeGearMaster largeGearMaster;
	public static LargeGearSlave largeGearSlave;
	public static Radiator radiator;
	public static RotaryDrill rotaryDrill;
	public static RotaryDrill rotaryDrillGold;
	public static FatCollector fatCollector;
	public static FatCongealer fatCongealer;
	public static RedstoneFluidTube redstoneFluidTube;
	public static WaterCentrifuge waterCentrifuge;
	public static ArcaneExtractor arcaneExtractor;
	public static QuartzStabilizer smallQuartzStabilizer;
	public static QuartzStabilizer largeQuartzStabilizer;
	public static CrystallinePrism crystallinePrism;
	public static ArcaneReflector arcaneReflector;
	public static LensHolder lensHolder;
	public static BasicBlock blockPureQuartz;
	public static BasicBlock blockLuminescentQuartz;
	public static BeamSplitter beamSplitter;
	public static ColorChart colorChart;
	public static BeamSplitterBasic beamSplitterBasic;
	public static CrystalMasterAxis crystalMasterAxis;
	public static Ratiator ratiator;
	public static BeaconHarness beaconHarness;
	public static FatFeeder fatFeeder;
	public static ChunkUnlocker chunkUnlocker; 
	public static RateManipulator rateManipulator;
	public static FluxManipulator fluxManipulator;
	public static FluxReaderAxis fluxReaderAxis;
	public static MultiplicationAxis multiplicationAxis;
	public static AdditionAxis additionAxis;
	public static EqualsAxis equalsAxis;
	public static GreaterThanAxis greaterThanAxis;
	public static LessThanAxis lessThanAxis;
	public static RedstoneAxis redstoneAxis;
	public static SquareRootAxis squareRootAxis;
	public static MechanicalBeamSplitter mechanicalBeamSplitter;
	public static CageCharger cageCharger;
	public static HamsterWheel hamsterWheel;
	public static FluidSplitter fluidSplitter;
	public static BasicFluidSplitter basicFluidSplitter;
	public static CopshowiumCreationChamber copshowiumCreationChamber;
	public static SinAxis sinAxis;
	public static CosAxis cosAxis;
	public static ArcSinAxis arcsinAxis;
	public static ArcCosAxis arccosAxis;
	public static GatewayFrame gatewayFrame;
	public static RedstoneKeyboard redstoneKeyboard;
	public static DetailedCrafter detailedCrafter;
	public static PrototypingTable prototypingTable;
	public static Prototype prototype;
	public static PrototypePort prototypePort;
	public static MechanicalArm mechanicalArm;
	public static RedstoneRegistry redstoneRegistry;
	public static AlchemicalTube alchemicalTube;
	public static FluidInjector fluidInjector;
	public static FlowLimiter flowLimiter;
	public static HeatedTube heatedTube;
	public static CoolingCoil coolingCoil;
	public static ChemicalVent chemicalVent;
	public static ReactionChamber reactionChamber;
	public static HeatLimiter heatLimiter;
	public static Dynamo dynamo;
	public static TeslaCoil teslaCoil;
	public static TeslaCoilTop teslaCoilTop;
	public static ReagentTank reagentTank;
	public static ReagentPump reagentPump;
	public static MaxwellDemon maxwellDemon;
	public static GlasswareHolder glasswareHolder;
	public static RedsAlchemicalTube redsAlchemicalTube;
	public static Alembic alembic;
	public static AlchemyChart alchemyChart;
	public static DensusPlate densusPlate;
	public static ChargingStand chargingStand;
	public static AtmosCharger atmosCharger;
	public static ReactiveSpot reactiveSpot;
	public static ClockworkStabilizer clockworkStabilizer;
	public static WindTurbine windTurbine;
	public static SolarHeater solarHeater;
	public static HeatReservoir heatReservoir;
	public static StirlingEngine stirlingEngine;
	public static StampMill stampMill;
	public static Icebox icebox;

	public static final ArrayList<Block> toRegister = new ArrayList<Block>();
	
	/**
	 * Registers the item form of a block and the item model.
	 * @param block The block to register
	 * @return The passed block for convenience. 
	 */
	public static <T extends Block> T blockAddQue(T block){
		return blockAddQue(block, true);
	}
	
	/**
	 * Registers the item form of a block and an if registerModel item model.
	 * @param block The block to register
	 * @param registerModel whether to register a model.
	 * @return The passed block for convenience. 
	 */
	public static <T extends Block> T blockAddQue(T block, boolean registerModel){
		Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
		ModItems.toRegister.add(item);
		if(registerModel){
			ModItems.itemAddQue(item);
		}
		return block;
	}
	
	/**
	 * Registers the item form of a block and the item model. 
	 * @param block The block having an item model registered
	 * @param meta The meta value of the item.
	 * @param location The location of the model. 
	 * @return The block for convenience. 
	 */
	public static <T extends Block> T blockAddQue(T block, int meta, ModelResourceLocation location){
		Item item = new ItemBlock(block).setRegistryName(block.getRegistryName());
		ModItems.toRegister.add(item);
		ModItems.toClientRegister.put(Pair.of(item, meta), location);
		return block;
	}

	public static void init(){
		masterAxis = new MasterAxis();
		millstone = new Millstone();
		sextupleGear = new Mechanism();
		largeGearMaster = new LargeGearMaster();
		largeGearSlave = new LargeGearSlave();
		heatingCrucible = new HeatingCrucible();
		fluidTube = new FluidTube();
		steamBoiler = new SteamBoiler();
		rotaryPump = new RotaryPump();
		steamTurbine = new SteamTurbine();
		blockSalt = new BlockSalt();
		fluidVoid = new FluidVoid();
		heatSink = new HeatSink();
		fluidTank = new FluidTank();
		firebox = new Firebox();
		smelter = new Smelter();
		saltReactor = new SaltReactor();
		fluidCoolingChamber = new FluidCoolingChamber();
		radiator = new Radiator();
		rotaryDrill = new RotaryDrill(false);
		rotaryDrillGold = new RotaryDrill(true);
		fatCollector = new FatCollector();
		fatCongealer = new FatCongealer();
		redstoneFluidTube = new RedstoneFluidTube();
		waterCentrifuge = new WaterCentrifuge();
		arcaneExtractor = new ArcaneExtractor();
		smallQuartzStabilizer = new QuartzStabilizer(false);
		largeQuartzStabilizer = new QuartzStabilizer(true);
		crystallinePrism = new CrystallinePrism();
		arcaneReflector = new ArcaneReflector();
		lensHolder = new LensHolder();
		blockPureQuartz = new BasicBlock("block_pure_quartz", Material.ROCK, 1, "pickaxe", 4, null, "blockQuartz");
		blockLuminescentQuartz = (BasicBlock) new BasicBlock("block_luminescent_quartz", Material.ROCK, 1, "pickaxe", 4, null, "blockQuartz").setLightLevel(1F);
		beamSplitter = new BeamSplitter();
		colorChart = new ColorChart();
		beamSplitterBasic = new BeamSplitterBasic();
		crystalMasterAxis = new CrystalMasterAxis();
		ratiator = new Ratiator();
		beaconHarness = new BeaconHarness();
		fatFeeder = new FatFeeder();
		chunkUnlocker = new ChunkUnlocker();
		rateManipulator = new RateManipulator();
		fluxManipulator = new FluxManipulator();
		fluxReaderAxis = new FluxReaderAxis();
		multiplicationAxis = new MultiplicationAxis();
		additionAxis = new AdditionAxis();
		equalsAxis = new EqualsAxis();
		greaterThanAxis = new GreaterThanAxis();
		lessThanAxis = new LessThanAxis();
		redstoneAxis = new RedstoneAxis();
		squareRootAxis = new SquareRootAxis();
		mechanicalBeamSplitter = new MechanicalBeamSplitter();
		cageCharger = new CageCharger();
		hamsterWheel = new HamsterWheel();
		fluidSplitter = new FluidSplitter();
		basicFluidSplitter = new BasicFluidSplitter();
		copshowiumCreationChamber = new CopshowiumCreationChamber();
		sinAxis = new SinAxis();
		cosAxis = new CosAxis();
		arcsinAxis = new ArcSinAxis();
		arccosAxis = new ArcCosAxis();
		gatewayFrame = new GatewayFrame();
		redstoneKeyboard = new RedstoneKeyboard();
		detailedCrafter = new DetailedCrafter();
		prototypingTable = new PrototypingTable();
		prototype = new Prototype();
		prototypePort = new PrototypePort();
		mechanicalArm = new MechanicalArm();
		redstoneRegistry = new RedstoneRegistry();
		alchemicalTube = new AlchemicalTube();
		fluidInjector = new FluidInjector();
		flowLimiter = new FlowLimiter();
		heatedTube = new HeatedTube();
		coolingCoil = new CoolingCoil();
		chemicalVent = new ChemicalVent();
		reactionChamber = new ReactionChamber();
		heatLimiter = new HeatLimiter();
		dynamo = new Dynamo();
		teslaCoil = new TeslaCoil();
		teslaCoilTop = new TeslaCoilTop();
		reagentTank = new ReagentTank();
		reagentPump = new ReagentPump();
		maxwellDemon = new MaxwellDemon();
		glasswareHolder = new GlasswareHolder();
		redsAlchemicalTube = new RedsAlchemicalTube();
		alembic = new Alembic();
		alchemyChart = new AlchemyChart();
		densusPlate = new DensusPlate();
		chargingStand = new ChargingStand();
		atmosCharger = new AtmosCharger();
		reactiveSpot = new ReactiveSpot();
		clockworkStabilizer = new ClockworkStabilizer();
		windTurbine = new WindTurbine();
		solarHeater = new SolarHeater();
		heatReservoir = new HeatReservoir();
		stirlingEngine = new StirlingEngine();
		stampMill = new StampMill();
		icebox = new Icebox();
	}

	@SideOnly(Side.CLIENT)
	public static void initModels(){
		for(HeatCable cable : HeatCableFactory.HEAT_CABLES.values()){
			cable.initModel();
		}

		for(RedstoneHeatCable cable : HeatCableFactory.REDSTONE_HEAT_CABLES.values()){
			cable.initModel();
		}

		fluidTube.initModel();
		redstoneFluidTube.initModel();
		prototype.initModel();
		prototypePort.initModel();
		alchemicalTube.initModel();
		redsAlchemicalTube.initModel();
		glasswareHolder.initModel();
		chargingStand.initModel();
		atmosCharger.initModel();
	}
}
