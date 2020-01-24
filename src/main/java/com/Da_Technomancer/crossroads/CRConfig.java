package com.Da_Technomancer.crossroads;

import com.Da_Technomancer.essentials.EssentialsConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
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

public class CRConfig{

	public static ForgeConfigSpec.BooleanValue genCopperOre;
	public static ForgeConfigSpec.BooleanValue genTinOre;
	public static ForgeConfigSpec.BooleanValue genRubyOre;
	public static ForgeConfigSpec.BooleanValue genVoidOre;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> processableOres;
	public static ForgeConfigSpec.ConfigValue<List<? extends String>> gearTypes;
	public static ForgeConfigSpec.DoubleValue speedPrecision;
//	public static ForgeConfigSpec.ConfigValue<String> retrogen; TODO
	public static ForgeConfigSpec.BooleanValue heatEffects;
	public static ForgeConfigSpec.BooleanValue allowAllSingle;
	public static ForgeConfigSpec.BooleanValue allowAllServer;
//	public static ForgeConfigSpec.BooleanValue technomancy; Moved to tag controlling unlock item
//	public static ForgeConfigSpec.BooleanValue alchemy;
//	public static ForgeConfigSpec.BooleanValue witchcraft;//NYI
//	public static ForgeConfigSpec.BooleanValue voidChunk;
//	public static ForgeConfigSpec.BooleanValue resetChunk;
//	public static ForgeConfigSpec.BooleanValue magicChunk;
//	public static ForgeConfigSpec.BooleanValue blastChunk;
	public static ForgeConfigSpec.BooleanValue fluxEvent;
	public static ForgeConfigSpec.BooleanValue disableSlaves;
	public static ForgeConfigSpec.IntValue gearResetTime;
	public static ForgeConfigSpec.BooleanValue wipeInvalidMappings;
//	public static ForgeConfigSpec.ConfigValue<List<? extends String>> blockedPrototype;
//	public static ForgeConfigSpec.IntValue allowPrototype;
//	public static ForgeConfigSpec.IntValue maximumPistolDamage;
	public static ForgeConfigSpec.IntValue electPerJoule;
	public static ForgeConfigSpec.BooleanValue allowHellfire;
	public static ForgeConfigSpec.IntValue voltusValue;
	public static ForgeConfigSpec.IntValue atmosEffect;
	public static ForgeConfigSpec.IntValue atmosCap;
//	public static ForgeConfigSpec.BooleanValue addBoboRecipes;
//	public static ForgeConfigSpec.ConfigValue<String> cccExpenLiquid; TODO move to tag
//	public static ForgeConfigSpec.ConfigValue<String> cccEntropLiquid;
	public static ForgeConfigSpec.DoubleValue rotaryLoss;
	public static ForgeConfigSpec.DoubleValue crystalAxisMult;
	public static ForgeConfigSpec.IntValue steamWorth;
	public static ForgeConfigSpec.IntValue jouleWorth;
	public static ForgeConfigSpec.DoubleValue stirlingSpeedLimit;
	public static ForgeConfigSpec.DoubleValue stirlingMultiplier;
	public static ForgeConfigSpec.IntValue fePerCharge;
	public static ForgeConfigSpec.IntValue stampMillDamping;
//	public static ForgeConfigSpec.BooleanValue bedrockDust;
	public static ForgeConfigSpec.BooleanValue phelEffect;
	public static ForgeConfigSpec.IntValue gravRange;
	public static ForgeConfigSpec.IntValue fePerEntropy;
//	public static ForgeConfigSpec.IntValue entropyDecayRate;
	public static ForgeConfigSpec.BooleanValue entropyDropBlock;
	public static ForgeConfigSpec.BooleanValue rotateBeam;

	private static final Tag<Block> destroyBlacklist = new BlockTags.Wrapper(new ResourceLocation(Crossroads.MODID, "destroy_blacklist"));

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

	protected static void init(){
		//Client config
		ForgeConfigSpec.Builder clientBuilder = new ForgeConfigSpec.Builder();

		rotateBeam = clientBuilder.comment("Should beams visually rotate?").define("rotate_beam", true);

		clientSpec = clientBuilder.build();
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, clientSpec);


		//Server config
		ForgeConfigSpec.Builder serverBuilder = new ForgeConfigSpec.Builder();

		serverBuilder.push(CAT_INTERNAL);
		speedPrecision = serverBuilder.comment("Lower values increase network lag but increases gear speed synchronization").defineInRange("speed_precision", 0.5F, 0.05F, 2F);
		gearResetTime = serverBuilder.comment("Interval in ticks between gear network checks").defineInRange("network_time", 300, 100, 2400);
		wipeInvalidMappings = serverBuilder.worldRestart().comment("Wipe internal per player dimension mappings on failure?", "Only change this if you know what you're doing").define("wipe_dim_map", false);
		serverBuilder.pop();
		serverBuilder.push(CAT_ORES);
		genCopperOre = serverBuilder.comment("Generate Copper Ore?").define("copper", true);
		genTinOre = serverBuilder.comment("Generate Tin Ore?").define("tin", true);
		genRubyOre = serverBuilder.comment("Generate Ruby Ore?").define("ruby", true);
		genVoidOre = serverBuilder.comment("Generate Void Crystal Ore?").define("void", true);
//		retrogen = serverBuilder.comment("Retrogen Key", "Changing this value will cause retrogen. Leaving it blank disables retrogen", "Turn this off when you are done!").define("retrogen", "");
		processableOres = serverBuilder.worldRestart().comment("Metal ore types that Crossroads should generate tripling items for", "Specify the metal then a space then a hexadecimal color, ex. \"copper FF4800\"", "Doesn't register a molten fluid or recipes").define("process_ores", initList("copper FF7800", "tin C8C8C8", "iron A0A0A0", "gold FFFF00"), compileRegex("\\w++ [0-9A-Fa-f]{6}+"));
		gearTypes = serverBuilder.worldRestart().comment("Metal types that Crossroads should add gears for", "Specify the metal then a space then a hexadecimal color then a space then a density in kg/m3", "Adding a new gear material requires adding localization and recipes via datapack", "Removing a default gear material is not recommended").define("gear_types", initList("copper FF783C 9000", "tin F0F0F0 7300", "iron A0A0A0 8000", "gold FFFF00 20000", "bronze FFA03C 8800", "copshowium FF8200 0"), compileRegex("\\w++ [0-9A-Fa-f]{6}+ [+]?\\d*\\.?[0-9]+"));
		serverBuilder.pop();
		serverBuilder.push(CAT_BALANCE);
		steamWorth = serverBuilder.comment("The number of degrees one bucket of steam is worth", "If this is changed, it is recommended to rebalance JSON recipes with steam").defineInRange("steam_value", 50, 0, Integer.MAX_VALUE);
		jouleWorth = serverBuilder.comment("The number of Joules generated from one degree worth of steam").defineInRange("joule_value", 4, 0, Integer.MAX_VALUE);
		stirlingSpeedLimit = serverBuilder.comment("The maximum speed a Stirling Engine can reach").defineInRange("stirling_limit", 1D, 0, Integer.MAX_VALUE);
		stirlingMultiplier = serverBuilder.comment("Multiplier for Stirling Engine power output").defineInRange("stirling_multiplier", 2.5D, 0, Integer.MAX_VALUE);
		fePerCharge = serverBuilder.comment("FE generated by one unit of charge-alignment beam").defineInRange("fe_per_charge", 2, 0, Integer.MAX_VALUE);
		rotaryLoss = serverBuilder.comment("Multiplier for rotary energy loss").defineInRange("rotary_loss", 1D, 0, Integer.MAX_VALUE);
		electPerJoule = serverBuilder.comment("FE generated from 1J. Set to 0 to effectively disable the dynamo").defineInRange("dynamo_efficiency", 20,0, Integer.MAX_VALUE);
		crystalAxisMult = serverBuilder.comment("Power generated by the Crystal Master Axis in J/t").defineInRange("crystal_power", 100D, 0, Integer.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.push(CAT_MISC);
		heatEffects = serverBuilder.comment("Enable overheat effects?", "If false, all heat cable overheating effects are replaced with burning").define("cable_effects", true);
		serverBuilder.pop();

		//Category includes overall path controls, and path specific categories
		serverBuilder.push(CAT_SPECIALIZATION);
		allowAllSingle = serverBuilder.comment("Allow Multiple specializations per player in Singleplayer?").define("paths_single", true);
		allowAllServer = serverBuilder.comment("Allow Multiple specializations per player in Multiplayer?").define("paths_multi", false);
//		technomancy = serverBuilder.comment("Enable unlocking Technomancy?").define("technomancy", true);
//		alchemy = serverBuilder.comment("Enable unlocking Alchemy?").define("alchemy", true);
//		witchcraft = serverBuilder.comment("Enable unlocking Witchcraft?", false, "Default: true; NYI");
		serverBuilder.push(CAT_TECHNOMANCY);
//		entropyDecayRate = serverBuilder.comment("Natural Temporal Entropy decay rate").defineInRange("entropy_decay", 1, 0, EntropySavedData.MAX_VALUE);
		entropyDropBlock = serverBuilder.comment("Whether Technomancy machines should drop an item when overloaded").define("drop_machine", false);
//		voidChunk = serverBuilder.comment("Allow Chunk Voiding disaster from Technomancy?").define("chunk_void", true);
//		resetChunk = serverBuilder.comment("Allow Chunk Reset disaster from Technomancy?").define("chunk_reset", true);
//		magicChunk = serverBuilder.comment("Allow Chunk Misc disaster from Technomancy?").define("chunk_beam", true);
//		blastChunk = serverBuilder.comment("Allow Explosion disaster from Technomancy?").define("chunk_explode", true);
		fluxEvent = serverBuilder.comment("Allow Temporal Entropy disasters from Technomancy?", "If disabled, disasters create a small explosion instead").define("flux_disaster", true);
//		blockedPrototype = serverBuilder.comment("Blocks disallowed to be used in prototypes", "Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'", "Use to prevent exploits, bugs, travel to the prototype dimension, griefing, and other naughty things. Also, most modded multiblocks should be blocked to prevent bugs.").define("proto_banned", initList(Crossroads.MODID + ":large_gear_slave", Crossroads.MODID + ":large_gear_master", Crossroads.MODID + ":prototype", Crossroads.MODID + ":gateway_frame", "minecraft:portal", "rftools:matter_transmitter", "bloodmagic:blockteleposer"), compileRegex("[^\\:\\n\\t ]+?:[^\\:\\n\\t ]+"));
//		allowPrototype = serverBuilder.comment("Restrictions on prototyping", "-1: Prototyping is disabled. May block large amounts of the mod.", "0: Default value.", "1: Prototyping destroys the template structure the prototype was made from instead of copying the template. (prevents unintended dupe exploits)", "2: Prototyping works as normal, except prototype blocks themselves cannot be placed, only used within other compatible devices (such as the watch)").defineInRange("proto_mode", 0, -1, 2);
//		maximumPistolDamage = serverBuilder.comment("Maximum pistol damage per shot", "-1 for no cap").defineInRange("pistol_cap", -1, -1, Integer.MAX_VALUE);
//		cccExpenLiquid = serverBuilder.comment("Liquid type for the Copshowium Creation Chamber without Temporal Entropy", "An invalid liquid will disable the crafting").define("ccc_expen", "copper");
//		cccEntropLiquid = serverBuilder.comment("Liquid type for the Copshowium Creation Chamber with Temporal Entropy", "An invalid liquid will disable the crafting").define("ccc_cheap", "distilled_water");
		fePerEntropy = serverBuilder.comment("FE equal to 1 Temporal Entropy").defineInRange("fe_per_entropy", 50, 1, Integer.MAX_VALUE);
		serverBuilder.pop();
		serverBuilder.push(CAT_ALCHEMY);
		phelEffect = serverBuilder.comment("Allow the full effect of Phelostogen?", "If disabled Phelostogen lights a single small fire instead").define("phel_effect", true);
		allowHellfire = serverBuilder.comment("Whether to allow Ignis Infernum", "If disabled, Ignis Infernum is still craftable, but it gets nerfed to Phelostogen level").define("ignis_infernum", true);
		atmosEffect = serverBuilder.comment("Level of effects from overcharging the atmosphere", "0: No negative effects", "1: Allow lightning strikes", "2: Allow creeper charging", "3: Allow lightning strikes & creeper charging").defineInRange("atmos_effects", 3, 0, 3);
		atmosCap = serverBuilder.comment("Maximum charge for the atmosphere").defineInRange("charge_limit", 1_000_000_000, 0, 2_000_000_000);
		voltusValue = serverBuilder.comment("FE produced by one Voltus").defineInRange("voltus_power", 10_000, 0, 100_000);
		stampMillDamping = serverBuilder.comment("Percentage of Stamp Mill progress to be lost on failure", "Effectively nerfs ore-tripling").defineInRange("mill_damping", 0, 0, 100);
//		bedrockDust = serverBuilder.comment("Bedrock craftability", "Can bedrock be crafted from bedrock dust?").define("bedrock_dust", true);
		gravRange = serverBuilder.comment("Range of Density Plates").defineInRange("grav_range", 64, 0, 128);
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
		return (Object o) -> o instanceof String && p.matcher((String) o).matches();
	}

	/**
	 * @param world The world (currently unused, may change)
	 * @param pos The current block position (currently unused, may change)
	 * @param state The blockstate
	 * @return Whether the block is protected via the config from destruction
	 */
	public static boolean isProtected(World world, BlockPos pos, BlockState state){
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
		return EssentialsConfig.formatFloat(f, null);
	}
}
