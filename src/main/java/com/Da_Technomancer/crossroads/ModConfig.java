package com.Da_Technomancer.crossroads;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public final class ModConfig{

	public static Configuration config;
	public static Property overheatEffects;

	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());

		config.load();
		
		overheatEffects = config.get("Heat Cables", "Heat Cable Effects", true, "If false, all heat cable overheating effects are replaced with turning into fire.");
		/*
		 * In order to add a new config option, somewhere called during
		 * pre-initilization or initialization, call variableName =
		 * ModConfig.config.get(String catagory name, String label, default,
		 * String comment(optional));
		 * 
		 * in order to read the config file, call property.readFoo();
		 */
	}
}
