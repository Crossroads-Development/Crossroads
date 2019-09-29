package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.loading.FMLPaths;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class CrossroadsConfig{

	public static ForgeConfigSpec.BooleanValue genCopperOre;
	public static ForgeConfigSpec.BooleanValue genTinOre;
	public static ForgeConfigSpec.BooleanValue genRubyOre;
	public static ForgeConfigSpec.BooleanValue genVoidOre;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> processableOres;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> gearTypes;
	public static ForgeConfigSpec.DoubleValue speedPrecision;
	public static ForgeConfigSpec.ConfigValue<String> retrogen;
	public static ForgeConfigSpec.BooleanValue heatEffects;
	public static ForgeConfigSpec.BooleanValue allowAllSingle;
	public static ForgeConfigSpec.BooleanValue allowAllServer;
	public static ForgeConfigSpec.BooleanValue technomancy;
	public static ForgeConfigSpec.BooleanValue alchemy;
	//	public static ForgeConfigSpec.BooleanValue witchcraft;//NYI
	public static ForgeConfigSpec.BooleanValue voidChunk;
	public static ForgeConfigSpec.BooleanValue resetChunk;
	public static ForgeConfigSpec.BooleanValue magicChunk;
	public static ForgeConfigSpec.BooleanValue blastChunk;
	public static ForgeConfigSpec.BooleanValue disableSlaves;
	public static ForgeConfigSpec.IntValue gearResetTime;
	public static ForgeConfigSpec.BooleanValue wipeInvalidMappings;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blockedPrototype;
	public static ForgeConfigSpec.IntValue allowPrototype;
	public static ForgeConfigSpec.IntValue maximumPistolDamage;
	public static ForgeConfigSpec.IntValue electPerJoule;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> growBlacklist;
	public static ForgeConfigSpec.BooleanValue allowHellfire;
	public static ForgeConfigSpec.DoubleValue voltusUsage;
	public static ForgeConfigSpec.IntValue atmosEffect;
	public static ForgeConfigSpec.LongValue atmosCap;
	//	public static ForgeConfigSpec.-Value documentCrafttweaker;
	public static ForgeConfigSpec.BooleanValue addBoboRecipes;
	public static ForgeConfigSpec.ConfigValue<String> cccExpenLiquid;
	public static ForgeConfigSpec.ConfigValue<String> cccEntropLiquid;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> destroyBlacklist;
	public static ForgeConfigSpec.DoubleValue rotaryLoss;
	public static ForgeConfigSpec.DoubleValue crystalAxisMult;
	public static ForgeConfigSpec.IntValue steamWorth;
	public static ForgeConfigSpec.IntValue jouleWorth;
	public static ForgeConfigSpec.DoubleValue stirlingSpeedLimit;
	public static ForgeConfigSpec.DoubleValue stirlingMultiplier;
	public static ForgeConfigSpec.IntValue fePerCharge;
	//	public static ForgeConfigSpec.-Value redstoneTransmitterRange;
	public static ForgeConfigSpec.IntValue stampMillDamping;
	public static ForgeConfigSpec.BooleanValue bedrockDust;
	public static ForgeConfigSpec.BooleanValue phelEffect;
	public static ForgeConfigSpec.IntValue gravRange;
	public static ForgeConfigSpec.IntValue fePerEntropy;
	public static ForgeConfigSpec.IntValue entropyDecayRate;
	public static ForgeConfigSpec.BooleanValue entropyDropBlock;

	public static ForgeConfigSpec.BooleanValue rotateBeam;

//	public static ArrayList<ForgeConfigSpec.-Value> boboItemProperties = new ArrayList<>();

	private static ForgeConfigSpec clientSpec;
	private static ForgeConfigSpec serverSpec;

	private static final String CAT_INTERNAL = "Internal";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_BALANCE = "Balance";
	private static final String CAT_MISC = "Misc";
	private static final String CAT_SPECIALIZATION = "Specializations";
	private static final String CAT_TECHNOMANCY = "Technomancy";
	private static final String CAT_ALCHEMY = "Alchemy";
	public static final String CAT_BOBO = "Bobo Features";

	protected static void init(){
		//Client config
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		rotateBeam = clientBuilder.comment("Should beams visually rotate").define("rotate_beam", true);

		clientSpec = clientBuilder.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);


		//Server config
		ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

		serverBuilder.push(CAT_INTERNAL);
		speedPrecision = serverBuilder.comment("Lower values increase network lag but increases gear speed synchronization").defineInRange("speed_precision", 0.5F, 0.05F, 2F);
		gearResetTime = serverBuilder.comment("Interval in ticks between gear network checks").defineInRange("network_time", 300, 100, 2400);
		wipeInvalidMappings = serverBuilder.comment("Wipe internal per player dimension mappings on failure?", "Only change this if you know what you're doing").define("wipe_dim_map", false);
//		disableSlaves = serverBuilder.comment("Freeze slave axes", "If you are crashing from StackOverflow errors and you either have a tiny amount of RAM or built an insanely large & complicated rotary setup, then set this to true, destroy the setup, and set this to false. Also maybe send me a picture of the setup").define("freeze_slave");
//		destroyBlacklist = serverBuilder.comment("Blocks that Crossroads shouldn't be able to destroy or replace. Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'.", new String[] {"minecraft:barrier", "minecraft:command_block", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:nether_portal", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:end_gateway", "minecraft:structure_block", "minecraft:structure_void"});
		serverBuilder.pop();
		serverBuilder.push(CAT_ORES);
		genCopperOre = serverBuilder.comment("Generate Copper Ore?").define("copper", true);
		genTinOre = serverBuilder.comment("Generate Tin Ore?").define("tin", true);
		genRubyOre = serverBuilder.comment("Generate Ruby Ore?").define("ruby", true);
		genVoidOre = serverBuilder.comment("Generate Void Crystal Ore?").define("void", true);
		retrogen = serverBuilder.comment("Retrogen Key", "Changing this value will cause retrogen. Leaving it blank disables retrogen", "Turn this off when you are done!").define("retrogen", "");
		processableOres = serverBuilder.comment("Metal ore types that Crossroads machines should be able to process", "Specify the metal then a space then a hexadecimal color, ex. \"copper FF4800\"").define("process_ores", initList("copper FF7800", "tin C8C8C8", "iron A0A0A0", "gold FFFF00"), compileRegex("\\w++ \\p{XDigit}{6}+"));
		gearTypes = serverBuilder.comment("Metal types that Crossroads should add gears for", "Specify the metal then a space then a hexadecimal color then a space then a density in kg/m3, ex. \"copper FF783C 9000\"").define("gear_types", initList("copper FF783C 9000", "tin F0F0F0 7300", "iron A0A0A0 8000", "gold FFFF00 20000", "bronze FFA03C 8800", "copshowium FF8200 0", "lead 74699E 11000", "silver BDF3EE 10000", "nickel F1F2C4 9000", "invar DFEDD8 8000", "platinum 74F5FF 21000", "electrum FEFF8A 15000"), compileRegex("\\w++ \\p{XDigit}{6}+ [+]?[0-9]*\\.?[0-9]+"));
		serverBuilder.pop();
		serverBuilder.push(CAT_BALANCE);
		steamWorth = serverBuilder.comment("The number of degrees one bucket of steam is worth").defineInRange("steam_value", 50, 0, Integer.MAX_VALUE);
		jouleWorth = serverBuilder.comment("The number of Joules generated from one degree worth of steam").defineInRange("joule_value", 4, 0, Integer.MAX_VALUE);
		stirlingSpeedLimit = serverBuilder.comment("The maximum speed a Stirling Engine can reach").defineInRange("stirling_limit", 1D, 0, Integer.MAX_VALUE);
		stirlingMultiplier = serverBuilder.comment("Multiplier for Stirling Engine power output").defineInRange("stirling_multiplier", 2.5D, 0, Integer.MAX_VALUE);
		fePerCharge = serverBuilder.comment("FE generated by one unit of charge-alignment beam").defineInRange("fe_per_charge", 2, 0, Integer.MAX_VALUE);
		rotaryLoss = serverBuilder.comment("Multiplier for rotary energy loss").defineInRange("rotary_loss", 1D, 0, Integer.MAX_VALUE);
		electPerJoule = serverBuilder.comment("FE generated from 1J. Set to 0 to effectively disable the dynamo").defineInRange("dynamo_efficiency", 20,0, Integer.MAX_VALUE);
		crystalAxisMult = serverBuilder.comment("Power generated by the Crystal Master Axis in J/t").defineInRange("crystal_power", 100D, 0, Integer.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.push(CAT_MISC);
		growBlacklist = serverBuilder.comment("Plant types that can not be grown by a potential beam. Should be in format 'modid:blockregistryname', ex. minecraft:wheat").defineList("ungrowable", new ArrayList<>(0), compileRegex("[^\\:\\n\\t ]+?:[^\\:\\n\\t ]+"));
		heatEffects = serverBuilder.comment("Enable overheat effects?", "If false, all heat cable overheating effects are replaced with burning").define("cable_effects", true);
//		redstoneTransmitterRange = serverBuilder.comment("Range of Wireless Redstone Transmitters and Receivers", 16, "Default 16, Range: 0-256", 0, 256);
		serverBuilder.pop();
		serverBuilder.push(CAT_SPECIALIZATION);
		allowAllSingle = serverBuilder.comment("Allow Multiple specializations per player in SinglePlayer?").define("paths_single", true);
		allowAllServer = serverBuilder.comment("Allow Multiple specializations per player in MultiPlayer?").define("paths_multi", false);
		technomancy = serverBuilder.comment("Enable Technomancy?").define("technomancy", true);
		alchemy = serverBuilder.comment("Enable Alchemy?").define("alchemy", true);
//		witchcraft = serverBuilder.comment("Enable Witchcraft", true, "Default: true; NYI");
		serverBuilder.push(CAT_TECHNOMANCY);
		entropyDecayRate = serverBuilder.comment("Natural Temporal Entropy decay rate").defineInRange("entropy_decay", 1, 0, EntropySavedData.MAX_VALUE);
		entropyDropBlock = serverBuilder.comment("Whether Technomancy machines should drop an item when overloaded").define("drop_machine", false);
		voidChunk = serverBuilder.comment("Allow Chunk Voiding disaster from Technomancy?").define("chunk_void", true);
		resetChunk = serverBuilder.comment("Allow Chunk Reset disaster from Technomancy?").define("chunk_reset", true);
		magicChunk = serverBuilder.comment("Allow Chunk Misc disaster from Technomancy?").define("chunk_beam", true);
		blastChunk = serverBuilder.comment("Allow Explosion disaster from Technomancy?").define("chunk_explode", true);
		blockedPrototype = serverBuilder.comment("Blocks disallowed to be used in prototypes", "Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'", "Use to prevent exploits, bugs, travel to the prototype dimension, griefing, and other naughty things. Also, most modded multiblocks should be blocked to prevent bugs.").define("proto_banned", initList(Crossroads.MODID + ":large_gear_slave", Crossroads.MODID + ":large_gear_master", Crossroads.MODID + ":prototype", Crossroads.MODID + ":gateway_frame", "minecraft:portal", "rftools:matter_transmitter", "bloodmagic:blockteleposer"), compileRegex("[^\\:\\n\\t ]+?:[^\\:\\n\\t ]+"));
		allowPrototype = serverBuilder.comment("Restrictions on prototyping", "-1: Prototyping is disabled. May block large amounts of the mod.", "0: Default value.", "1: Prototyping destroys the template structure the prototype was made from instead of copying the template. (prevents unintended dupe exploits)", "2: Prototyping works as normal, except prototype blocks themselves cannot be placed, only used within other compatible devices (such as the watch)").defineInRange("proto_mode", 0, -1, 2);
		maximumPistolDamage = serverBuilder.comment("Maximum pistol damage per shot", "-1 for no cap").defineInRange("pistol_cap", -1, -1, Integer.MAX_VALUE);
		cccExpenLiquid = serverBuilder.comment("Liquid type for the Copshowium Creation Chamber without Temporal Entropy", "An invalid liquid will disable the crafting").define("ccc_expen", "copper");
		cccEntropLiquid = serverBuilder.comment("Liquid type for the Copshowium Creation Chamber with Temporal Entropy", "An invalid liquid will disable the crafting").define("ccc_cheap", "distilled_water");
		fePerEntropy = serverBuilder.comment("FE equal to 1 Temporal Entropy").defineInRange("fe_per_entropy", 50, 1, Integer.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.push(CAT_INTERNAL);
		allowHellfire = serverBuilder.comment("Whether to allow crafting Ignis Infernum").define("ignis_infernum", true);
		atmosEffect = serverBuilder.comment("Level of effects from overcharging the atmosphere", "0: No negative effects", "1: Allow lightning strikes", "2: Allow creeper charging", "3: Allow lightning strikes & creeper charging").defineInRange("atmos_effects", 3, 0, 3);
		atmosCap = serverBuilder.comment("Maximum charge for the atmosphere").defineInRange("charge_limit", 1_000_000_000L, 0L, 2_000_000_000L);
		voltusUsage = serverBuilder.comment("Voltus used to produce 1000FE of charge in the atmosphere").defineInRange("voltus_power", 0.1D, 0, Integer.MAX_VALUE);
		stampMillDamping = serverBuilder.comment("Percentage of Stamp Mill progress to be lost on failure", "Effectively nerfs ore-tripling").defineInRange("mill_damping", 0, 0, 100);
		bedrockDust = serverBuilder.comment("Bedrock craftability", "Can bedrock be crafted from bedrock dust?").define("bedrock_dust", true);
		phelEffect = serverBuilder.comment("Allow the full effect of phelostogen?", "If disabled phelostogen lights a single fire instead").define("phel_effect", true);
		gravRange = serverBuilder.comment("Range of Density Plates").defineInRange("grav_range", 64, 0, 128);
		serverBuilder.pop();
		serverBuilder.pop();
		serverBuilder.push(CAT_BOBO);
		addBoboRecipes = serverBuilder.comment("Add recipes for bobo items").define("bb_recipes", true);
//		boboItemProperties.add(heatEffects);
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
		return (Object o) -> o instanceof String && p.matcher((String) o).matches();
	}

	/**
	 * @param world The world (currently unused, may change)
	 * @param pos The current block position (currently unused, may change)
	 * @param state The blockstate
	 * @return Whether the block is protected via the config from destruction
	 */
	public static boolean isProtected(World world, BlockPos pos, BlockState state){
		String id = state.getBlock().getRegistryName().toString();
		for(String s : destroyBlacklist.get()){
			if(s.equals(id)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the value of the ForgeConfigSpec.-Value, but if render side it will return the server side value if the ForgeConfigSpec.-Value is in the list SYNCED_PROPERTIES.
	 *
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static String[] getConfigStringList(ForgeConfigSpec.ConfigValue<List<? extends String>> prop, boolean client){
		return (String[]) prop.get().toArray();
	}

	protected static void load(){
		CommentedFileConfig clientConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(Crossroads.MODID + "-client.toml")).sync().autosave().writingMode(WritingMode.REPLACE).build();
		clientConfig.load();
		clientSpec.setConfig(clientConfig);

		CommentedFileConfig serverConfig = CommentedFileConfig.builder(FMLPaths.CONFIGDIR.get().resolve(Crossroads.MODID + "-server.toml")).sync().autosave().writingMode(WritingMode.REPLACE).build();
		serverConfig.load();
		serverSpec.setConfig(serverConfig);
	}
}
