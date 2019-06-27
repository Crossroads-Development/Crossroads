package com.Da_Technomancer.crossroads.integration.patchouli;

import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.EssentialsConfig;
import net.minecraft.client.resources.I18n;
import vazkii.patchouli.api.PatchouliAPI;

import java.util.HashMap;
import java.util.Map;

//Adds config flags that can be read by the book in order to display content or not
//Used in Detailed Crafter Processor, Multiplayer Processor, Key Crafting Processor, Elemental Processor, and Key Text Processor
//Needless to say, this cannot be refreshed after preinit - both due to implementation, and how patchouli works
public class DataBuilder {
	static Map<String, String> dataMap;

	public static void registerFlags() {
		//BOBO FLAG IS HERE
		PatchouliAPI.instance.setConfigFlag("crossroads:bobo_enabled", ModConfig.getConfigBool(ModConfig.addBoboRecipes, true));
		PatchouliAPI.instance.setConfigFlag( "crossroads:bobo_rain_idol", ModConfig.getConfigBool(ModConfig.weatherControl, true));


		PatchouliAPI.instance.setConfigFlag( "crossroads:heat_effects", ModConfig.getConfigBool(ModConfig.heatEffects, true));
		PatchouliAPI.instance.setConfigFlag( "crossroads:allow_all_server", ModConfig.getConfigBool(ModConfig.allowAllServer, true));
		PatchouliAPI.instance.setConfigFlag( "crossroads:allow_all_single", ModConfig.getConfigBool(ModConfig.allowAllSingle, true));
		PatchouliAPI.instance.setConfigFlag("crossroads:brazier_witch", EssentialsConfig.getConfigInt(EssentialsConfig.brazierRange, true) != 0);
		PatchouliAPI.instance.setConfigFlag("crossroads:item_chute_rotary", EssentialsConfig.getConfigBool(EssentialsConfig.itemChuteRotary, true));
		PatchouliAPI.instance.setConfigFlag("crossroads:document_craft_tweaker", ModConfig.getConfigBool(ModConfig.documentCrafttweaker, true));
		//magic
		PatchouliAPI.instance.setConfigFlag("crossroads:allow_time_beam", ModConfig.getConfigBool(ModConfig.allowTimeBeam, true));

		//technomancy
		PatchouliAPI.instance.setConfigFlag("crossroads:technomancy_enabled",  ModConfig.getConfigBool(ModConfig.technomancy, true));
		PatchouliAPI.instance.setConfigFlag("crossroads:prototype_disabled", true);
		PatchouliAPI.instance.setConfigFlag("crossroads:prototype_default", false);
		PatchouliAPI.instance.setConfigFlag("crossroads:prototype_consume", false);
		PatchouliAPI.instance.setConfigFlag("crossroads:prototype_device", false);

		PatchouliAPI.instance.setConfigFlag("crossroads:watch_enabled", false);
		if (ModConfig.getConfigInt(ModConfig.allowPrototype, true) != -1) {
			PatchouliAPI.instance.setConfigFlag("crossroads:prototype_disabled", false);
			PatchouliAPI.instance.setConfigFlag("crossroads:watch_enabled", true);

			switch (ModConfig.getConfigInt(ModConfig.allowPrototype, true)) {
				case 0:
					PatchouliAPI.instance.setConfigFlag("crossroads:prototype_default", true);
					break;
				case 1:
					PatchouliAPI.instance.setConfigFlag("crossroads:prototype_consume", true);
					break;
				case 2:
					PatchouliAPI.instance.setConfigFlag("crossroads:prototype_device", true);
			}
		}
		PatchouliAPI.instance.setConfigFlag("crossroads:pistol_damage_capped", ModConfig.getConfigInt(ModConfig.maximumPistolDamage, true) != -1);

		//alchemy
		PatchouliAPI.instance.setConfigFlag("crossroads:alchemy_enabled",  ModConfig.getConfigBool(ModConfig.alchemy, true));
		PatchouliAPI.instance.setConfigFlag("crossroads:allow_hellfire", ModConfig.getConfigBool(ModConfig.allowHellfire, true));
	}
	public static void registerDataValues() {
		dataMap = new HashMap<>();

		dataMap.put("deg_per_bucket_steam_inefficient", (Math.round(EnergyConverters.DEG_PER_BUCKET_STEAM * 1.1D) + ""));
		dataMap.put("deg_per_bucket_steam", (Math.round(EnergyConverters.DEG_PER_BUCKET_STEAM) + ""));
		dataMap.put("steam_turbine", EnergyConverters.DEG_PER_BUCKET_STEAM / EnergyConverters.DEG_PER_JOULE + "");
		dataMap.put("fat_per_value", EnergyConverters.FAT_PER_VALUE  + "");
		dataMap.put("brazier_range", EssentialsConfig.getConfigInt(EssentialsConfig.brazierRange, true) + "");

		dataMap.put("ccc_expensive_liquid",  I18n.format("fluid." + ModConfig.getConfigString(ModConfig.cccExpenLiquid, true)));
		dataMap.put("ccc_field_liquid", I18n.format("fluid." + ModConfig.getConfigString(ModConfig.cccFieldLiquid, true)));
		dataMap.put("prototype_pistol_cap", ModConfig.getConfigInt(ModConfig.maximumPistolDamage, true) + "");
		dataMap.put("voltus_usage",  1000F / ModConfig.getConfigDouble(ModConfig.voltusUsage, true) + "");

		//just for testing
		dataMap.put("meaning_of_life", "42");
	}
}
