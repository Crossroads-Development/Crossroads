package com.Da_Technomancer.crossroads.integration.create;

import net.minecraftforge.fml.ModList;

/**
 * Interacts with the Create mod
 * This class is safe to interact with even when Create is not installed.
 */
public class CreateHelper{

	protected static final String CREATE_ID = "create";
	private static boolean hasCreate;

	public static void initIntegration(){
		if(ModList.get().isLoaded(CREATE_ID)){
			hasCreate = true;
			CreateHeaterProxy.registerHeatSources();
		}
	}

	public static boolean hasCreate(){
		return hasCreate;
	}


}
