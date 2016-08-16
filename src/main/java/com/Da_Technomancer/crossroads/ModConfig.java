package com.Da_Technomancer.crossroads;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ModConfig{

	public static Configuration config;

	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());

		config.load();

		/*
		 * In order to add a new config option, somewhere called during
		 * pre-initilization or initialization, call variableName =
		 * ModConfig.config.get(String catagory name, String label, default,
		 * String comment(optional));
		 * 
		 * in order to read the config file, call the appropriate method below
		 * and pass in the property.
		 */
	}

	public static void save(){
		config.save();
	}

	public static boolean getConfigBool(Property property){
		return property.getBoolean(true);
	}

	public static int getConfigInt(Property property){
		return property.getInt();
	}

	public static double getConfigDouble(Property property){
		return property.getDouble();
	}

	public static String getConfigString(Property property){
		return property.getString();
	}
}
