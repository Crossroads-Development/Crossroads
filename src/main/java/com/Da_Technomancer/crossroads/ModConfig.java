package com.Da_Technomancer.crossroads;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ModConfig{

	public static Configuration config;
	
	public static Property genCopperOre;
	public static Property genTinOre;
	public static Property genRubyOre;
	public static Property genNativeCopperOre;
	public static Property speedTiers;
	public static Property weatherControl;
	public static Property rotateBeam;
	public static Property smallText;
	public static Property retrogen;
	public static Property heatEffects;
	public static Property allowAllSingle;
	public static Property allowAllServer;
	public static Property technomancy;
	public static Property alchemy;
	public static Property witchcraft;
	public static Property voidChunk;
	public static Property resetChunk;
	public static Property magicChunk;
	public static Property fieldLinesEnergy;
	public static Property fieldLinesPotential;
	public static Property disableSlaves;
	public static Property registerOres;
	public static Property gearResetTime;
	public static Property wipeInvalidMappings;
	public static Property blockedPrototype;
	
	private static final String CAT_INTERNAL = "Internal";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_MISC = "Misc";
	private static final String CAT_SPECIALIZATION = "Specializations";
	private static final String CAT_FLUX = "Flux Disasters";	
	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();
		
		genCopperOre = config.get(CAT_ORES, "Generate Copper Ore?", true);
		genTinOre = config.get(CAT_ORES, "Generate Tin Ore?", true);
		genRubyOre = config.get(CAT_ORES, "Generate Ruby Ore?", true);
		genNativeCopperOre = config.get(CAT_ORES, "Generate Native Copper Ore?", true);
		retrogen = config.get(CAT_ORES, "Retrogen Key", "", "Changing this value will cause retrogen. Leaving it blank disables retrogen. TURN THIS OFF WHEN YOU ARE DONE!");
		speedTiers = config.get(CAT_INTERNAL, "Speed Tiers", 50, "Higher value means smoother gear rotation and less clipping, but more packets sent AKA lag. (Range 1-1000, Default 50)", 1, 1000);
		weatherControl = config.get(CAT_MISC, "Enable rain idol? (Default true)", true);
		rotateBeam = config.get(CAT_MISC, "Rotate Beams", true, "Should magic beams rotate? (Default true)");
		smallText = config.get(CAT_MISC, "Use small text in the guide book? (Default true)", true);
		heatEffects = config.get(CAT_MISC, "Cable Overheat Effects", true, "If false, all heat cable overheating effects are replaced with burning (Default true)");
		allowAllSingle = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in SinglePlayer? (Default true)", true);
		allowAllServer = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in MultiPlayer? (Default false)", false);
		technomancy = config.get(CAT_SPECIALIZATION, "Enable Technomancy? (Default true)", true);
		alchemy = config.get(CAT_SPECIALIZATION, "Enable Alchemy? (Default true)", true, "NYI");
		witchcraft = config.get(CAT_SPECIALIZATION, "Enable Witchcraft? (Default true)", true, "NYI");
		voidChunk = config.get(CAT_FLUX, "Allow Chunk Voiding disaster from Technomancy? (Default true)", true);
		resetChunk = config.get(CAT_FLUX, "Allow Chunk Reset disaster from Technomancy? (Default true)", true);
		magicChunk = config.get(CAT_FLUX, "Allow Chunk Magic-ifying disaster from Technomancy? (Default true)", true);
		fieldLinesEnergy = config.get(CAT_FLUX, "Draw energy fields with lines (True: lines, False: planes)? (Default true)", true);
		fieldLinesPotential = config.get(CAT_FLUX, "Draw potential fields with lines (True: lines, False: planes)? (Default false)", false);
		disableSlaves = config.get(CAT_INTERNAL, "If you are crashing from StackOverflow errors and you either have a tiny amount of RAM or built an insanely large & complicated rotary setup, then set this to true, destroy the setup, and set this to false. Also maybe send me a picture of the setup.", false);
		registerOres = config.get(CAT_ORES, "Register OreDictionary for copper/tin/bronze? (Default true)", true, "Disabling this will make Crossroads copper/tin/bronze completely useless. The recipes will need copper/tin/bronze from other mods. Don't ask me why you'd want this.");
		gearResetTime = config.get(CAT_INTERNAL, "Gear Reset Time", 300, "Interval in ticks between gear network checks and visual angle resets. (Range 100-2400, Default 300)", 100, 2400);
		wipeInvalidMappings = config.get(CAT_INTERNAL, "Wipe internal per player dimension mappings on failure? (Default false)", false, "Only use this if needed, as the mappings between players and technomancy workspace dimensions will be lost. If doing this, delete the files for those dimensions. Also, make a backup of the world file before setting this to true.");
		blockedPrototype = config.get(CAT_INTERNAL, "Blocks disallowed to be used in prototypes. Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'.", new String[] {Main.MODID + ":large_gear_slave", Main.MODID + ":large_gear_master", Main.MODID + ":prototype", "minecraft:portal", "rftools:matter_transmitter"}, "Use to prevent exploits, bugs, travel to the prototype dimension, griefing, and other naughty things. Also, most modded multiblocks should be blocked to prevent bugs.");
	}
}
