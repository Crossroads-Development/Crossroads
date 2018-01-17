package com.Da_Technomancer.crossroads.blocks;

import java.util.ArrayList;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.blocks.alchemy.AlchemicalTube;
import com.Da_Technomancer.crossroads.blocks.alchemy.ChemicalVent;
import com.Da_Technomancer.crossroads.blocks.alchemy.CoolingCoil;
import com.Da_Technomancer.crossroads.blocks.alchemy.Dynamo;
import com.Da_Technomancer.crossroads.blocks.alchemy.FlorenceHolder;
import com.Da_Technomancer.crossroads.blocks.alchemy.FlowLimiter;
import com.Da_Technomancer.crossroads.blocks.alchemy.FluidInjector;
import com.Da_Technomancer.crossroads.blocks.alchemy.HeatLimiter;
import com.Da_Technomancer.crossroads.blocks.alchemy.HeatedTube;
import com.Da_Technomancer.crossroads.blocks.alchemy.MaxwellDemon;
import com.Da_Technomancer.crossroads.blocks.alchemy.PhialHolder;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReactionChamber;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentPump;
import com.Da_Technomancer.crossroads.blocks.alchemy.ReagentTank;
import com.Da_Technomancer.crossroads.blocks.alchemy.RedsAlchemicalTube;
import com.Da_Technomancer.crossroads.blocks.alchemy.TeslaCoil;
import com.Da_Technomancer.crossroads.blocks.alchemy.TeslaCoilTop;
import com.Da_Technomancer.crossroads.blocks.fluid.BasicFluidSplitter;
import com.Da_Technomancer.crossroads.blocks.fluid.FatCollector;
import com.Da_Technomancer.crossroads.blocks.fluid.FatCongealer;
import com.Da_Technomancer.crossroads.blocks.fluid.FatFeeder;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidSplitter;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidTank;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidTube;
import com.Da_Technomancer.crossroads.blocks.fluid.FluidVoid;
import com.Da_Technomancer.crossroads.blocks.fluid.Radiator;
import com.Da_Technomancer.crossroads.blocks.fluid.RedstoneFluidTube;
import com.Da_Technomancer.crossroads.blocks.fluid.RotaryPump;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamBoiler;
import com.Da_Technomancer.crossroads.blocks.fluid.SteamTurbine;
import com.Da_Technomancer.crossroads.blocks.fluid.WaterCentrifuge;
import com.Da_Technomancer.crossroads.blocks.heat.FluidCoolingChamber;
import com.Da_Technomancer.crossroads.blocks.heat.FuelHeater;
import com.Da_Technomancer.crossroads.blocks.heat.HeatCable;
import com.Da_Technomancer.crossroads.blocks.heat.HeatExchanger;
import com.Da_Technomancer.crossroads.blocks.heat.HeatingChamber;
import com.Da_Technomancer.crossroads.blocks.heat.HeatingCrucible;
import com.Da_Technomancer.crossroads.blocks.heat.RedstoneHeatCable;
import com.Da_Technomancer.crossroads.blocks.heat.SaltReactor;
import com.Da_Technomancer.crossroads.blocks.magic.ArcaneExtractor;
import com.Da_Technomancer.crossroads.blocks.magic.ArcaneReflector;
import com.Da_Technomancer.crossroads.blocks.magic.BeaconHarness;
import com.Da_Technomancer.crossroads.blocks.magic.BeamSplitter;
import com.Da_Technomancer.crossroads.blocks.magic.BeamSplitterBasic;
import com.Da_Technomancer.crossroads.blocks.magic.ColorChart;
import com.Da_Technomancer.crossroads.blocks.magic.CrystalMasterAxis;
import com.Da_Technomancer.crossroads.blocks.magic.CrystallinePrism;
import com.Da_Technomancer.crossroads.blocks.magic.LensHolder;
import com.Da_Technomancer.crossroads.blocks.magic.QuartzStabilizer;
import com.Da_Technomancer.crossroads.blocks.rotary.Axle;
import com.Da_Technomancer.crossroads.blocks.rotary.Grindstone;
import com.Da_Technomancer.crossroads.blocks.rotary.ItemChute;
import com.Da_Technomancer.crossroads.blocks.rotary.ItemChutePort;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearMaster;
import com.Da_Technomancer.crossroads.blocks.rotary.LargeGearSlave;
import com.Da_Technomancer.crossroads.blocks.rotary.MasterAxis;
import com.Da_Technomancer.crossroads.blocks.rotary.RotaryDrill;
import com.Da_Technomancer.crossroads.blocks.rotary.SidedGearHolder;
import com.Da_Technomancer.crossroads.blocks.technomancy.AdditionAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.ArcCosAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.ArcSinAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.CageCharger;
import com.Da_Technomancer.crossroads.blocks.technomancy.ChunkUnlocker;
import com.Da_Technomancer.crossroads.blocks.technomancy.CopshowiumCreationChamber;
import com.Da_Technomancer.crossroads.blocks.technomancy.CosAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.EqualsAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.FluxManipulator;
import com.Da_Technomancer.crossroads.blocks.technomancy.FluxReaderAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.GatewayFrame;
import com.Da_Technomancer.crossroads.blocks.technomancy.GreaterThanAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.LessThanAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.MechanicalArm;
import com.Da_Technomancer.crossroads.blocks.technomancy.MechanicalBeamSplitter;
import com.Da_Technomancer.crossroads.blocks.technomancy.MultiplicationAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.Prototype;
import com.Da_Technomancer.crossroads.blocks.technomancy.PrototypePort;
import com.Da_Technomancer.crossroads.blocks.technomancy.PrototypingTable;
import com.Da_Technomancer.crossroads.blocks.technomancy.RateManipulator;
import com.Da_Technomancer.crossroads.blocks.technomancy.RedstoneAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.RedstoneRegistry;
import com.Da_Technomancer.crossroads.blocks.technomancy.SinAxis;
import com.Da_Technomancer.crossroads.blocks.technomancy.SquareRootAxis;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.itemSets.HeatCableFactory;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class ModBlocks{

	public static SidedGearHolder sidedGearHolder;
	public static MasterAxis masterAxis;
	public static FluidTube fluidTube;
	public static HeatingCrucible heatingCrucible;
	public static Grindstone grindstone;
	public static SteamBoiler steamBoiler;
	public static BlockSalt blockSalt;
	public static Brazier brazier;
	public static FluidVoid fluidVoid;
	public static RotaryPump rotaryPump;
	public static SteamTurbine steamTurbine;
	public static HeatExchanger heatExchanger;
	public static HeatExchanger insulHeatExchanger;
	public static FluidTank fluidTank;
	public static FuelHeater fuelHeater;
	public static HeatingChamber heatingChamber;
	public static SaltReactor saltReactor;
	public static FluidCoolingChamber fluidCoolingChamber;
	public static SlottedChest slottedChest;
	public static SortingHopper sortingHopper;
	public static LargeGearMaster largeGearMaster;
	public static LargeGearSlave largeGearSlave;
	public static CandleLilyPad candleLilyPad;
	public static ItemChute itemChute;
	public static ItemChutePort itemChutePort;
	public static Radiator radiator;
	public static RotaryDrill rotaryDrill;
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
	public static BeamSplitter beamSplitter;
	public static ColorChart colorChart;
	public static FertileSoil fertileSoil;
	public static MultiPistonExtend multiPistonExtend;
	public static MultiPistonExtend multiPistonExtendSticky;
	public static MultiPistonBase multiPiston;
	public static MultiPistonBase multiPistonSticky;
	public static BeamSplitterBasic beamSplitterBasic;
	public static CrystalMasterAxis crystalMasterAxis;
	public static Axle axle;
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
	public static PortExtender portExtender;
	public static ReactionChamber reactionChamber;
	public static HeatLimiter heatLimiter;
	public static Dynamo dynamo;
	public static TeslaCoil teslaCoil;
	public static TeslaCoilTop teslaCoilTop;
	public static ReagentTank reagentTank;
	public static ReagentPump reagentPump;
	public static MaxwellDemon maxwellDemon;
	public static PhialHolder phialHolder;
	public static FlorenceHolder florenceHolder;
	public static RedsAlchemicalTube redsAlchemicalTube;

	public static final ArrayList<Block> toRegister = new ArrayList<Block>();
	
	/**
	 * Registers the item form of a block and the item model.
	 * @param block
	 * @return The passed block for convenience. 
	 */
	public static <T extends Block> T blockAddQue(T block){
		return blockAddQue(block, true);
	}
	
	/**
	 * Registers the item form of a block and an if registerModel item model.
	 * @param block
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
	 * @param block
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
	
	/**
	 * Registers the item form of a block and the item model for each metadata up to endMeta. 
	 * @param block
	 * @param endMeta The end meta value of the item.
	 * @param location The location of the model. 
	 * @return The block for convenience. 
	 */
	public static <T extends Block> T blockAddQueRange(T block, int endMeta, Item multiItem){
		ModItems.toRegister.add(multiItem);
		multiItem.setRegistryName(block.getRegistryName());
		for(int i = 0; i <= endMeta; i++){
			ModItems.toClientRegister.put(Pair.of(multiItem, i), new ModelResourceLocation(block.getRegistryName(), "inventory"));
		}
		return block;
	}

	public static final void init(){
		masterAxis = new MasterAxis();
		grindstone = new Grindstone();
		sidedGearHolder = new SidedGearHolder();
		largeGearMaster = new LargeGearMaster();
		largeGearSlave = new LargeGearSlave();
		heatingCrucible = new HeatingCrucible();
		fluidTube = new FluidTube();
		steamBoiler = new SteamBoiler();
		rotaryPump = new RotaryPump();
		steamTurbine = new SteamTurbine();
		blockSalt = new BlockSalt();
		brazier = new Brazier();
		fluidVoid = new FluidVoid();
		heatExchanger = new HeatExchanger(false);
		insulHeatExchanger = new HeatExchanger(true);
		fluidTank = new FluidTank();
		fuelHeater = new FuelHeater();
		heatingChamber = new HeatingChamber();
		saltReactor = new SaltReactor();
		fluidCoolingChamber = new FluidCoolingChamber();
		slottedChest = new SlottedChest();
		sortingHopper = new SortingHopper();
		candleLilyPad = new CandleLilyPad();
		itemChute = new ItemChute();
		itemChutePort = new ItemChutePort();
		radiator = new Radiator();
		rotaryDrill = new RotaryDrill();
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
		beamSplitter = new BeamSplitter();
		colorChart = new ColorChart();
		fertileSoil = new FertileSoil();
		multiPistonExtend = new MultiPistonExtend(false);
		multiPistonExtendSticky = new MultiPistonExtend(true);
		multiPiston = new MultiPistonBase(false);
		multiPistonSticky = new MultiPistonBase(true);
		beamSplitterBasic = new BeamSplitterBasic();
		crystalMasterAxis = new CrystalMasterAxis();
		axle = new Axle();
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
		portExtender = new PortExtender();
		reactionChamber = new ReactionChamber();
		heatLimiter = new HeatLimiter();
		dynamo = new Dynamo();
		teslaCoil = new TeslaCoil();
		teslaCoilTop = new TeslaCoilTop();
		reagentTank = new ReagentTank();
		reagentPump = new ReagentPump();
		maxwellDemon = new MaxwellDemon();
		phialHolder = new PhialHolder();
		florenceHolder = new FlorenceHolder();
		redsAlchemicalTube = new RedsAlchemicalTube();
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
	}
}
