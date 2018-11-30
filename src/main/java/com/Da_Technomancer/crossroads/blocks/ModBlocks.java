package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.blocks.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.fluid.*;
import com.Da_Technomancer.crossroads.blocks.heat.*;
import com.Da_Technomancer.crossroads.blocks.beams.*;
import com.Da_Technomancer.crossroads.blocks.rotary.*;
import com.Da_Technomancer.crossroads.blocks.technomancy.*;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

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
	public static BeamExtractor beamExtractor;
	public static QuartzStabilizer quartzStabilizer;
	public static CrystallinePrism crystallinePrism;
	public static BeamReflector beamReflector;
	public static LensFrame lensFrame;
	public static BasicBlock blockPureQuartz;
	public static BasicBlock blockLuminescentQuartz;
	public static BeamSiphon beamSiphon;
	public static ColorChart colorChart;
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
	public static AlchemicalTube alchemicalTubeGlass;
	public static RedsAlchemicalTube redsAlchemicalTubeGlass;
	public static FluidInjector fluidInjectorGlass;
	public static FlowLimiter flowLimiterGlass;
	public static HeatedTube heatedTubeGlass;
	public static CoolingCoil coolingCoilGlass;
	public static ReactionChamber reactionChamberGlass;
	public static ReagentTank reagentTankGlass;
	public static ReagentPump reagentPumpGlass;
	public static AlchemicalTube alchemicalTubeCrystal;
	public static RedsAlchemicalTube redsAlchemicalTubeCrystal;
	public static FluidInjector fluidInjectorCrystal;
	public static FlowLimiter flowLimiterCrystal;
	public static HeatedTube heatedTubeCrystal;
	public static CoolingCoil coolingCoilCrystal;
	public static ReactionChamber reactionChamberCrystal;
	public static ReagentTank reagentTankCrystal;
	public static ReagentPump reagentPumpCrystal;
	public static ChemicalVent chemicalVent;
	public static HeatLimiter heatLimiter;
	public static Dynamo dynamo;
	public static TeslaCoil teslaCoil;
	public static TeslaCoilTop teslaCoilTopNormal;
	public static TeslaCoilTop teslaCoilTopDistance;
	public static TeslaCoilTop teslaCoilTopIntensity;
	public static TeslaCoilTop teslaCoilTopAttack;
	public static TeslaCoilTop teslaCoilTopEfficiency;
	public static MaxwellDemon maxwellDemon;
	public static GlasswareHolder glasswareHolder;
	public static Alembic alembic;
	public static AlchemyChart alchemyChart;
	public static DensusPlate densusPlate;
	public static DensusPlate antiDensusPlate;
	public static BasicBlock cavorite;
	public static ChargingStand chargingStand;
	public static AtmosCharger atmosCharger;
	public static ReactiveSpot reactiveSpot;
	public static ClockworkStabilizer clockworkStabilizer;
	public static WindTurbine windTurbine;
	public static SolarHeater solarHeater;
	public static HeatReservoir heatReservoir;
	public static StirlingEngine stirlingEngine;
	public static Icebox icebox;
	public static StampMill stampMill;
	public static StampMillTop stampMillTop;
	public static OreCleanser oreCleanser;
	public static BlastFurnace blastFurnace;
	public static BeamRedirector beamRedirector;
	public static PermeableGlass permeableGlass;
	public static PermeableQuartz permeableQuartz;
	public static RedstoneTransmitter redstoneTransmitter;
	public static RedstoneReceiver redstoneReceiver;

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
		beamExtractor = new BeamExtractor();
		quartzStabilizer = new QuartzStabilizer();
		crystallinePrism = new CrystallinePrism();
		beamReflector = new BeamReflector();
		lensFrame = new LensFrame();
		blockPureQuartz = new BasicBlock("block_pure_quartz", Material.ROCK, 4, "blockQuartz");
		blockLuminescentQuartz = (BasicBlock) new BasicBlock("block_luminescent_quartz", Material.ROCK, 4, "blockQuartz").setLightLevel(1F);
		beamSiphon = new BeamSiphon();
		colorChart = new ColorChart();
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
		alchemicalTubeGlass = new AlchemicalTube(false);
		redsAlchemicalTubeGlass = new RedsAlchemicalTube(false);
		fluidInjectorGlass = new FluidInjector(false);
		flowLimiterGlass = new FlowLimiter(false);
		heatedTubeGlass = new HeatedTube(false);
		coolingCoilGlass = new CoolingCoil(false);
		reactionChamberGlass = new ReactionChamber(false);
		reagentPumpGlass = new ReagentPump(false);
		reagentTankGlass = new ReagentTank(false);
		alchemicalTubeCrystal = new AlchemicalTube(true);
		redsAlchemicalTubeCrystal = new RedsAlchemicalTube(true);
		fluidInjectorCrystal = new FluidInjector(true);
		flowLimiterCrystal = new FlowLimiter(true);
		heatedTubeCrystal = new HeatedTube(true);
		coolingCoilCrystal = new CoolingCoil(true);
		reactionChamberCrystal = new ReactionChamber(true);
		reagentPumpCrystal = new ReagentPump(true);
		reagentTankCrystal = new ReagentTank(true);
		chemicalVent = new ChemicalVent();
		heatLimiter = new HeatLimiter();
		dynamo = new Dynamo();
		teslaCoil = new TeslaCoil();
		teslaCoilTopNormal = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.NORMAL);
		teslaCoilTopDistance = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.DISTANCE);
		teslaCoilTopIntensity = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.INTENSITY);
		teslaCoilTopAttack = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.ATTACK);
		teslaCoilTopEfficiency = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.EFFICIENCY);
		maxwellDemon = new MaxwellDemon();
		glasswareHolder = new GlasswareHolder();
		alembic = new Alembic();
		alchemyChart = new AlchemyChart();
		densusPlate = new DensusPlate(false);
		antiDensusPlate = new DensusPlate(true);
		cavorite = new BasicBlock("block_cavorite", Material.ROCK, 3){
			@Override
			public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn){
				tooltip.add("Blocks gravity from gravity plates");
				tooltip.add("Safe for decoration");
			}
		};
		chargingStand = new ChargingStand();
		atmosCharger = new AtmosCharger();
		reactiveSpot = new ReactiveSpot();
		clockworkStabilizer = new ClockworkStabilizer();
		windTurbine = new WindTurbine();
		solarHeater = new SolarHeater();
		heatReservoir = new HeatReservoir();
		stirlingEngine = new StirlingEngine();
		icebox = new Icebox();
		stampMill = new StampMill();
		stampMillTop = new StampMillTop();
		oreCleanser = new OreCleanser();
		blastFurnace = new BlastFurnace();
		beamRedirector = new BeamRedirector();
		permeableGlass = new PermeableGlass();
		permeableQuartz = new PermeableQuartz();
		redstoneTransmitter = new RedstoneTransmitter();
		redstoneReceiver = new RedstoneReceiver();
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
		alchemicalTubeGlass.initModel();
		redsAlchemicalTubeGlass.initModel();
		alchemicalTubeCrystal.initModel();
		redsAlchemicalTubeCrystal.initModel();
		glasswareHolder.initModel();
		chargingStand.initModel();
		atmosCharger.initModel();
	}
}
