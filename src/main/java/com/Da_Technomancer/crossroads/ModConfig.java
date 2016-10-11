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

		/*
		 * In order to add a new config option, somewhere called during
		 * pre-initilization or initialization, call variableName =
		 * ModConfig.config.get(String catagory name, String label, default,
		 * String comment(optional));
		 * 
		 * in order to read the config file, call property.readFoo();
		 */
	}
	
	
	protected static void additionalConfigs(){
		
		overheatEffects = ModConfig.config.get
				("heat cables", "Enable all heat cable overheating effects.(default true)", true,
				("True enables all effects (obsidian, sponge, villager, explosion,etc) on overheat, while "
						+ "false only enables the fire effect"));
	}
}
