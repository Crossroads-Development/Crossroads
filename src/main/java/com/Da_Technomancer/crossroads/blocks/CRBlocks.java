package com.Da_Technomancer.crossroads.blocks;

import com.Da_Technomancer.crossroads.Crossroads;
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
import com.mojang.serialization.MapCodec;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
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
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.RegisterEvent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

import static com.Da_Technomancer.essentials.blocks.ESBlocks.singletonBlockType;

public class CRBlocks{

	public static final DeferredRegister<MapCodec<? extends Block>> BLOCK_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_TYPE, Crossroads.MODID);


	public static final HashMap<HeatInsulators, HeatCable> HEAT_CABLES = new HashMap<>();
	public static final HashMap<HeatInsulators, RedstoneHeatCable> REDSTONE_HEAT_CABLES = new HashMap<>();

	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Mechanism>> MECHANISM_TYPE = BLOCK_TYPES.register("mechanism", singletonBlockType(Mechanism::new));
	public static Mechanism mechanism;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MasterAxis>> MASTER_AXIS_TYPE = BLOCK_TYPES.register("master_axis", singletonBlockType(MasterAxis::new));
	public static MasterAxis masterAxis;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluidTube>> FLUID_TUBE_TYPE = BLOCK_TYPES.register("fluid_tube", singletonBlockType(FluidTube::new));
	public static FluidTube fluidTube;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatingCrucible>> HEATING_CRUCIBLE_TYPE = BLOCK_TYPES.register("heating_crucible", singletonBlockType(HeatingCrucible::new));
	public static HeatingCrucible heatingCrucible;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Millstone>> MILLSTONE_TYPE = BLOCK_TYPES.register("millstone", singletonBlockType(Millstone::new));
	public static Millstone millstone;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<SteamBoiler>> STEAM_BOILER_TYPE = BLOCK_TYPES.register("steam_boiler", singletonBlockType(SteamBoiler::new));
	public static SteamBoiler steamBoiler;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BlockSalt>> BLOCK_SALT_TYPE = BLOCK_TYPES.register("block_salt", singletonBlockType(BlockSalt::new));
	public static BlockSalt blockSalt;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluidVoid>> FLUID_VOID_TYPE = BLOCK_TYPES.register("fluid_void", singletonBlockType(FluidVoid::new));
	public static FluidVoid fluidVoid;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RotaryPump>> ROTARY_PUMP_TYPE = BLOCK_TYPES.register("rotary_pump", singletonBlockType(RotaryPump::new));
	public static RotaryPump rotaryPump;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<SteamTurbine>> STEAM_TURBINE_TYPE = BLOCK_TYPES.register("steam_turbine", singletonBlockType(SteamTurbine::new));
	public static SteamTurbine steamTurbine;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatSink>> HEAT_SINK_TYPE = BLOCK_TYPES.register("heat_sink", singletonBlockType(HeatSink::new));
	public static HeatSink heatSink;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluidTank>> FLUID_TANK_TYPE = BLOCK_TYPES.register("fluid_tank", singletonBlockType(FluidTank::new));
	public static FluidTank fluidTank;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Firebox>> FIREBOX_TYPE = BLOCK_TYPES.register("firebox", singletonBlockType(Firebox::new));
	public static Firebox firebox;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Smelter>> SMELTER_TYPE = BLOCK_TYPES.register("smelter", singletonBlockType(Smelter::new));
	public static Smelter smelter;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<SaltReactor>> SALT_REACTOR_TYPE = BLOCK_TYPES.register("salt_reactor", singletonBlockType(SaltReactor::new));
	public static SaltReactor saltReactor;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluidCoolingChamber>> FLUID_COOLING_CHAMBER_TYPE = BLOCK_TYPES.register("fluid_cooling_chamber", singletonBlockType(FluidCoolingChamber::new));
	public static FluidCoolingChamber fluidCoolingChamber;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LargeGearMaster>> LARGE_GEAR_MASTER_TYPE = BLOCK_TYPES.register("large_gear_master", singletonBlockType(LargeGearMaster::new));
	public static LargeGearMaster largeGearMaster;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LargeGearSlave>> LARGE_GEAR_SLAVE_TYPE = BLOCK_TYPES.register("large_gear_slave", singletonBlockType(LargeGearSlave::new));
	public static LargeGearSlave largeGearSlave;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Radiator>> RADIATOR_TYPE = BLOCK_TYPES.register("radiator", singletonBlockType(Radiator::new));
	public static Radiator radiator;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RotaryDrill>> ROTARY_DRILL_TYPE = BLOCK_TYPES.register("rotary_drill", () -> RotaryDrill.CODEC);
	public static RotaryDrill rotaryDrill;
	public static RotaryDrill rotaryDrillGold;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FatCollector>> FAT_COLLECTOR_TYPE = BLOCK_TYPES.register("fat_collector", singletonBlockType(FatCollector::new));
	public static FatCollector fatCollector;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FatCongealer>> FAT_CONGEALER_TYPE = BLOCK_TYPES.register("fat_congealer", singletonBlockType(FatCongealer::new));
	public static FatCongealer fatCongealer;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RedstoneFluidTube>> REDSTONE_FLUID_TUBE_TYPE = BLOCK_TYPES.register("redstone_fluid_tube", singletonBlockType(RedstoneFluidTube::new));
	public static RedstoneFluidTube redstoneFluidTube;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WaterCentrifuge>> WATER_CENTRIFUGE_TYPE = BLOCK_TYPES.register("water_centrifuge", singletonBlockType(WaterCentrifuge::new));
	public static WaterCentrifuge waterCentrifuge;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamExtractor>> BEAM_EXTRACTOR_TYPE = BLOCK_TYPES.register("beam_extractor", singletonBlockType(BeamExtractor::new));
	public static BeamExtractor beamExtractor;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<QuartzStabilizer>> QUARTZ_STABILIZER_TYPE = BLOCK_TYPES.register("quartz_stabilizer", singletonBlockType(QuartzStabilizer::new));
	public static QuartzStabilizer quartzStabilizer;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CrystallinePrism>> CRYSTALLINE_PRISM_TYPE = BLOCK_TYPES.register("crystalline_prism", singletonBlockType(CrystallinePrism::new));
	public static CrystallinePrism crystallinePrism;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamReflector>> BEAM_REFLECTOR_TYPE = BLOCK_TYPES.register("beam_reflector", singletonBlockType(BeamReflector::new));
	public static BeamReflector beamReflector;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamReflectorSensitive>> BEAM_REFLECTOR_SENSITIVE_TYPE = BLOCK_TYPES.register("beam_reflector_sensitive", singletonBlockType(BeamReflectorSensitive::new));
	public static BeamReflectorSensitive beamReflectorSensitive;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LensFrame>> LENS_FRAME_TYPE = BLOCK_TYPES.register("lens_frame", singletonBlockType(LensFrame::new));
	public static LensFrame lensFrame;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> PURE_QUARTZ_TYPE = BLOCK_TYPES.register("pure_quartz", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockPureQuartz;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BRIGHT_QUARTZ_TYPE = BLOCK_TYPES.register("bright_quartz", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockBrightQuartz;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamSiphon>> BEAM_SIPHON_TYPE = BLOCK_TYPES.register("beam_siphon", singletonBlockType(BeamSiphon::new));
	public static BeamSiphon beamSiphon;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamSplitter>> BEAM_SPLITTER_TYPE = BLOCK_TYPES.register("beam_splitter", singletonBlockType(BeamSplitter::new));
	public static BeamSplitter beamSplitter;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ColorChart>> COLOR_CHART_TYPE = BLOCK_TYPES.register("color_chart", singletonBlockType(ColorChart::new));
	public static ColorChart colorChart;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LightCluster>> LIGHT_CLUSTER_TYPE = BLOCK_TYPES.register("light_cluster", singletonBlockType(LightCluster::new));
	public static LightCluster lightCluster;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CrystalMasterAxis>> CRYSTAL_MASTER_AXIS_TYPE = BLOCK_TYPES.register("crystal_master_axis", singletonBlockType(CrystalMasterAxis::new));
	public static CrystalMasterAxis crystalMasterAxis;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeaconHarness>> BEACON_HARNESS_TYPE = BLOCK_TYPES.register("beacon_harness", singletonBlockType(BeaconHarness::new));
	public static BeaconHarness beaconHarness;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FatFeeder>> FAT_FEEDER_TYPE = BLOCK_TYPES.register("fat_feeder", singletonBlockType(FatFeeder::new));
	public static FatFeeder fatFeeder;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RedstoneAxis>> REDSTONE_AXIS_TYPE = BLOCK_TYPES.register("redstone_axis", singletonBlockType(RedstoneAxis::new));
	public static RedstoneAxis redstoneAxis;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CageCharger>> CAGE_CHARGER_TYPE = BLOCK_TYPES.register("cage_charger", singletonBlockType(CageCharger::new));
	public static CageCharger cageCharger;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HamsterWheel>> HAMSTER_WHEEL_TYPE = BLOCK_TYPES.register("hamster_wheel", singletonBlockType(HamsterWheel::new));
	public static HamsterWheel hamsterWheel;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CopshowiumCreationChamber>> COPSHOWIUM_CREATION_CHAMBER_TYPE = BLOCK_TYPES.register("copshowium_creation_chamber", singletonBlockType(CopshowiumCreationChamber::new));
	public static CopshowiumCreationChamber copshowiumCreationChamber;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GatewayController>> GATEWAY_CONTROLLER_TYPE = BLOCK_TYPES.register("gateway_controller", singletonBlockType(GatewayController::new));
	public static GatewayController gatewayController;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GatewayFrameEdge>> GATEWAY_FRAME_EDGE_TYPE = BLOCK_TYPES.register("gateway_frame_edge", singletonBlockType(GatewayFrameEdge::new));
	public static GatewayFrameEdge gatewayEdge;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<DetailedCrafter>> DETAILED_CRAFTER_TYPE = BLOCK_TYPES.register("detailed_crafter", singletonBlockType(DetailedCrafter::new));
	public static DetailedCrafter detailedCrafter;
	//public static PrototypingTable prototypingTable;
	//public static Prototype prototype;
	//public static PrototypePort prototypePort;
	//public static MechanicalArm mechanicalArm;
	//public static RedstoneRegistry redstoneRegistry;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<AlchemicalTube>> ALCHEMICAL_TUBE_TYPE = BLOCK_TYPES.register("alchemical_tube", () -> AlchemicalTube.CODEC);
	public static AlchemicalTube alchemicalTubeGlass;
	public static AlchemicalTube alchemicalTubeCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<RedsAlchemicalTube>> REDS_ALCHEMICAL_TUBE_TYPE = BLOCK_TYPES.register("reds_alchemical_tube", () -> RedsAlchemicalTube.CODEC);
	public static RedsAlchemicalTube redsAlchemicalTubeGlass;
	public static RedsAlchemicalTube redsAlchemicalTubeCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluidInjector>> FLUID_INJECTOR_TYPE = BLOCK_TYPES.register("fluid_injector", () -> FluidInjector.CODEC);
	public static FluidInjector fluidInjectorGlass;
	public static FluidInjector fluidInjectorCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FlowLimiter>> FLOW_LIMITER_TYPE = BLOCK_TYPES.register("flow_limiter", () -> FlowLimiter.CODEC);
	public static FlowLimiter flowLimiterGlass;
	public static FlowLimiter flowLimiterCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatedTube>> HEATED_TUBE_TYPE = BLOCK_TYPES.register("heated_tube", () -> HeatedTube.CODEC);
	public static HeatedTube heatedTubeGlass;
	public static HeatedTube heatedTubeCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CoolingCoil>> COOLING_COIL_TYPE = BLOCK_TYPES.register("cooling_coil", () -> CoolingCoil.CODEC);
	public static CoolingCoil coolingCoilGlass;
	public static CoolingCoil coolingCoilCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ReactionChamber>> REACTION_CHAMBER_TYPE = BLOCK_TYPES.register("reaction_chamber", () -> ReactionChamber.CODEC);
	public static ReactionChamber reactionChamberGlass;
	public static ReactionChamber reactionChamberCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ReagentTank>> REAGENT_TANK_TYPE = BLOCK_TYPES.register("reagent_tank", () -> ReagentTank.CODEC);
	public static ReagentTank reagentTankGlass;
	public static ReagentTank reagentTankCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ReagentPump>> REAGENT_PUMP_TYPE = BLOCK_TYPES.register("reagent_pump", () -> ReagentPump.CODEC);
	public static ReagentPump reagentPumpGlass;
	public static ReagentPump reagentPumpCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ChemicalVent>> CHEMICAL_VENT_TYPE = BLOCK_TYPES.register("chemical_vent", singletonBlockType(ChemicalVent::new));
	public static ChemicalVent chemicalVent;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatLimiterBasic>> HEAT_LIMITER_BASIC_TYPE = BLOCK_TYPES.register("heat_limiter_basic", singletonBlockType(HeatLimiterBasic::new));
	public static HeatLimiterBasic heatLimiterBasic;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatLimiterRedstone>> HEAT_LIMITER_REDSTONE_TYPE = BLOCK_TYPES.register("heat_limiter_redstone", singletonBlockType(HeatLimiterRedstone::new));
	public static HeatLimiterRedstone heatLimiterRedstone;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ReagentFilter>> REAGENT_FILTER_TYPE = BLOCK_TYPES.register("reagent_filter_glass", () -> ReagentFilter.CODEC);
	public static ReagentFilter reagentFilterGlass;
	public static ReagentFilter reagentFilterCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Dynamo>> DYNAMO_TYPE = BLOCK_TYPES.register("dynamo", singletonBlockType(Dynamo::new));
	public static Dynamo dynamo;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<TeslaCoil>> TESLA_COIL_TYPE = BLOCK_TYPES.register("tesla_coil", singletonBlockType(TeslaCoil::new));
	public static TeslaCoil teslaCoil;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<TeslaCoilTop>> TESLA_COIL_TOP_TYPE = BLOCK_TYPES.register("tesla_coil_top_normal", () -> TeslaCoilTop.CODEC);
	public static TeslaCoilTop teslaCoilTopNormal;
	public static TeslaCoilTop teslaCoilTopDistance;
	public static TeslaCoilTop teslaCoilTopIntensity;
	public static TeslaCoilTop teslaCoilTopAttack;
	public static TeslaCoilTop teslaCoilTopEfficiency;
	public static TeslaCoilTop teslaCoilTopDecorative;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MaxwellDemon>> MAXWELL_DEMON_TYPE = BLOCK_TYPES.register("maxwell_demon", singletonBlockType(MaxwellDemon::new));
	public static MaxwellDemon maxwellDemon;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GlasswareHolder>> GLASSWARE_HOLDER_TYPE = BLOCK_TYPES.register("glassware_holder", singletonBlockType(GlasswareHolder::new));
	public static GlasswareHolder glasswareHolder;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<DensusPlate>> DENSUS_PLATE_TYPE = BLOCK_TYPES.register("densus_plate", () -> DensusPlate.CODEC);
	public static DensusPlate densusPlate;
	public static DensusPlate antiDensusPlate;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> CAVORITE_TYPE = BLOCK_TYPES.register("cavorite", singletonBlockType(BasicBlock::new));
	public static BasicBlock cavorite;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ChargingStand>> CHARGING_STAND_TYPE = BLOCK_TYPES.register("charging_stand", singletonBlockType(ChargingStand::new));
	public static ChargingStand chargingStand;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<AtmosCharger>> ATMOS_CHARGER_TYPE = BLOCK_TYPES.register("atmos_charger", singletonBlockType(AtmosCharger::new));
	public static AtmosCharger atmosCharger;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<VoltusGenerator>> VOLTUS_GENERATOR_TYPE = BLOCK_TYPES.register("voltus_generator", singletonBlockType(VoltusGenerator::new));
	public static VoltusGenerator voltusGenerator;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ReactiveSpot>> REACTIVE_SPOT_TYPE = BLOCK_TYPES.register("reactive_spot", singletonBlockType(ReactiveSpot::new));
	public static ReactiveSpot reactiveSpot;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ClockworkStabilizer>> CLOCKWORK_STABILIZER_TYPE = BLOCK_TYPES.register("clockwork_stabilizer", singletonBlockType(ClockworkStabilizer::new));
	public static ClockworkStabilizer clockworkStabilizer;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WindTurbine>> WIND_TURBINE_TYPE = BLOCK_TYPES.register("wind_turbine", singletonBlockType(WindTurbine::new));
	public static WindTurbine windTurbine;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<SolarHeater>> SOLAR_HEATER_TYPE = BLOCK_TYPES.register("solar_heater", singletonBlockType(SolarHeater::new));
	public static SolarHeater solarHeater;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatReservoir>> HEAT_RESERVOIR_TYPE = BLOCK_TYPES.register("heat_reservoir", singletonBlockType(HeatReservoir::new));
	public static HeatReservoir heatReservoir;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<StirlingEngine>> STIRLING_ENGINE_TYPE = BLOCK_TYPES.register("stirling_engine", singletonBlockType(StirlingEngine::new));
	public static StirlingEngine stirlingEngine;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Icebox>> ICEBOX_TYPE = BLOCK_TYPES.register("icebox", singletonBlockType(Icebox::new));
	public static Icebox icebox;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<StampMill>> STAMP_MILL_TYPE = BLOCK_TYPES.register("stamp_mill", singletonBlockType(StampMill::new));
	public static StampMill stampMill;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<StampMillTop>> STAMP_MILL_TOP_TYPE = BLOCK_TYPES.register("stamp_mill_top", singletonBlockType(StampMillTop::new));
	public static StampMillTop stampMillTop;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<OreCleanser>> ORE_CLEANSER_TYPE = BLOCK_TYPES.register("ore_cleanser", singletonBlockType(OreCleanser::new));
	public static OreCleanser oreCleanser;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BlastFurnace>> BLAST_FURNACE_TYPE = BLOCK_TYPES.register("blast_furnace", singletonBlockType(BlastFurnace::new));
	public static BlastFurnace blastFurnace;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamRedirector>> BEAM_REDIRECTOR_TYPE = BLOCK_TYPES.register("beam_redirector", singletonBlockType(BeamRedirector::new));
	public static BeamRedirector beamRedirector;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<PermeableGlass>> PERMEABLE_GLASS_TYPE = BLOCK_TYPES.register("permeable_glass", singletonBlockType(PermeableGlass::new));
	public static PermeableGlass permeableGlass;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<PermeableQuartz>> PERMEABLE_QUARTZ_TYPE = BLOCK_TYPES.register("permeable_quartz", singletonBlockType(PermeableQuartz::new));
	public static PermeableQuartz permeableQuartz;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<PermeableObsidian>> PERMEABLE_OBSIDIAN_TYPE = BLOCK_TYPES.register("permeable_obsidian", singletonBlockType(PermeableObsidian::new));
	public static PermeableObsidian permeableObsidian;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluxNode>> FLUX_NODE_TYPE = BLOCK_TYPES.register("flux_node", singletonBlockType(FluxNode::new));
	public static FluxNode fluxNode;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<TemporalAccelerator>> TEMPORAL_ACCELERATOR_TYPE = BLOCK_TYPES.register("temporal_accelerator", singletonBlockType(TemporalAccelerator::new));
	public static TemporalAccelerator temporalAccelerator;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ChronoHarness>> CHRONO_HARNESS_TYPE = BLOCK_TYPES.register("chrono_harness", singletonBlockType(ChronoHarness::new));
	public static ChronoHarness chronoHarness;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FluxSink>> FLUX_SINK_TYPE = BLOCK_TYPES.register("flux_sink", singletonBlockType(FluxSink::new));
	public static FluxSink fluxSink;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Steamer>> STEAMER_TYPE = BLOCK_TYPES.register("steamer", singletonBlockType(Steamer::new));
	public static Steamer steamer;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<WindingTable>> WINDING_TABLE_TYPE = BLOCK_TYPES.register("winding_table", singletonBlockType(WindingTable::new));
	public static WindingTable windingTable;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BASIC_BLOCK_TYPE = BLOCK_TYPES.register("redstone_crystal", singletonBlockType(BasicBlock::new));
	public static BasicBlock redstoneCrystal;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<DetailedAutoCrafter>> DETAILED_AUTO_CRAFTER_TYPE = BLOCK_TYPES.register("detailed_auto_crafter", singletonBlockType(DetailedAutoCrafter::new));
	public static DetailedAutoCrafter detailedAutoCrafter;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LodestoneTurbine>> LODESTONE_TURBINE_TYPE = BLOCK_TYPES.register("lodestone_turbine", singletonBlockType(LodestoneTurbine::new));
	public static LodestoneTurbine lodestoneTurbine;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LodestoneDynamo>> LODESTONE_DYNAMO_TYPE = BLOCK_TYPES.register("lodestone_dynamo", singletonBlockType(LodestoneDynamo::new));
	public static LodestoneDynamo lodestoneDynamo;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<SequenceBox>> SEQUENCE_BOX_TYPE = BLOCK_TYPES.register("sequence_box", singletonBlockType(SequenceBox::new));
	public static SequenceBox sequenceBox;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ChunkAccelerator>> CHUNK_ACCELERATOR_TYPE = BLOCK_TYPES.register("chunk_accelerator", singletonBlockType(ChunkAccelerator::new));
	public static ChunkAccelerator chunkAccelerator;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<GatewayControllerDestination>> GATEWAY_CONTROLLER_DESTINATION_TYPE = BLOCK_TYPES.register("gateway_controller_destination", singletonBlockType(GatewayControllerDestination::new));
	public static GatewayControllerDestination gatewayControllerDestination;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamCannon>> BEAM_CANNON_TYPE = BLOCK_TYPES.register("beam_cannon", singletonBlockType(BeamCannon::new));
	public static BeamCannon beamCannon;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FormulationVat>> FORMULATION_VAT_TYPE = BLOCK_TYPES.register("formulation_vat", singletonBlockType(FormulationVat::new));
	public static FormulationVat formulationVat;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BrewingVat>> BREWING_VAT_TYPE = BLOCK_TYPES.register("brewing_vat", singletonBlockType(BrewingVat::new));
	public static BrewingVat brewingVat;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<AutoInjector>> AUTO_INJECTOR_TYPE = BLOCK_TYPES.register("auto_injector", singletonBlockType(AutoInjector::new));
	public static AutoInjector autoInjector;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ColdStorage>> COLD_STORAGE_TYPE = BLOCK_TYPES.register("cold_storage", singletonBlockType(ColdStorage::new));
	public static ColdStorage coldStorage;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HydroponicsTrough>> HYDROPONICS_TROUGH_TYPE = BLOCK_TYPES.register("hydroponics_trough", singletonBlockType(HydroponicsTrough::new));
	public static HydroponicsTrough hydroponicsTrough;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MedicinalMushroom>> MEDICINAL_MUSHROOM_TYPE = BLOCK_TYPES.register("medicinal_mushroom", singletonBlockType(MedicinalMushroom::new));
	public static MedicinalMushroom medicinalMushroom;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<PetrolCactus>> PETROL_CACTUS_TYPE = BLOCK_TYPES.register("petrol_cactus", singletonBlockType(PetrolCactus::new));
	public static PetrolCactus petrolCactus;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Wheezewort>> WHEEZEWORT_TYPE = BLOCK_TYPES.register("wheezewort", singletonBlockType(Wheezewort::new));
	public static Wheezewort wheezewort;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<StasisStorage>> STASIS_STORAGE_TYPE = BLOCK_TYPES.register("stasis_storage", singletonBlockType(StasisStorage::new));
	public static StasisStorage stasisStorage;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<CultivatorVat>> CULTIVATOR_VAT_TYPE = BLOCK_TYPES.register("cultivator_vat", singletonBlockType(CultivatorVat::new));
	public static CultivatorVat cultivatorVat;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<Incubator>> INCUBATOR_TYPE = BLOCK_TYPES.register("incubator", singletonBlockType(Incubator::new));
	public static Incubator incubator;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BloodCentrifuge>> BLOOD_CENTRIFUGE_TYPE = BLOCK_TYPES.register("blood_centrifuge", singletonBlockType(BloodCentrifuge::new));
	public static BloodCentrifuge bloodCentrifuge;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<EmbryoLab>> EMBRYO_LAB_TYPE = BLOCK_TYPES.register("embryo_lab", singletonBlockType(EmbryoLab::new));
	public static EmbryoLab embryoLab;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<HeatReservoirCreative>> HEAT_RESERVOIR_CREATIVE_TYPE = BLOCK_TYPES.register("heat_reservoir_creative", singletonBlockType(HeatReservoirCreative::new));
	public static HeatReservoirCreative heatReservoirCreative;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<MasterAxisCreative>> MASTER_AXIS_CREATIVE_TYPE = BLOCK_TYPES.register("master_axis_creative", singletonBlockType(MasterAxisCreative::new));
	public static MasterAxisCreative masterAxisCreative;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BeamExtractorCreative>> BEAM_EXTRACTOR_CREATIVE_TYPE = BLOCK_TYPES.register("beam_extractor_creative", singletonBlockType(BeamExtractorCreative::new));
	public static BeamExtractorCreative beamExtractorCreative;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<ItemCannon>> ITEM_CANNON_TYPE = BLOCK_TYPES.register("item_cannon", singletonBlockType(ItemCannon::new));
	public static ItemCannon itemCannon;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<FireDetector>> FIRE_DETECTOR_TYPE = BLOCK_TYPES.register("fire_detector", singletonBlockType(FireDetector::new));
	public static FireDetector fireDetector;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BloodBeamLinker>> BLOOD_BEAM_LINKER_TYPE = BLOCK_TYPES.register("blood_beam_linker", singletonBlockType(BloodBeamLinker::new));
	public static BloodBeamLinker bloodBeamLinker;
	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<LightningRodExtension>> LIGHTNING_ROD_EXTENSION_TYPE = BLOCK_TYPES.register("lightning_rod_extension", singletonBlockType(LightningRodExtension::new));
	public static LightningRodExtension lightningRodExtension;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BLOCK_TIN_TYPE = BLOCK_TYPES.register("block_tin", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockTin;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BLOCK_RAW_TIN_TYPE = BLOCK_TYPES.register("block_raw_tin", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockRawTin;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> ORE_TIN_TYPE = BLOCK_TYPES.register("ore_tin", singletonBlockType(BasicBlock::new));
	public static BasicBlock oreTin;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> ORE_TIN_DEEP_TYPE = BLOCK_TYPES.register("ore_tin_deep", singletonBlockType(BasicBlock::new));
	public static BasicBlock oreTinDeep;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BLOCK_BRONZE_TYPE = BLOCK_TYPES.register("block_bronze", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockBronze;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BLOCK_RUBY_TYPE = BLOCK_TYPES.register("block_ruby", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockRuby;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> ORE_RUBY_TYPE = BLOCK_TYPES.register("ore_ruby", singletonBlockType(BasicBlock::new));
	public static BasicBlock oreRuby;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> BLOCK_COPSHOWIUM_TYPE = BLOCK_TYPES.register("block_copshowium", singletonBlockType(BasicBlock::new));
	public static BasicBlock blockCopshowium;
	//	public static final DeferredHolder<MapCodec<? extends Block>, MapCodec<BasicBlock>> ORE_VOID_TYPE = BLOCK_TYPES.register("ore_void", singletonBlockType(BasicBlock::new));
	public static BasicBlock oreVoid;

	public static BlockBehaviour.Properties getRockProperty(){
		//.forceSolidOn() prevents flowing fluid from destroying this block. Default behavior is anything with a bounding box smaller than some limit is destroyed
		return BlockBehaviour.Properties.of().mapColor(MapColor.STONE).forceSolidOn().sound(SoundType.STONE).strength(3).requiresCorrectToolForDrops();
	}

	public static BlockBehaviour.Properties getMetalProperty(){
		return BlockBehaviour.Properties.of().mapColor(MapColor.METAL).forceSolidOn().sound(SoundType.METAL).strength(3).requiresCorrectToolForDrops();
	}

	public static BlockBehaviour.Properties getGlassProperty(){
		return BlockBehaviour.Properties.of().sound(SoundType.GLASS).forceSolidOn().strength(0.5F).isValidSpawn((state, getter, pos, type) -> false).isRedstoneConductor((state, getter, pos) -> false).isSuffocating((state, getter, pos) -> false).isViewBlocking((state, getter, pos) -> false);
	}

	public static BlockBehaviour.Properties getWoodProperty(){
		return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).forceSolidOn().sound(SoundType.WOOD).strength(2, 3).ignitedByLava();
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
			public int getSignal(BlockState state, BlockGetter world, BlockPos pos, Direction side){
				return 15;
			}

			@Override
			public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
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
			public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
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
	private static void setFluidTrans(GenericFluid.FluidData... fluids){
		RenderType type = RenderType.translucent();
		for(GenericFluid.FluidData f : fluids){
			ItemBlockRenderTypes.setRenderLayer(f.getStill(), type);
			ItemBlockRenderTypes.setRenderLayer(f.getFlowing(), type);
		}
	}
}
