package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.essentials.ESConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.google.common.collect.Lists;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CRConfig{

//	public static ForgeConfigSpec.BooleanValue genCopperOre;
	public static ForgeConfigSpec.BooleanValue genTinOre;
	public static ForgeConfigSpec.BooleanValue genRubyOre;
	public static ForgeConfigSpec.BooleanValue genVoidOre;
	//	public static ForgeConfigSpec.DoubleValue rubyRarity;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> processableOres;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> gearTypes;
	public static ForgeConfigSpec.DoubleValue speedPrecision;
	public static ForgeConfigSpec.BooleanValue enchantDestruction;
	//	public static ForgeConfigSpec.ConfigValue<String> retrogen; TODO
	public static ForgeConfigSpec.BooleanValue heatEffects;
	public static ForgeConfigSpec.BooleanValue allowAllSingle;
	public static ForgeConfigSpec.BooleanValue allowAllServer;
	public static ForgeConfigSpec.BooleanValue fluxEvent;
	public static ForgeConfigSpec.IntValue gearResetTime;
	//	public static ForgeConfigSpec.BooleanValue wipeInvalidMappings;
	public static ForgeConfigSpec.BooleanValue beamPowerCollision;
	public static ForgeConfigSpec.IntValue electPerJoule;
	public static ForgeConfigSpec.BooleanValue allowHellfire;
	public static ForgeConfigSpec.IntValue voltusValue;
	public static ForgeConfigSpec.IntValue atmosEffect;
	public static ForgeConfigSpec.BooleanValue atmosLightningHorsemen;
	public static ForgeConfigSpec.IntValue atmosCap;
	public static ForgeConfigSpec.BooleanValue chargeSpawnLightning;
	public static ForgeConfigSpec.BooleanValue allowOverflow;
	public static ForgeConfigSpec.BooleanValue cccRequireTime;
	public static ForgeConfigSpec.DoubleValue rotaryLoss;
	public static ForgeConfigSpec.IntValue rotaryLossMode;
	public static ForgeConfigSpec.DoubleValue crystalAxisMult;
	public static ForgeConfigSpec.IntValue steamWorth;
	public static ForgeConfigSpec.IntValue jouleWorth;
	public static ForgeConfigSpec.DoubleValue stirlingSpeedLimit;
	public static ForgeConfigSpec.DoubleValue stirlingConversion;
	public static ForgeConfigSpec.IntValue fePerCharge;
	public static ForgeConfigSpec.IntValue stampMillDamping;
	public static ForgeConfigSpec.BooleanValue phelEffect;
	public static ForgeConfigSpec.IntValue gravRange;
	public static ForgeConfigSpec.DoubleValue gravAccel;
	public static ForgeConfigSpec.IntValue fePerEntropy;
	public static ForgeConfigSpec.BooleanValue entropyDropBlock;
	public static ForgeConfigSpec.BooleanValue rotateBeam;
	public static ForgeConfigSpec.BooleanValue teTimeAccel;
	public static ForgeConfigSpec.BooleanValue allowStatRecall;
	public static ForgeConfigSpec.IntValue recallTimeLimit;
	public static ForgeConfigSpec.BooleanValue hardGateway;
	public static ForgeConfigSpec.IntValue growMultiplier;
	public static ForgeConfigSpec.BooleanValue windingDestroy;
	public static ForgeConfigSpec.DoubleValue springGunCap;
	public static ForgeConfigSpec.DoubleValue windingResist;
	public static ForgeConfigSpec.DoubleValue whirligigHover;
	public static ForgeConfigSpec.DoubleValue whirligigSafe;
	public static ForgeConfigSpec.BooleanValue verticalBiomes;
	public static ForgeConfigSpec.BooleanValue forgetPaths;
	public static ForgeConfigSpec.DoubleValue lodestoneTurbinePower;
	public static ForgeConfigSpec.DoubleValue hamsterPower;
	public static ForgeConfigSpec.DoubleValue demonPower;
	public static ForgeConfigSpec.IntValue lodestoneDynamo;
	public static ForgeConfigSpec.IntValue fatPerValue;
	public static ForgeConfigSpec.IntValue mbPerIngot;
	public static ForgeConfigSpec.DoubleValue hellTemperature;
	public static ForgeConfigSpec.IntValue fireboxCap;
	public static ForgeConfigSpec.BooleanValue beamSounds;
	public static ForgeConfigSpec.BooleanValue electricSounds;
	public static ForgeConfigSpec.BooleanValue fluxSounds;
	public static ForgeConfigSpec.IntValue effectPacketDistance;
	public static ForgeConfigSpec.IntValue beaconHarnessPower;
	public static ForgeConfigSpec.BooleanValue technoArmorReinforce;
	public static ForgeConfigSpec.IntValue technoArmorCost;
	public static ForgeConfigSpec.BooleanValue beamDamageAbsolute;
	public static ForgeConfigSpec.BooleanValue beaconHarnessLoadSafety;
	public static ForgeConfigSpec.IntValue fluxNodeGain;
	public static ForgeConfigSpec.BooleanValue riftSpawnDrops;
	public static ForgeConfigSpec.DoubleValue enviroBootDepth;
	public static ForgeConfigSpec.IntValue enviroBootFrostWalk;
	public static ForgeConfigSpec.IntValue enviroBootSoulSpeed;
	public static ForgeConfigSpec.BooleanValue allowGateway;
	public static ForgeConfigSpec.BooleanValue allowGatewayEntities;
	public static ForgeConfigSpec.BooleanValue fluxSafeMode;
	public static ForgeConfigSpec.BooleanValue undergroundLightning;
	public static ForgeConfigSpec.BooleanValue cageMeterOverlay;
	public static ForgeConfigSpec.DoubleValue beamRaytraceStep;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> sedationBlacklist;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> cloningBlacklist;
	public static ForgeConfigSpec.DoubleValue injectionEfficiency;
	public static ForgeConfigSpec.IntValue injectionPermaPenalty;
	public static ForgeConfigSpec.IntValue hydroponicsMult;
	public static ForgeConfigSpec.IntValue respawnDelay;
	public static ForgeConfigSpec.IntValue respawnPenaltyDuration;
	public static ForgeConfigSpec.IntValue degradationPenalty;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> permanentEffectBlacklist;

	private static final Tag<Block> destroyBlacklist = BlockTags.bind(Crossroads.MODID + ":destroy_blacklist");

	private static ForgeConfigSpec clientSpec;
	private static ForgeConfigSpec serverSpec;

	private static final String CAT_INTERNAL = "Internal";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_BALANCE = "Balance";
	private static final String CAT_MISC = "Misc";
	private static final String CAT_SPECIALIZATION = "Specializations";
	private static final String CAT_TECHNOMANCY = "Technomancy";
	private static final String CAT_ALCHEMY = "Alchemy";
	private static final String CAT_WITCHCRAFT = "Witchcraft";

	protected static void init(){
		//Client config
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		rotateBeam = clientBuilder.comment("Should beams visually rotate?").define("rotate_beam", true);
//		colorChartResolution = clientBuilder.comment("Pixel size on the color chart", "Higher values will reduce FPS lag in the color chart UI, but will make it less precise and 'smooth' looking").defineInRange("color_res", 1, 1, 4);
		beamSounds = clientBuilder.comment("Should beams make sounds?").define("beam_sounds", true);
		electricSounds = clientBuilder.comment("Should electrical arcs make sounds?").define("electric_sounds", true);
		fluxSounds = clientBuilder.comment("Should temporal entropy transfer make sounds?").define("entropy_sounds", true);
		cageMeterOverlay = clientBuilder.comment("Should the overlay for the beam cage render while not holding a beam staff?", "Regardless of setting, it only shows while a beam cage is equipped.").define("beam_cage_overlay", true);
		clientSpec = clientBuilder.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);


		//Server config
		ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

		serverBuilder.push(CAT_INTERNAL);
		speedPrecision = serverBuilder.comment("Lower values increase network lag but increases gear speed synchronization").defineInRange("predict_speed", 0.20F, 0.05F, 10F);
		gearResetTime = serverBuilder.comment("Interval in ticks between gear network checks").defineInRange("network_time", 300, 100, 2400);
//		wipeInvalidMappings = serverBuilder.worldRestart().comment("Wipe internal per player dimension mappings on failure?", "Only change this if you know what you're doing").define("wipe_dim_map", false);
		mbPerIngot = serverBuilder.comment("Number of millibuckets of molten metal per ingot", "Does not change recipes").defineInRange("ingot_mb", 144, 1, 10_000);
		effectPacketDistance = serverBuilder.comment("Distance in blocks that players can see various effects from (electric arcs, beams from staffs, etc)", "Lower values will decrease the amount of packets sent").defineInRange("effect_distance", 512, 1, 512);
		beamPowerCollision = serverBuilder.comment("Whether beams decide what they can pass through based on beam power", "If true, low power beams require a smaller hole, and high power beams require a larger empty space in blocks to pass through").define("beam_collision_use_power", false);
		beaconHarnessLoadSafety = serverBuilder.comment("If enabled, the beacon harness will have an extra 'safety period' for a full color loop when the chunk is loaded", "Used to stop beacon harnesses shutting off when loading across several chunks").define("beacon_harness_load_safety", false);
		beamRaytraceStep = serverBuilder.comment("The size of the raytracing interval used by the beam staff and beam cannon", "Larger numbers cause less lag when using those devices, but are less precise").defineInRange("beam_raytrace_step", 0.25F, 0.1F, 1F);
		serverBuilder.pop();
		serverBuilder.push(CAT_ORES);
//		genCopperOre = serverBuilder.comment("Generate Copper Ore?").define("copper", true);
		genTinOre = serverBuilder.comment("Generate Tin Ore?").define("tin", true);
		genRubyOre = serverBuilder.comment("Generate Ruby Ore?").define("ruby", true);
		genVoidOre = serverBuilder.comment("Generate Void Crystal Ore?").define("void", true);
//		rubyRarity = serverBuilder.comment("Ruby ore spawn frequency", "The chance that a nether quartz ore will generate as ruby instead").defineInRange("ruby_rate", 1D / 64D, 0F, 1D);
//		retrogen = serverBuilder.comment("Retrogen Key", "Changing this value will cause retrogen. Leaving it blank disables retrogen", "Turn this off when you are done!").define("retrogen", "");
		processableOres = serverBuilder.worldRestart().comment("Metal ore types that Crossroads should generate tripling items for", "Specify the metal then a space then a hexadecimal color, ex. \"copper FF4800\"", "Doesn't register a molten fluid, recipes, or localization", "Use a datapack to register any desired recipes or localization for new materials, Find a mod already adding the desired molten fluid for the fluid").defineList("process_ores", initList("copper FF7800", "tin C8C8C8", "iron A0A0A0", "gold FFFF00"), compileRegex("\\w++ [0-9A-Fa-f]{6}+"));
		gearTypes = serverBuilder.worldRestart().comment("Metal types that Crossroads should add gears for", "Specify the metal then a space then a hexadecimal color then a space then a density in kg/m3", "Adding a new gear material requires adding localization and recipes via datapack", "Removing a default gear material is not recommended").defineList("gear_types", initList("copper FF783C 9000", "tin F0F0F0 7300", "iron A0A0A0 8000", "gold FFFF00 20000", "bronze FFA03C 8800", "copshowium FF8200 0"), compileRegex("\\w++ [0-9A-Fa-f]{6}+ [+]?\\d*\\.?[0-9]+"));
		serverBuilder.pop();
		serverBuilder.push(CAT_BALANCE);
		steamWorth = serverBuilder.comment("The number of degrees one bucket of steam is worth", "If this is changed, it is recommended to rebalance JSON recipes with steam").defineInRange("steam_value", 50, 0, Integer.MAX_VALUE);
		jouleWorth = serverBuilder.comment("The number of Joules generated from one degree worth of steam").defineInRange("joule_value", 4, 0, Integer.MAX_VALUE);
		stirlingSpeedLimit = serverBuilder.comment("The maximum speed a Stirling Engine can reach").defineInRange("stirling_limit", 2D, 0, Integer.MAX_VALUE);
		stirlingConversion = serverBuilder.comment("Stirling Engine joules per degree of heat consumed", "Stirling engines will never actually reach this efficiency- it is an unreachable upper bound").defineInRange("stirling_conversion", 20D, 0, Integer.MAX_VALUE);
		fePerCharge = serverBuilder.comment("FE generated by one unit of charge-alignment beam").defineInRange("fe_per_charge", 50, 0, Integer.MAX_VALUE);
		rotaryLoss = serverBuilder.comment("Multiplier for rotary energy loss").defineInRange("rotary_loss", 1D, 0, Integer.MAX_VALUE);
		rotaryLossMode = serverBuilder.comment("Formula for rotary power loss, where 'a' is 'rotary_loss' config", "0: No loss", "1: -a*w^2 /tick, where w is I-weighted average system speed [default]", "2: -a% of system energy /tick [Larger systems lose power faster]", "3: -a*w of gear energy /tick, for every gear [Larger systems lose power faster, relatively less loss for larger or slower gears]").defineInRange("rotary_loss_mode", 1, 0, 3);
		electPerJoule = serverBuilder.comment("FE equivalent to 1J. Set to 0 to effectively disable the dynamo and lodestone dynamo").defineInRange("dynamo_efficiency", 2,0, Integer.MAX_VALUE);
		crystalAxisMult = serverBuilder.comment("Power generated by the Crystal Master Axis in J/t").defineInRange("crystal_power", 100D, 0, Integer.MAX_VALUE);
		growMultiplier = serverBuilder.comment("Power divider for potential beams grow effect", "For example, 2 will cause twice as much beam power for the same effect").defineInRange("grow_divider", 1, 1, 64);
		windingDestroy = serverBuilder.comment("Whether to destroy items when failing to wind them in a Winding Table").define("winding_destroy", true);
		windingResist = serverBuilder.comment("Resistive power exerted by the Winding Table per speed of the wound item").defineInRange("winding_resist", 20D, 0D, 1_000D);
		springGunCap = serverBuilder.comment("The maximum speed on a Spring Gun", "Higher maximum speeds increase maximum damage").defineInRange("spring_gun_cap", 10D, 1D, 100D);
		lodestoneTurbinePower = serverBuilder.comment("The power produced by the Lodestone Turbine (in J/t)").defineInRange("lodestone_power", 15D, 0D, 100D);
		hamsterPower = serverBuilder.comment("The power produced by the Hamster Wheel (in J/t)").defineInRange("hamster_power", 5D, 0D, 100D);
		demonPower = serverBuilder.comment("The power produced by the Maxwell's Demon (both hot and cold, in degrees C/t)").defineInRange("demon_power", 5D, 0D, 100D);
		fireboxCap = serverBuilder.comment("Maximum fuel burn time in the Firebox. Set to -1 to remove the limit").defineInRange("firebox_cap", 4000, -1, Integer.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.push(CAT_MISC);
		heatEffects = serverBuilder.comment("Enable overheat effects?", "If false, all heat cable overheating effects are replaced with burning").define("cable_effects", true);
		whirligigHover = serverBuilder.comment("Minimum spring speed to hover with the Whirligig", "If set at or above 10, gaining elevation with a Whirligig is disabled").defineInRange("whirligig_hover", 6D, 1D, 100D);
		whirligigSafe = serverBuilder.comment("Minimum spring speed to eliminate fall damage with the Whirligig", "If set at or above 10, gaining the Whirligig can not eliminate fall damage, but can reduce it").defineInRange("whirligig_safe", 4D, 1D, 100D);
		chargeSpawnLightning = serverBuilder.comment("Whether the charge alignment can summon lightning").define("charge_lightning", true);
		stampMillDamping = serverBuilder.comment("Percentage of Stamp Mill progress to be lost on failure", "Effectively nerfs ore-tripling").defineInRange("mill_damping", 0, 0, 100);
		enchantDestruction = serverBuilder.comment("Whether Enchantment beams have a chance to destroy items").define("enchant_destroy", true);
		fatPerValue = serverBuilder.comment("Amount of liquid fat equivalent to 1 hunger or saturation (in millibuckets)").defineInRange("fat_cost", 100, 1, 10_000);
		hellTemperature = serverBuilder.comment("Minimum temperature of nether biomes (in degrees C)").defineInRange("nether_temp", 60, 0, 1_000D);
		beamDamageAbsolute = serverBuilder.comment("Whether void-potential (death) beams do absolute damage", "Absolute damage ignores potion effects and enchantments").define("beam_damage_absolute", false);
		undergroundLightning = serverBuilder.comment("Whether Charge beams can summon lightning underground or under a roof").define("underground_lightning", false);
		serverBuilder.pop();

		//Category includes overall path controls, and path specific categories
		serverBuilder.push(CAT_SPECIALIZATION);
		allowAllSingle = serverBuilder.comment("Allow Multiple specializations per player in Singleplayer?").define("paths_single", true);
		allowAllServer = serverBuilder.comment("Allow Multiple specializations per player in Multiplayer?").define("paths_multi", false);
		forgetPaths = serverBuilder.comment("Allow forgetting paths using Path Sigils?").define("path_forget", false);
		serverBuilder.push(CAT_TECHNOMANCY);
		entropyDropBlock = serverBuilder.comment("Whether Technomancy machines should drop an item when overloaded").define("drop_machine", false);
		fluxEvent = serverBuilder.comment("Allow Temporal Entropy disasters from Technomancy?", "If disabled, disasters create a small explosion instead").define("flux_disaster", true);
		fePerEntropy = serverBuilder.comment("FE equal to 1 Temporal Entropy").defineInRange("fe_per_entropy", 50, 1, Integer.MAX_VALUE);
		teTimeAccel = serverBuilder.comment("Allow time acceleration of Tile Entities?", "Disabling this does not affect acceleration of normal entities or block ticks").define("te_accel", true);
		hardGateway = serverBuilder.comment("Enable hardmode for the Gateway?", "If true, dialing in chevrons only works if the beam alignment matches the chevron being dialed", "Enable this if you want an extra challenge").define("gateway_hard", false);
		allowOverflow = serverBuilder.comment("Destroy the CCC if Copshowium overfills the tank?", "Disabling this will make the CCC much easier to use").define("allow_overflow", true);
		cccRequireTime = serverBuilder.comment("Does the CCC require specifically a Time beam?").define("ccc_req_time", true);
		allowStatRecall = serverBuilder.comment("Should recalling restore previous health and hunger?", "Recommended to disable this for PvP").define("stat_recall", true);
		recallTimeLimit = serverBuilder.comment("Maximum time duration for recalling, in seconds. Set to -1 to disable limit, 0 to disable recalling").defineInRange("time_recall", 60*5, -1, 60*60*24);
		lodestoneDynamo = serverBuilder.comment("Power output of the Lodestone Dynamo (in J/t)", "Set to 0 to disable the Lodestone Dynamo").defineInRange("lodestone_dynamo", 100, 0, 1_000);
		beaconHarnessPower = serverBuilder.comment("Beam power output of the beacon harness").defineInRange("beacon_harness_power", 512, 8, 4096);
		technoArmorReinforce = serverBuilder.comment("Allow adding Netherite armor to Technomancy armor?").define("techno_armor_reinforce", true);
		technoArmorCost = serverBuilder.comment("Multiplier for XP levels to upgrade Technomancy armor").defineInRange("techno_armor_xp", 1, 0, 100);
		fluxNodeGain = serverBuilder.comment("Temporal Entropy gained per Entropy Node when transferring", "Higher values are more difficult, and encourage using fewer node chains").defineInRange("entropy_node_gain", 2, 0, 64);
		enviroBootDepth = serverBuilder.comment("Speed boost from All-Terrain Boots in water (swim speed boost)", "Set to 0 to disable").defineInRange("enviro_boot_depth", 3F, 0F, 100F);
		enviroBootFrostWalk = serverBuilder.comment("Level of the All-Terrain Boots frost walker effect", "Higher levels increase freezing range", "Set to 0 to disable").defineInRange("enviro_boot_frost", 2, 0, 10);
		enviroBootSoulSpeed = serverBuilder.comment("Level of the All-Terrain Boots soul speed effect", "Set to 0 to disable").defineInRange("enviro_boot_soul", 3, 0, 10);
		allowGateway = serverBuilder.comment("Whether Gateways can teleport entities at all").define("allow_gateway", true);
		allowGatewayEntities = serverBuilder.comment("Whether Gateways can teleport entities that aren't players", "If false, players can still use Gateways").define("allow_gateway_ent", true);
		fluxSafeMode = serverBuilder.comment("If enabled, machines will NOT break/explode/cause damage when overfilling on Temporal Entropy", "Machines which overfill on Temporal Entropy will shut down instead").define("flux_safe", false);
		serverBuilder.pop();
		serverBuilder.push(CAT_ALCHEMY);
		phelEffect = serverBuilder.comment("Allow the full effect of Phelostogen?", "If disabled Phelostogen lights a single small fire instead").define("phel_effect", true);
		allowHellfire = serverBuilder.comment("Whether to allow Ignis Infernum", "If disabled, Ignis Infernum is still craftable, but it gets nerfed to Phelostogen level").define("ignis_infernum", true);
		atmosEffect = serverBuilder.comment("Level of effects from overcharging the atmosphere", "0: No negative effects", "1: Allow lightning strikes", "2: Allow creeper charging", "3: Allow lightning strikes & creeper charging").defineInRange("atmos_effects", 3, 0, 3);
		atmosLightningHorsemen = serverBuilder.comment("Whether lightning bolts from atmospheric overcharging can spawn the 4 horsemen", "Overriden by gamerules").define("atmos_horses", true);
		atmosCap = serverBuilder.comment("Maximum charge for the atmosphere").defineInRange("charge_limit", 1_000_000_000, 0, 2_000_000_000);
		voltusValue = serverBuilder.comment("FE produced by one Voltus").defineInRange("voltus_power", 2_000, 0, 100_000);
		gravRange = serverBuilder.comment("Range of Density Plates").defineInRange("grav_range", 64, 0, 128);
		gravAccel = serverBuilder.comment("Acceleration of Density Plates", "In blocks/tick/tick, where normal gravity ~0.08").defineInRange("grav_accel", 0.15D, 0, 10D);
		verticalBiomes = serverBuilder.comment("Whether to change biomes in columns", "If true, biomes are transmuted in a column from bedrock to worldheight", "If false, biomes are transmitted only within the vertical bounds of the effect").define("vertical_biomes", true);
		riftSpawnDrops = serverBuilder.comment("If true, rift beams will spawn mob drops instead of actual mobs", "Rift beams do this regardless of config setting in peaceful mode").define("rift_drops", false);
		serverBuilder.pop();
		serverBuilder.push(CAT_WITCHCRAFT);
		sedationBlacklist = serverBuilder.comment("Specify entities that can not have their AI disabled by sedation. Players can never be fully sedated", "Format of 'domain:entity_id', ex. minecraft:pig").defineList("sedation_blacklist", Lists.newArrayList("minecraft:player", "minecraft:wither", "minecraft:ender_dragon"), (Object entry) -> entry instanceof String);
		cloningBlacklist = serverBuilder.comment("Specify entities which can not be cloned. Players can never be cloned", "Format of 'domain:entity_id', ex. minecraft:pig").defineList("cloning_blacklist", Lists.newArrayList("minecraft:player", "minecraft:wither", "minecraft:ender_dragon"), (Object entry) -> entry instanceof String);
		permanentEffectBlacklist = serverBuilder.comment("Specify potion effects that can not be applied permanently to an entity or player", "Format of 'domain:potion_effect_id', ex. minecraft:health_boost").defineList("permanent_effect_blacklist", Lists.newArrayList("minecraft:health_boost", "minecraft:glowing"), (Object entry) -> entry instanceof String);
		injectionEfficiency = serverBuilder.comment("The duration of injected potions vs drinking them", "Setting to 1 or below makes injection equivalent to normal potions").defineInRange("injection_efficiency", 2F, 1F, 100F);
		injectionPermaPenalty = serverBuilder.comment("The permanent maximum health reduction for each injected permanent potion effect", "Set to 0 or lower to disable the penalty", "Set to 20 or higher to effectively disable permanent injection for players").defineInRange("injection_perma_penalty", 2, 0, 100);
		degradationPenalty = serverBuilder.comment("The reduction in maximum health for each point of degradation on a clone").defineInRange("degradation_penalty", 2, 0, 100);
		hydroponicsMult = serverBuilder.comment("Production/growth speed multiplier for the Hydroponics Trough compared to normal crop growth", "Setting to 0 will effectively disable the machine").defineInRange("hydroponics_mult", 16, 0, 100);
		respawnDelay = serverBuilder.comment("Time in seconds for genetically modified entities to respawn", "Set to 0 or a negative value to disable respawning").defineInRange("respawn_delay", 30, -1, Short.MAX_VALUE);
		respawnPenaltyDuration = serverBuilder.comment("Time in seconds for the debuffs on respawned entities", "Also controls the vulnerability period. Cannot be disabled").defineInRange("respawn_penalty_duration", 30, 1, Short.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.pop();

		serverSpec = serverBuilder.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, serverSpec);
	}

	@SafeVarargs
	private static <T> List<T> initList(T... args){
		List<T> l = new ArrayList<>(args.length);
		Collections.addAll(l, args);
		return l;
	}

	private static Predicate<Object> compileRegex(String regex){
		Pattern p = Pattern.compile(regex);
		return (Object o) -> {
			return o instanceof String && p.matcher((String) o).matches();
		};
	}

	/**
	 * @param world The world (currently unused, may change)
	 * @param pos The current block position (currently unused, may change)
	 * @param state The blockstate being destroyed/modified
	 * @return Whether the block is protected via the config from destruction
	 */
	public static boolean isProtected(Level world, BlockPos pos, BlockState state){
		return destroyBlacklist.contains(state.getBlock());
	}

	protected static void load(){
		CommentedFileConfig clientConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(Crossroads.MODID + "-client.toml")).sync().autosave().writingMode(WritingMode.REPLACE).build();
		clientConfig.load();
		clientSpec.setConfig(clientConfig);

		CommentedFileConfig serverConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(Crossroads.MODID + "-server.toml")).sync().autosave().writingMode(WritingMode.REPLACE).build();
		serverConfig.load();
		serverSpec.setConfig(serverConfig);
	}

	public static String formatVal(double d){
		return formatVal((float) d);
	}

	public static String formatVal(float f){
		return ESConfig.formatFloat(f, null);
	}
}
