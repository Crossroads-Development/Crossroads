package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.api.templates.ICustomItemBlock;
import com.Da_Technomancer.crossroads.blocks.alchemy.*;
import com.Da_Technomancer.crossroads.blocks.beams.*;
import com.Da_Technomancer.crossroads.blocks.electric.Dynamo;
import com.Da_Technomancer.crossroads.blocks.electric.LightningRodExtension;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoil;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.blocks.fluid.*;
import com.Da_Technomancer.crossroads.blocks.heat.*;
import com.Da_Technomancer.crossroads.blocks.rotary.*;
import com.Da_Technomancer.crossroads.blocks.technomancy.*;
import com.Da_Technomancer.crossroads.blocks.witchcraft.*;
import com.Da_Technomancer.crossroads.fluids.CRFluids;
import com.Da_Technomancer.crossroads.fluids.GenericFluid;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegisterEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class CRBlocks{

	public static final HashMap<HeatInsulators, HeatCable> HEAT_CABLES = new HashMap<>();
	public static final HashMap<HeatInsulators, RedstoneHeatCable> REDSTONE_HEAT_CABLES = new HashMap<>();
	public static Mechanism mechanism;
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
	public static BeamReflectorSensitive beamReflectorSensitive;
	public static LensFrame lensFrame;
	public static BasicBlock blockPureQuartz;
	public static BasicBlock blockBrightQuartz;
	public static BeamSiphon beamSiphon;
	public static BeamSplitter beamSplitter;
	public static ColorChart colorChart;
	public static LightCluster lightCluster;
	public static CrystalMasterAxis crystalMasterAxis;
	public static BeaconHarness beaconHarness;
	public static FatFeeder fatFeeder;
	public static RedstoneAxis redstoneAxis;
	public static CageCharger cageCharger;
	public static HamsterWheel hamsterWheel;
	public static CopshowiumCreationChamber copshowiumCreationChamber;
	public static GatewayController gatewayController;
	public static GatewayFrameEdge gatewayEdge;
	public static DetailedCrafter detailedCrafter;
	//	public static PrototypingTable prototypingTable;
//	public static Prototype prototype;
//	public static PrototypePort prototypePort;
//	public static MechanicalArm mechanicalArm;
//	public static RedstoneRegistry redstoneRegistry;
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
	public static HeatLimiterBasic heatLimiterBasic;
	public static HeatLimiterRedstone heatLimiterRedstone;
	public static ReagentFilter reagentFilterGlass;
	public static ReagentFilter reagentFilterCrystal;
	public static Dynamo dynamo;
	public static TeslaCoil teslaCoil;
	public static TeslaCoilTop teslaCoilTopNormal;
	public static TeslaCoilTop teslaCoilTopDistance;
	public static TeslaCoilTop teslaCoilTopIntensity;
	public static TeslaCoilTop teslaCoilTopAttack;
	public static TeslaCoilTop teslaCoilTopEfficiency;
	public static TeslaCoilTop teslaCoilTopDecorative;
	public static MaxwellDemon maxwellDemon;
	public static GlasswareHolder glasswareHolder;
	public static DensusPlate densusPlate;
	public static DensusPlate antiDensusPlate;
	public static BasicBlock cavorite;
	public static ChargingStand chargingStand;
	public static AtmosCharger atmosCharger;
	public static VoltusGenerator voltusGenerator;
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
	public static PermeableObsidian permeableObsidian;
	public static FluxNode fluxNode;
	public static TemporalAccelerator temporalAccelerator;
	public static ChronoHarness chronoHarness;
	public static FluxSink fluxSink;
	public static Steamer steamer;
	public static WindingTable windingTable;
	public static BasicBlock redstoneCrystal;
	public static DetailedAutoCrafter detailedAutoCrafter;
	public static LodestoneTurbine lodestoneTurbine;
	public static LodestoneDynamo lodestoneDynamo;
	public static SequenceBox sequenceBox;
	public static ChunkAccelerator chunkAccelerator;
	public static GatewayControllerDestination gatewayControllerDestination;
	public static BeamCannon beamCannon;
	public static FormulationVat formulationVat;
	public static BrewingVat brewingVat;
	public static AutoInjector autoInjector;
	public static ColdStorage coldStorage;
	public static HydroponicsTrough hydroponicsTrough;
	public static MedicinalMushroom medicinalMushroom;
	public static PetrolCactus petrolCactus;
	public static Wheezewort wheezewort;
	public static StasisStorage stasisStorage;
	public static CultivatorVat cultivatorVat;
	public static Incubator incubator;
	public static BloodCentrifuge bloodCentrifuge;
	public static EmbryoLab embryoLab;
	public static HeatReservoirCreative heatReservoirCreative;
	public static MasterAxisCreative masterAxisCreative;
	public static BeamExtractorCreative beamExtractorCreative;
	public static ItemCannon itemCannon;
	public static FireDetector fireDetector;
	public static BloodBeamLinker bloodBeamLinker;
	public static LightningRodExtension lightningRodExtension;
	public static BasicBlock blockTin;
	public static BasicBlock blockRawTin;
	public static BasicBlock oreTin;
	public static BasicBlock oreTinDeep;
	public static BasicBlock blockBronze;
	public static BasicBlock blockRuby;
	public static BasicBlock oreRuby;
	public static BasicBlock blockCopshowium;
	public static BasicBlock oreVoid;

	public static BlockBehaviour.Properties getRockProperty(){
		return BlockBehaviour.Properties.of(Material.STONE).strength(3).requiresCorrectToolForDrops().sound(SoundType.STONE);
	}

	public static BlockBehaviour.Properties getMetalProperty(){
		return BlockBehaviour.Properties.of(Material.METAL).strength(3).requiresCorrectToolForDrops().sound(SoundType.METAL);
	}

	public static BlockBehaviour.Properties getGlassProperty(){
		return BlockBehaviour.Properties.of(Material.GLASS).strength(0.5F).sound(SoundType.GLASS);
	}

	/**
	 * Public for read only
	 * Do NOT modify this value, as it is mutable
	 */
	public static final Item.Properties itemBlockProp = new Item.Properties();

	private static final HashMap<String, Block> toRegister = new HashMap<>();

	/**
	 * Queues up a block to be registered, along with an itemblock added to the creative tab
	 * @param regName Block registry name (without essentials: prefix)
	 * @param block Block. Supports ICustomItemBlock.
	 * @return The block
	 * @param <T> Block class
	 */
	public static <T extends Block> T queueForRegister(String regName, T block){
		return queueForRegister(regName, block, true, CRItems.MAIN_CREATIVE_TAB_ID);
	}

	/**
	 * Queues up a block to be registered, optionally along with an itemblock added to the creative tab
	 * @param regName Block registry name (without essentials: prefix)
	 * @param block Block. Supports ICustomItemBlock.
	 * @param itemblock Whether to create an itemblock.
	 * @param tab Creative tab id to register the itemblock in. Null to not add to creative tab.
	 * @return The block
	 * @param <T> Block class
	 */
	public static <T extends Block> T queueForRegister(String regName, T block, boolean itemblock, @Nullable String tab){
		toRegister.put(regName, block);
		if(itemblock){
			Item item;
			if(block instanceof ICustomItemBlock customBlock){
				item = customBlock.createItemBlock();
			}else{
				item = new BlockItem(block, itemBlockProp);
			}
			CRItems.queueForRegister(regName, item, tab);
		}
		return block;
	}

	public static void init(){
		for(HeatInsulators insul : HeatInsulators.values()){
			HEAT_CABLES.put(insul, new HeatCable(insul));
			REDSTONE_HEAT_CABLES.put(insul, new RedstoneHeatCable(insul));
		}

		//Ores
		blockTin = new BasicBlock("block_tin", getMetalProperty());
		blockRawTin = new BasicBlock("block_raw_tin", getRockProperty());
		oreTin = new BasicBlock("ore_tin", getRockProperty().strength(3));
		oreTinDeep = new BasicBlock("ore_tin_deep", getRockProperty().strength(4.5F, 3).sound(SoundType.DEEPSLATE));
		blockBronze = new BasicBlock("block_bronze", getMetalProperty());
		blockRuby = new BasicBlock("block_ruby", getRockProperty());
		oreRuby = new BasicBlock("ore_ruby", getRockProperty().strength(3));
		blockCopshowium = new BasicBlock("block_copshowium", getMetalProperty());
		oreVoid = new BasicBlock("ore_void", getRockProperty().strength(3, 9));

		masterAxis = new MasterAxis();
		masterAxisCreative = new MasterAxisCreative();
		millstone = new Millstone();
		mechanism = new Mechanism();
		largeGearMaster = new LargeGearMaster();
		largeGearSlave = new LargeGearSlave();
		heatingCrucible = new HeatingCrucible();
		fluidTube = new FluidTube();
		redstoneFluidTube = new RedstoneFluidTube();
		steamBoiler = new SteamBoiler();
		rotaryPump = new RotaryPump();
		steamTurbine = new SteamTurbine();
		blockSalt = new BlockSalt();
		heatSink = new HeatSink();
		fluidTank = new FluidTank();
		firebox = new Firebox();
		icebox = new Icebox();
		smelter = new Smelter();
		steamer = new Steamer();
		saltReactor = new SaltReactor();
		fluidCoolingChamber = new FluidCoolingChamber();
		radiator = new Radiator();
		rotaryDrill = new RotaryDrill(false);
		rotaryDrillGold = new RotaryDrill(true);
		fatCollector = new FatCollector();
		fatCongealer = new FatCongealer();
		fatFeeder = new FatFeeder();
		waterCentrifuge = new WaterCentrifuge();
		lightningRodExtension = new LightningRodExtension();
		dynamo = new Dynamo();
		windTurbine = new WindTurbine();
		solarHeater = new SolarHeater();
		heatReservoir = new HeatReservoir();
		heatReservoirCreative = new HeatReservoirCreative();
		stirlingEngine = new StirlingEngine();
		stampMill = new StampMill();
		stampMillTop = new StampMillTop();
		oreCleanser = new OreCleanser();
		blastFurnace = new BlastFurnace();
		windingTable = new WindingTable();
		detailedCrafter = new DetailedCrafter();

		//Beams
		blockPureQuartz = new BasicBlock("block_pure_quartz", getRockProperty());
		blockBrightQuartz = new BasicBlock("block_bright_quartz", getRockProperty().lightLevel(state -> 15));
		permeableGlass = new PermeableGlass();
		permeableQuartz = new PermeableQuartz();
		permeableObsidian = new PermeableObsidian();
		redstoneCrystal = new BasicBlock("redstone_crystal", getGlassProperty().strength(0.3F)){
			@Override
			public boolean isSignalSource(BlockState state){
				return true;
			}

			@Override
			public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
				return 15;
			}

			@Override
			public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
				tooltip.add(Component.translatable("tt.crossroads.redstone_crystal.drops"));
				tooltip.add(Component.translatable("tt.crossroads.redstone_crystal.power"));
			}
		};
		beamExtractor = new BeamExtractor();
		beamExtractorCreative = new BeamExtractorCreative();
		quartzStabilizer = new QuartzStabilizer();
		crystallinePrism = new CrystallinePrism();
		beamReflector = new BeamReflector();
		beamReflectorSensitive = new BeamReflectorSensitive();
		lensFrame = new LensFrame();
		beamRedirector = new BeamRedirector();
		beamSiphon = new BeamSiphon();
		beamSplitter = new BeamSplitter();
		colorChart = new ColorChart();
		lightCluster = new LightCluster();
		crystalMasterAxis = new CrystalMasterAxis();
//		ratiator = new Ratiator();

		//Technomancy
		copshowiumCreationChamber = new CopshowiumCreationChamber();
		clockworkStabilizer = new ClockworkStabilizer();
		detailedAutoCrafter = new DetailedAutoCrafter();
		beaconHarness = new BeaconHarness();
		chronoHarness = new ChronoHarness();
		redstoneAxis = new RedstoneAxis();
		cageCharger = new CageCharger();
		beamCannon = new BeamCannon();
		itemCannon = new ItemCannon();
//		mathAxis = new MathAxis();
		gatewayController = new GatewayController();
		gatewayControllerDestination = new GatewayControllerDestination();
		gatewayEdge = new GatewayFrameEdge();
//		redstoneKeyboard = new RedstoneKeyboard();
//		prototypingTable = new PrototypingTable();
//		prototype = new Prototype();
//		prototypePort = new PrototypePort();
//		mechanicalArm = new MechanicalArm();
//		redstoneRegistry = new RedstoneRegistry();
		lodestoneTurbine = new LodestoneTurbine();
		lodestoneDynamo = new LodestoneDynamo();
		sequenceBox = new SequenceBox();
		temporalAccelerator = new TemporalAccelerator();
		chunkAccelerator = new ChunkAccelerator();
		fluxNode = new FluxNode();
		fluxSink = new FluxSink();

		//Alchemy
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
		heatLimiterBasic = new HeatLimiterBasic();
		heatLimiterRedstone = new HeatLimiterRedstone();
		reagentFilterGlass = new ReagentFilter(false);
		reagentFilterCrystal = new ReagentFilter(true);
		teslaCoil = new TeslaCoil();
		teslaCoilTopNormal = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.NORMAL);
		teslaCoilTopDistance = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.DISTANCE);
		teslaCoilTopIntensity = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.INTENSITY);
		teslaCoilTopAttack = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.ATTACK);
		teslaCoilTopEfficiency = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.EFFICIENCY);
		teslaCoilTopDecorative = new TeslaCoilTop(TeslaCoilTop.TeslaCoilVariants.DECORATIVE);
		glasswareHolder = new GlasswareHolder();
		densusPlate = new DensusPlate(false);
		antiDensusPlate = new DensusPlate(true);
		cavorite = new BasicBlock("block_cavorite", getRockProperty()){
			@Override
			public void appendHoverText(ItemStack stack, @Nullable BlockGetter worldIn, List<Component> tooltip, TooltipFlag flagIn){
				tooltip.add(Component.translatable("tt.crossroads.cavorite"));
				tooltip.add(Component.translatable("tt.crossroads.decoration"));
			}
		};
		chargingStand = new ChargingStand();
		atmosCharger = new AtmosCharger();
		voltusGenerator = new VoltusGenerator();
		reactiveSpot = new ReactiveSpot();
		fireDetector = new FireDetector();

		//Witchcraft
		formulationVat = new FormulationVat();
		cultivatorVat = new CultivatorVat();
		bloodCentrifuge = new BloodCentrifuge();
		embryoLab = new EmbryoLab();
		incubator = new Incubator();
		brewingVat = new BrewingVat();
		autoInjector = new AutoInjector();
		coldStorage = new ColdStorage();
		stasisStorage = new StasisStorage();
		bloodBeamLinker = new BloodBeamLinker();
		hydroponicsTrough = new HydroponicsTrough();
		medicinalMushroom = new MedicinalMushroom();
		petrolCactus = new PetrolCactus();
		wheezewort = new Wheezewort();

		//Bobo
		hamsterWheel = new HamsterWheel();
		maxwellDemon = new MaxwellDemon();
		fluidVoid = new FluidVoid();
	}

	public static void registerBlocks(RegisterEvent.RegisterHelper<Block> helper){
		EventHandlerCommon.CRModEventsCommon.registerAll(helper, toRegister);
	}

	@OnlyIn(Dist.CLIENT)
	public static void clientInit(){
//		setCutout(permeableGlass, rotaryPump, steamTurbine, alchemicalTubeGlass, alchemicalTubeCrystal, redsAlchemicalTubeGlass, redsAlchemicalTubeCrystal, fluidInjectorGlass, fluidInjectorCrystal, flowLimiterGlass, flowLimiterCrystal, heatedTubeGlass, heatedTubeCrystal, coolingCoilGlass, coolingCoilCrystal, reactionChamberGlass, reactionChamberCrystal, reagentTankGlass, reagentTankCrystal, reagentPumpGlass, reagentPumpCrystal, glasswareHolder, reagentFilterGlass, reagentFilterCrystal, chargingStand, medicinalMushroom, petrolCactus);
//		setTrans(hydroponicsTrough, embryoLab, cultivatorVat);
		setFluidTrans(CRFluids.distilledWater, CRFluids.steam, CRFluids.fertilizerSolution, CRFluids.nutrientSolution, CRFluids.dirtyWater);
	}

//	@OnlyIn(Dist.CLIENT)
//	@Deprecated
//	private static void setCutout(Block... blocks){
//		RenderType cutout = RenderType.cutout();
//		for(Block block : blocks){
//			ItemBlockRenderTypes.setRenderLayer(block, cutout);
//		}
//	}
//
//	@Deprecated
//	private static void setTrans(Block... blocks){
//		RenderType type = RenderType.translucent();
//		for(Block block : blocks){
//			ItemBlockRenderTypes.setRenderLayer(block, type);
//		}
//	}

	@OnlyIn(Dist.CLIENT)
	private static void setFluidTrans(GenericFluid.FluidData...fluids){
		RenderType type = RenderType.translucent();
		for(GenericFluid.FluidData f : fluids){
			ItemBlockRenderTypes.setRenderLayer(f.getStill(), type);
			ItemBlockRenderTypes.setRenderLayer(f.getFlowing(), type);
		}
	}
}
