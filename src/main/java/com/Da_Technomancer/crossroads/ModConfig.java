package com.Da_Technomancer.crossroads;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.regex.Pattern;

public final class ModConfig{

	public static Configuration config;

	public static Property genCopperOre;
	public static Property genTinOre;
	public static Property genRubyOre;
	public static Property genVoidOre;
	public static Property processableOres;
	public static Property gearTypes;
	public static Property speedPrecision;
	public static Property weatherControl;
	public static Property rotateBeam;
	public static Property retrogen;
	public static Property heatEffects;
	public static Property allowAllSingle;
	public static Property allowAllServer;
	public static Property technomancy;
	public static Property alchemy;
	public static Property witchcraft;//NYI
	public static Property voidChunk;
	public static Property resetChunk;
	public static Property magicChunk;
	public static Property blastChunk;
	public static Property fieldLinesPotential;
	public static Property disableSlaves;
	public static Property gearResetTime;
	public static Property wipeInvalidMappings;
	public static Property blockedPrototype;
	public static Property allowPrototype;
	public static Property maximumPistolDamage;
	public static Property electPerJoule;
	public static Property growBlacklist;
	public static Property allowHellfire;
	public static Property voltusUsage;
	public static Property atmosEffect;
	public static Property documentCrafttweaker;
	public static Property addBoboRecipes;
	public static Property cccExpenLiquid;
	public static Property cccFieldLiquid;
	public static Property destroyBlacklist;
	public static Property rotaryLoss;
	public static Property crystalAxisMult;
	public static Property steamWorth;
	public static Property jouleWorth;
	public static Property stirlingSpeedLimit;
	public static Property stirlingMultiplier;
	public static Property fePerCharge;
	public static Property redstoneTransmitterRange;
	public static Property stampMillDamping;
	public static Property bedrockDust;
	public static Property phelEffect;
	public static Property gravRange;

	private static final ArrayList<Property> SYNCED_PROPERTIES = new ArrayList<Property>();
	public static NBTTagCompound syncPropNBT;

	private static final String CAT_INTERNAL = "Internal";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_BALANCE = "Balance";
	private static final String CAT_MISC = "Misc";
	private static final String CAT_SPECIALIZATION = "Specializations";
	private static final String CAT_TECHNOMANCY = "Technomancy";
	private static final String CAT_ALCHEMY = "Alchemy";
	private static final String CAT_BOBO = "Bobo Features";

	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		speedPrecision = config.get(CAT_INTERNAL, "Speed Precision", .02F, "Lower value means smoother gear rotation and less clipping, but more packets sent AKA lag. (Range 0.0-1.0, Default .02)", 0F, 1F);
		documentCrafttweaker = config.get(CAT_INTERNAL, "Show CraftTweaker integration documentation in the guide book? (Default false)", false, "The documentation will appear in the misc section of Technician's Manual, NOT the Mysterious Journal. Pack makers: Turn this off before releasing the pack!");
		gearResetTime = config.get(CAT_INTERNAL, "Gear Reset Time", 300, "Interval in ticks between gear network checks. (Range 100-2400, Default 300)", 100, 2400);
		wipeInvalidMappings = config.get(CAT_INTERNAL, "Wipe internal per player dimension mappings on failure? (Default false)", false, "Only use this if needed, as the mappings between players and technomancy workspace dimensions will be lost. If doing this, delete the files for those dimensions. Also, make a backup of the world file before setting this to true.");
		disableSlaves = config.get(CAT_INTERNAL, "If you are crashing from StackOverflow errors and you either have a tiny amount of RAM or built an insanely large & complicated rotary setup, then set this to true, destroy the setup, and set this to false. Also maybe send me a picture of the setup.", false);
		destroyBlacklist = config.get(CAT_INTERNAL, "Blocks that Crossroads shouldn't be able to destroy or replace. Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'.", new String[] {"minecraft:barrier", "minecraft:command_block", "minecraft:end_portal", "minecraft:end_portal_frame", "minecraft:nether_portal", "minecraft:chain_command_block", "minecraft:repeating_command_block", "minecraft:end_gateway", "minecraft:structure_block", "minecraft:structure_void"});
		genCopperOre = config.get(CAT_ORES, "Generate Copper Ore?", true);
		genTinOre = config.get(CAT_ORES, "Generate Tin Ore?", true);
		genRubyOre = config.get(CAT_ORES, "Generate Ruby Ore?", true);
		genVoidOre = config.get(CAT_ORES, "Generate Void Crystal Ore?", true);
		SYNCED_PROPERTIES.add(processableOres = config.get(CAT_ORES, "Metal ore types that Crossroads machines should be able to process", new String[] {"copper FF7800", "tin C8C8C8", "iron A0A0A0", "gold FFFF00"}, "Specify the metal then a space then a hexadecimal color, ex. \"copper FF4800\" \nThis config MUST match the server settings exactly (color doesn't matter) in multiplayer or THINGS WILL BREAK! It can not sync itself!", Pattern.compile("\\w++ \\p{XDigit}{6}+")));
		SYNCED_PROPERTIES.add(gearTypes = config.get(CAT_ORES, "Metal types that Crossroads should add gears for", new String[] {"copper FF783C 9000", "tin F0F0F0 7300", "iron A0A0A0 8000", "gold FFFF00 20000", "bronze FFA03C 8800", "copshowium FF8200 0", "lead 74699E 11000", "silver BDF3EE 10000", "nickel F1F2C4 9000", "invar DFEDD8 8000", "platinum 74F5FF 21000", "electrum FEFF8A 15000"}, "Specify the metal then a space then a hexadecimal color then a space then a density in kg/m3, ex. \"copper FF783C 9000\" \nThis config MUST match the server settings exactly (color doesn't matter) in multiplayer or THINGS WILL BREAK! It can not sync itself!", Pattern.compile("\\w++ \\p{XDigit}{6}+ [+]?[0-9]*\\.?[0-9]+")));
		retrogen = config.get(CAT_ORES, "Retrogen Key", "", "Changing this value will cause retrogen. Leaving it blank disables retrogen. TURN THIS OFF WHEN YOU ARE DONE!");
		SYNCED_PROPERTIES.add(steamWorth = config.get(CAT_BALANCE, "The number of degrees one bucket of steam is worth", 50D, "Default: 50"));
		SYNCED_PROPERTIES.add(jouleWorth = config.get(CAT_BALANCE, "The number of Joules generated from one degree worth of steam", 4D, "Default: 4"));
		SYNCED_PROPERTIES.add(stirlingSpeedLimit = config.get(CAT_BALANCE, "The maximum speed a Stirling Engine can reach", 0.5D, "Default: 0.5"));
		SYNCED_PROPERTIES.add(stirlingMultiplier = config.get(CAT_BALANCE, "Multiplier for Stirling Engine power output", 2.5D, "Default: 2.5"));
		SYNCED_PROPERTIES.add(fePerCharge = config.get(CAT_BALANCE, "FE generated by one unit of charge-alignment beam", 2, "Default: 2", 0, Integer.MAX_VALUE));
		rotaryLoss = config.get(CAT_BALANCE, "Multiplier for rotary energy loss (Default 1)", 1D);
		crystalAxisMult = config.get(CAT_BALANCE, "Power generated by the Crystal Master Axis in J/t (Default 100)", 100D);
		rotateBeam = config.get(CAT_MISC, "Rotate Beams", true, "Should beams beams rotate? (Default true)");
		growBlacklist = config.get(CAT_MISC, "Plant types that can not be grown by a potential beam. Should be in format 'modid:blockregistryname', ex. minecraft:wheat", new String[0]);
		redstoneTransmitterRange = config.get(CAT_MISC, "Range of Wireless Redstone Transmitters and Receivers", 16, "Default 16, 0-128", 0, 256);
		allowAllSingle = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in SinglePlayer? (Default true)", true);
		SYNCED_PROPERTIES.add(allowAllServer = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in MultiPlayer? (Default false)", false));
		SYNCED_PROPERTIES.add(technomancy = config.get(CAT_SPECIALIZATION, "Enable Technomancy? (Default true)", true));
		SYNCED_PROPERTIES.add(alchemy = config.get(CAT_SPECIALIZATION, "Enable Alchemy? (Default true)", true));
		SYNCED_PROPERTIES.add(witchcraft = config.get(CAT_SPECIALIZATION, "Enable Witchcraft? (Default true)", true, "NYI"));
		fieldLinesPotential = config.get(CAT_TECHNOMANCY, "Draw potential fields with lines (True: lines, False: planes)? (Default false)", false);
		voidChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Voiding disaster from Technomancy? (Default true)", true);
		resetChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Reset disaster from Technomancy? (Default true)", true);
		magicChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Magic-ifying disaster from Technomancy? (Default true)", true);
		blastChunk = config.get(CAT_TECHNOMANCY, "Allow Explosion disaster from Technomancy? (Default true)", true);
		blockedPrototype = config.get(CAT_TECHNOMANCY, "Blocks disallowed to be used in prototypes. Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'.", new String[] {Main.MODID + ":large_gear_slave", Main.MODID + ":large_gear_master", Main.MODID + ":prototype", Main.MODID + ":gateway_frame", "minecraft:portal", "rftools:matter_transmitter", "bloodmagic:blockteleposer"}, "Use to prevent exploits, bugs, travel to the prototype dimension, griefing, and other naughty things. Also, most modded multiblocks should be blocked to prevent bugs.");
		SYNCED_PROPERTIES.add(allowPrototype = config.get(CAT_TECHNOMANCY, "Restrictions on prototyping. (Default 0)", 0, "-1: Prototyping is disabled. May block large amounts of the mod. 0: Default value. 1: Prototyping destroys the template structure the prototype was made from instead of copying the template. (prevents unintended dupe exploits). 2: Prototyping works as normal, except prototype blocks themselves cannot be placed, only used within other compatible devices (such as the watch).", -1, 2));
		maximumPistolDamage = config.get(CAT_TECHNOMANCY, "Maximum pistol damage per shot, -1 for no cap. (Default -1)", -1);
		cccExpenLiquid = config.get(CAT_TECHNOMANCY, "Liquid type for the Copshowium Creation Chamber without fields (Default copper)", "copper", "An invalid liquid will disable the crafting");
		cccFieldLiquid = config.get(CAT_TECHNOMANCY, "Liquid type for the Copshowium Creation Chamber with fields (Default distilled_water)", "distilled_water", "An invalid liquid will disable the crafting");
		electPerJoule = config.get(CAT_ALCHEMY, "FE generated from 1J. Set to 0 to effectively disable the dynamo. (Default 20)", 20, "", 0, Integer.MAX_VALUE);
		SYNCED_PROPERTIES.add(allowHellfire = config.get(CAT_ALCHEMY, "Whether to allow crafting Ignis Infernum. (Default true)",true));
		atmosEffect = config.get(CAT_ALCHEMY, "Level of effects from overcharging the atmosphere (Default 3)", 3, "0: No negative effects. 1: Allow lightning strikes. 2: Allow creeper charging. 3: Allow lightning strikes & creeper charging.");
		SYNCED_PROPERTIES.add(voltusUsage = config.get(CAT_ALCHEMY, "Voltus used to produce 1000FE of charge in the atmosphere (Default 0.1)", 0.1D));
		stampMillDamping = config.get(CAT_BALANCE, "Percentage of Stamp Mill progress to be lost on failure?", 0, "Default 0, 0-100. Effectively nerfs ore-tripling", 0, 100);
		bedrockDust = config.get(CAT_ALCHEMY, "Harvest Bedrock Dust instead of Bedrock", false, "Default: false");
		phelEffect = config.get(CAT_ALCHEMY, "Allow the full effect of phelostogen", true, "Default: true, if disabled phelostogen lights a single fire instead");
		gravRange = config.get(CAT_ALCHEMY, "Range of Density Plates", 64, "Deaulft: 64, 0-128", 0, 128);

		//TODO Itemize bobo item configs
		SYNCED_PROPERTIES.add(addBoboRecipes = config.get(CAT_BOBO, "Add recipes for bobo items? (Default true)", true));
		SYNCED_PROPERTIES.add(weatherControl = config.get(CAT_BOBO, "Enable rain idol? (Default true)", true));
		SYNCED_PROPERTIES.add(heatEffects = config.get(CAT_BOBO, "Cable Overheat Effects", true, "If false, all heat cable overheating effects are replaced with burning (Default true)"));
	}

	/**
	 * Returns the value of the Property, but if render side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static boolean getConfigBool(Property prop, boolean client){
		if(prop.getType() != Property.Type.BOOLEAN || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong render config than crash.
		if(!client || syncPropNBT == null){
			return prop.getBoolean();
		}
		int index = SYNCED_PROPERTIES.indexOf(prop);
		if(index == -1){
			return prop.getBoolean();
		}
		return syncPropNBT.getBoolean("p_" + index);
	}

	/**
	 * Returns the value of the Property, but if render side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 *
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static double getConfigDouble(Property prop, boolean client){
		if(prop.getType() != Property.Type.DOUBLE || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong render config than crash.
		if(!client || syncPropNBT == null){
			return prop.getDouble();
		}
		int index = SYNCED_PROPERTIES.indexOf(prop);
		if(index == -1){
			return prop.getDouble();
		}
		return syncPropNBT.getDouble("p_" + index);
	}

	/**
	 * Returns the value of the Property, but if render side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static int getConfigInt(Property prop, boolean client){
		if(prop.getType() != Property.Type.INTEGER || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong render config than crash.
		if(!client || syncPropNBT == null){
			return prop.getInt();
		}
		int index = SYNCED_PROPERTIES.indexOf(prop);
		if(index == -1){
			return prop.getInt();
		}
		return syncPropNBT.getInteger("p_" + index);
	}

	/**
	 * Returns the value of the Property, but if render side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static String getConfigString(Property prop, boolean client){
		if(prop.getType() != Property.Type.STRING || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong render config than crash.
		if(!client || syncPropNBT == null){
			return prop.getString();
		}
		int index = SYNCED_PROPERTIES.indexOf(prop);
		if(index == -1){
			return prop.getString();
		}
		return syncPropNBT.getString("p_" + index);
	}

	/**
	 * Returns the value of the Property, but if render side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual render. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static String[] getConfigStringList(Property prop, boolean client){
		if(prop.getType() != Property.Type.STRING || !prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong render config than crash.
		if(!client || syncPropNBT == null){
			return prop.getStringList();
		}
		int index = SYNCED_PROPERTIES.indexOf(prop);
		if(index == -1){
			return prop.getStringList();
		}
		String[] out = new String[syncPropNBT.getInteger("p_" + index)];
		for(int i = 0; i < out.length; i++){
			out[i] = syncPropNBT.getString("p_" + index + "_" + i);
		}
		return out;
	}
	
	public static NBTTagCompound nbtToSyncConfig(){
		NBTTagCompound out = new NBTTagCompound();
		int i = 0;
		for(Property prop : SYNCED_PROPERTIES){
			switch(prop.getType()){
				case BOOLEAN:
					if(!prop.isList()){
						out.setBoolean("p_" + i, prop.getBoolean());
					}else{
						//Not currently supported
					}
					break;
				case COLOR:
					//Not currently supported
					break;
				case DOUBLE:
					if(!prop.isList()){
						out.setDouble("p_" + i, prop.getDouble());
					}else{
						//Not currently supported
					}
					break;
				case INTEGER:
					if(!prop.isList()){
						out.setInteger("p_" + i, prop.getInt());
					}else{
						//Not currently supported
					}
					break;
				case MOD_ID:
					//Not currently supported
					break;
				case STRING:
					if(!prop.isList()){
						out.setBoolean("p_" + i, prop.getBoolean());
					}else{
						out.setInteger("p_" + i, prop.getStringList().length);
						for(int ind = 0; ind < prop.getStringList().length; ind++){
							out.setString("p_" + i + "_" + ind, prop.getStringList()[ind]);
						}
					}
					break;
				default:
					break;
			}
			i++;
		}
		return out;
	}
}
