package com.Da_Technomancer.crossroads;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;

public final class ModConfig{

	public static Configuration config;

	public static Property genCopperOre;
	public static Property genTinOre;
	public static Property genRubyOre;
	public static Property genNativeCopperOre;
	public static Property speedPrecision;
	public static Property weatherControl;
	public static Property rotateBeam;
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
	public static Property blastChunk;
	public static Property fieldLinesEnergy;
	public static Property fieldLinesPotential;
	public static Property disableSlaves;
	public static Property registerOres;
	public static Property gearResetTime;
	public static Property wipeInvalidMappings;
	public static Property blockedPrototype;
	public static Property allowPrototype;
	public static Property maximumPistolDamage;
	public static Property addWrench;
	public static Property wrenchTypes;
	public static Property electPerJoule;
	public static Property growBlacklist;
	public static Property allowHellfire;
	public static Property voltusUsage;
	public static Property atmosEffect;

	private static final ArrayList<Property> SYNCED_PROPERTIES = new ArrayList<Property>();
	public static NBTTagCompound syncPropNBT;

	private static final String CAT_INTERNAL = "Internal";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_MISC = "Misc";
	private static final String CAT_SPECIALIZATION = "Specializations";
	private static final String CAT_TECHNOMANCY = "Technomancy";
	private static final String CAT_ALCHEMY = "Alchemy";

	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();

		genCopperOre = config.get(CAT_ORES, "Generate Copper Ore?", true);
		genTinOre = config.get(CAT_ORES, "Generate Tin Ore?", true);
		genRubyOre = config.get(CAT_ORES, "Generate Ruby Ore?", true);
		genNativeCopperOre = config.get(CAT_ORES, "Generate Native Copper Ore?", true);
		retrogen = config.get(CAT_ORES, "Retrogen Key", "", "Changing this value will cause retrogen. Leaving it blank disables retrogen. TURN THIS OFF WHEN YOU ARE DONE!");
		speedPrecision = config.get(CAT_INTERNAL, "Speed Precision", .02F, "Lower value means smoother gear rotation and less clipping, but more packets sent AKA lag. (Range 0.0-1.0, Default .02)", 0F, 1F);
		SYNCED_PROPERTIES.add(weatherControl = config.get(CAT_MISC, "Enable rain idol? (Default true)", true));
		rotateBeam = config.get(CAT_MISC, "Rotate Beams", true, "Should magic beams rotate? (Default true)");
		SYNCED_PROPERTIES.add(heatEffects = config.get(CAT_MISC, "Cable Overheat Effects", true, "If false, all heat cable overheating effects are replaced with burning (Default true)"));
		allowAllSingle = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in SinglePlayer? (Default true)", true);
		SYNCED_PROPERTIES.add(allowAllServer = config.get(CAT_SPECIALIZATION, "Allow Multiple specializations per player in MultiPlayer? (Default false)", false));
		SYNCED_PROPERTIES.add(technomancy = config.get(CAT_SPECIALIZATION, "Enable Technomancy? (Default true)", true));
		SYNCED_PROPERTIES.add(alchemy = config.get(CAT_SPECIALIZATION, "Enable Alchemy? (Default true)", true));
		SYNCED_PROPERTIES.add(witchcraft = config.get(CAT_SPECIALIZATION, "Enable Witchcraft? (Default true)", true, "NYI"));
		voidChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Voiding disaster from Technomancy? (Default true)", true);
		resetChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Reset disaster from Technomancy? (Default true)", true);
		magicChunk = config.get(CAT_TECHNOMANCY, "Allow Chunk Magic-ifying disaster from Technomancy? (Default true)", true);
		blastChunk = config.get(CAT_TECHNOMANCY, "Allow Explosion disaster from Technomancy? (Default true)", true);
		fieldLinesEnergy = config.get(CAT_TECHNOMANCY, "Draw energy fields with lines (True: lines, False: planes)? (Default true)", true);
		fieldLinesPotential = config.get(CAT_TECHNOMANCY, "Draw potential fields with lines (True: lines, False: planes)? (Default false)", false);
		disableSlaves = config.get(CAT_INTERNAL, "If you are crashing from StackOverflow errors and you either have a tiny amount of RAM or built an insanely large & complicated rotary setup, then set this to true, destroy the setup, and set this to false. Also maybe send me a picture of the setup.", false);
		registerOres = config.get(CAT_ORES, "Register OreDictionary for copper/tin/bronze? (Default true)", true, "Disabling this will make Crossroads copper/tin/bronze completely useless. The recipes will need copper/tin/bronze from other mods. Don't ask me why you'd want this.");
		gearResetTime = config.get(CAT_INTERNAL, "Gear Reset Time", 300, "Interval in ticks between gear network checks. (Range 100-2400, Default 300)", 100, 2400);
		wipeInvalidMappings = config.get(CAT_INTERNAL, "Wipe internal per player dimension mappings on failure? (Default false)", false, "Only use this if needed, as the mappings between players and technomancy workspace dimensions will be lost. If doing this, delete the files for those dimensions. Also, make a backup of the world file before setting this to true.");
		blockedPrototype = config.get(CAT_TECHNOMANCY, "Blocks disallowed to be used in prototypes. Should be in the format 'modid:blockregistryname', ex. 'minecraft:obsidian' or 'crossroads:block_salt'.", new String[] {Main.MODID + ":large_gear_slave", Main.MODID + ":large_gear_master", Main.MODID + ":prototype", Main.MODID + ":gateway_frame", "minecraft:portal", "rftools:matter_transmitter", "bloodmagic:blockteleposer"}, "Use to prevent exploits, bugs, travel to the prototype dimension, griefing, and other naughty things. Also, most modded multiblocks should be blocked to prevent bugs.");
		SYNCED_PROPERTIES.add(allowPrototype = config.get(CAT_TECHNOMANCY, "Restrictions on prototyping. (Default 0)", 0, "-1: Prototyping is disabled. May block large amounts of the mod. 0: Default value. 1: Prototyping destroys the template structure the prototype was made from instead of copying the template. (prevents unintended dupe exploits). 2: Prototyping works as normal, except prototype blocks themselves cannot be placed, only used within other compatible devices (such as the watch).", -1, 2));
		maximumPistolDamage = config.get(CAT_TECHNOMANCY, "Maximum pistol damage per shot, -1 for no cap. (Default -1)", -1);
		addWrench = config.get(CAT_INTERNAL, "Show the Crossroads wrench in the creative menu? (Default true)", true);
		SYNCED_PROPERTIES.add(wrenchTypes = config.get(CAT_INTERNAL, "Item ids for wrench items. Should be in format 'modid:itemregistryname', ex. minecraft:apple or crossroads:wrench.", new String[] {Main.MODID + ":wrench", Main.MODID + ":liech_wrench", "actuallyadditions:itemlaserwrench", "appliedenergistics2:certus_quartz_wrench", "appliedenergistics2:nether_quartz_wrench", "base:wrench", "enderio:itemyetawrench", "extrautils2:wrench", "bigreactors:wrench", "forestry:wrench", "progressiveautomation:wrench", "thermalfoundation:wrench", "redstonearsenal:tool.wrench_flux", "rftools:smartwrench", "immersiveengineering:tool"}));
		electPerJoule = config.get(CAT_ALCHEMY, "FE generated from 1J. Set to 0 to effectively disable the dynamo. (Default 20)", 20, "", 0, Integer.MAX_VALUE);
		growBlacklist = config.get(CAT_MISC, "Plant types that can not be grown by a potential beam. Should be in format 'modid:blockregistryname', ex. minecraft:wheat", new String[0]);
		SYNCED_PROPERTIES.add(allowHellfire = config.get(CAT_ALCHEMY, "Whether to allow crafting Ignis Infernum. (Default true)",true));
		SYNCED_PROPERTIES.add(voltusUsage = config.get(CAT_ALCHEMY, "Voltus used to charge atmosphere per 1000FE (Default 0.01)", 0.01D));
		atmosEffect = config.get(CAT_ALCHEMY, "Level of effects from overcharging the atmosphere (Default 3)", 3, "0: No negative effects. 1: Allow lightning strikes. 2: Allow creeper charging. 3: Allow lightning strikes & creeper charging.");
	}

	/**
	 * Returns the value of the Property, but if client side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual client. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static boolean getConfigBool(Property prop, boolean client){
		if(prop.getType() != Property.Type.BOOLEAN || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong client config than crash.
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
	 * Returns the value of the Property, but if client side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 *
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual client. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static double getConfigDouble(Property prop, boolean client){
		if(prop.getType() != Property.Type.DOUBLE || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong client config than crash.
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
	 * Returns the value of the Property, but if client side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual client. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static int getConfigInt(Property prop, boolean client){
		if(prop.getType() != Property.Type.INTEGER || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong client config than crash.
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
	 * Returns the value of the Property, but if client side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual client. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static String getConfigString(Property prop, boolean client){
		if(prop.getType() != Property.Type.STRING || prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong client config than crash.
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
	 * Returns the value of the Property, but if client side it will return the server side value if the Property is in the list SYNCED_PROPERTIES.
	 * 
	 * @param prop The property to get the value of.
	 * @param client Whether this is on the virtual client. Pass this param. true if unknown.
	 * @return The config value.
	 */
	public static String[] getConfigStringList(Property prop, boolean client){
		if(prop.getType() != Property.Type.STRING || !prop.isList()){
			throw new ClassCastException(Main.MODID + ": Incorrect config property type.");
		}
		//The NBT should never be null, but just to be safe, it is better to have the wrong client config than crash.
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
						//Not supported
					}
					break;
				case COLOR:
					//Not supported
					break;
				case DOUBLE:
					if(!prop.isList()){
						out.setDouble("p_" + i, prop.getDouble());
					}else{
						//Not supported
					}
					break;
				case INTEGER:
					if(!prop.isList()){
						out.setInteger("p_" + i, prop.getInt());
					}else{
						//Not supported
					}
					break;
				case MOD_ID:
					//Not supported
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
	
	/**
	 * @param stack The stack to test
	 * @param client Whether this is on the client side
	 * @return Whether this item is considered a wrench
	 */
	public static boolean isWrench(ItemStack stack, boolean client){
		if(stack.isEmpty()){
			return false;
		}
		ResourceLocation loc = stack.getItem().getRegistryName();
		if(loc == null){
			return false;
		}
		String name = loc.toString();
		for(String s : getConfigStringList(wrenchTypes, client)){
			if(name.equals(s)){
				return true;
			}
		}
		return false;
	}
}
