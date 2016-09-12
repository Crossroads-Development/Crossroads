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
	
	private static final String CAT_OPTIM = "Optimization";
	private static final String CAT_ORES = "Ores";
	private static final String CAT_MISC = "Misc";
	
	protected static void init(FMLPreInitializationEvent e){

		config = new Configuration(e.getSuggestedConfigurationFile());
		config.load();
		
		genCopperOre = config.get(CAT_ORES, "Generate Copper Ore?", true);
		genTinOre = config.get(CAT_ORES, "Generate Tin Ore?", true);
		genRubyOre = config.get(CAT_ORES, "Generate Ruby Ore?", true);
		genNativeCopperOre = config.get(CAT_ORES, "Generate Native Copper Ore?", true);
		speedTiers = config.get(CAT_OPTIM, "Speed Tiers", 4, "Higher value means smoother gear rotation, but more packets sent AKA lag. range 1-100 Default 4", 1, 100);
		weatherControl = config.get(CAT_MISC, "Enable rain idol? (Default true)", true);
		rotateBeam = config.get(CAT_OPTIM, "Rotate Beams", true, "Should magic beams rotate? (Default true)");
		smallText = config.get(CAT_MISC, "Use small text in the guide book? (Default true)", true);
	}
}
