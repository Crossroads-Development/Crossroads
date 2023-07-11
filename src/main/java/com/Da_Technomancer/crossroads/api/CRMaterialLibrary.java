package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.api.rotary.IMechanismProperty;

import java.awt.*;
import java.util.List;
import java.util.*;

public final class CRMaterialLibrary{

	/*
	 * Instructions for adding a new GearMaterial (for addons):
	 * Initialize a GearMaterial instance with a unique name, and with shouldReload = false. If shouldReload is default (true), your material will get wiped every time the CR config reloads
	 * Call registerGearMaterial with the new instance
	 * Add localization for material.<name>
	 * Add recipes via JSON as desired
	 */

	private static final HashMap<String, OreProfile> metalTypes = new HashMap<>();
	private static final HashMap<String, GearMaterial> gearMats = new HashMap<>();
	//Do not use this list for saving/loading from disk! Order is not guaranteed between loads. It exists for server-client packets only
	private static final ArrayList<GearMaterial> gearMatList = new ArrayList<>();
	private static OreProfile DEFAULT_ORE_PROFILE;
	private static GearMaterial DEFAULT_GEAR_MATERIAL;

	public static OreProfile findProfile(String id){
		return metalTypes.getOrDefault(id, getDefaultProfile());
	}

	public static GearMaterial findMaterial(String id){
		return gearMats.getOrDefault(id.toLowerCase(Locale.US), getDefaultMaterial());
	}

	public static Collection<OreProfile> getProfiles(){
		return metalTypes.values();
	}

	public static Collection<GearMaterial> getMaterials(){
		return gearMats.values();
	}

	public static void registerProfile(OreProfile mat){
		metalTypes.put(mat.getId(), mat);
	}

	public static void registerGearMaterial(GearMaterial mat){
		gearMats.put(mat.getId().toLowerCase(Locale.US), mat);
		mat.pos = gearMatList.size();
		gearMatList.add(mat);
	}

	/**
	 * Fallback material if a material fails to load
	 * @return The Iron OreProfile
	 */
	public static OreProfile getDefaultProfile(){
		return DEFAULT_ORE_PROFILE;
	}

	/**
	 * Fallback material if a material fails to load
	 * @return The Iron GearMaterial
	 */
	public static GearMaterial getDefaultMaterial(){
		return DEFAULT_GEAR_MATERIAL;
	}

	public static void loadConfig(){

		//GearMaterial stuff

		//Clear any previously registered GearMaterials if shouldReload is true. Otherwise, leave them registered
		//The old GearMaterials are wiped to allow them being defined by the server config, which could differ between the client and server
		gearMatList.removeIf(OreProfile::wipeOnReload);
		gearMats.clear();
		gearMatList.forEach(mat -> gearMats.put(mat.getId(), mat));

		List<? extends String> rawInput1 = CRConfig.gearTypes.get();

		for(String raw1 : rawInput1){
			int spaceIndex1 = raw1.indexOf(' ');
			String metal1 = raw1.substring(0, spaceIndex1);
			Color col1;

			String colorString1 = '#' + raw1.substring(spaceIndex1 + 1, spaceIndex1 + 7);
			try{
				col1 = Color.decode(colorString1);
			}catch(NumberFormatException e1){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col1 = Color.getHSBColor((float) Math.random(), 1F, 1F);
			}

			double density = Double.parseDouble(raw1.substring(spaceIndex1 + 8));
			GearMaterial typ = new GearMaterial(metal1, density, col1);
			registerGearMaterial(typ);
		}

		DEFAULT_GEAR_MATERIAL = gearMats.get("iron");
		//If the config was modified to prevent iron being registered, crash
		if(DEFAULT_GEAR_MATERIAL == null){
			IllegalArgumentException e1 = new IllegalArgumentException("Default Gear Material not registered!");
			Crossroads.logger.error("Config Modified to prevent registering default gear material (iron)", e1);
			throw e1;
		}


		//OreProfile stuff

		//Clear any previously registered OreProfile if shouldReload is true. Otherwise, leave them registered
		//The old OreProfiles are wiped to allow them being defined by the server config, which could differ between the client and server
		ArrayList<String> toRemove = new ArrayList<>(metalTypes.size());
		for(OreProfile prof : metalTypes.values()){
			if(prof.wipeOnReload()){
				toRemove.add(prof.id);
			}
		}
		toRemove.forEach(metalTypes::remove);

		List<? extends String> rawInput = CRConfig.processableOres.get();

		for(String raw : rawInput){
			int spaceIndex = raw.length() - 7;
			String metal = raw.substring(0, spaceIndex);
			Color col;

			String colorString = '#' + raw.substring(spaceIndex + 1);
			try{
				col = Color.decode(colorString);
			}catch(NumberFormatException e){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col = Color.getHSBColor((float) Math.random(), 1F, 1F);
				Crossroads.logger.error(String.format("Invalid color defined for ore profile: %s; Selecting random color", metal), e);
			}

			registerProfile(new OreProfile(metal, col));
		}

		DEFAULT_ORE_PROFILE = metalTypes.get("iron");
		//If the config was modified to prevent iron being registered, crash
		if(DEFAULT_ORE_PROFILE == null){
			IllegalArgumentException e = new IllegalArgumentException("Default Ore Profile not registered!");
			Crossroads.logger.error("Config Modified to prevent registering default ore profile (iron)", e);
			throw e;
		}
	}

	public static class OreProfile{

		private final String id;
		private final Color color;
		private final boolean shouldReload;

		protected OreProfile(String nameIn, Color matColor){
			this(nameIn, matColor, true);
		}

		protected OreProfile(String nameIn, Color matColor, boolean shouldReload){
			id = nameIn;
			color = matColor;
			this.shouldReload = shouldReload;
		}

		public boolean wipeOnReload(){
			return shouldReload;
		}

		public String getId(){
			return id;
		}

		public Color getColor(){
			return color;
		}

		/**
		 * Gets the localized name of this material (as an adjective)- to be combined with a component
		 * Do not trust the result on the physical server side (due to missing localization maps)
		 * @return The localized name
		 */
		public String getName(){
			return MiscUtil.localize("material." + id);
		}
	}

	//Despite this class extending OreProfile, registering a GearMaterial is completely independent of registering an OreProfile
	public static class GearMaterial extends OreProfile implements IMechanismProperty{

		// The densities for the materials used here are kg/cubic meter of the substance
		private final double density;
		private int pos;

		public GearMaterial(String nameIn, double matDensity, Color matColor){
			this(nameIn, matDensity, matColor, true);
		}

		public GearMaterial(String nameIn, double matDensity, Color matColor, boolean shouldReload){
			super(nameIn, matColor, shouldReload);
			density = matDensity;
		}

		public double getDensity(){
			return density;
		}

		@Override
		public int serialize(){
			return pos;
		}

		@Override
		public String getSaveName(){
			return getId();
		}

		public static GearMaterial deserialize(int serial){
			if(serial < 0 || serial >= gearMatList.size()){
				return getDefaultMaterial();
			}
			return gearMatList.get(serial);
		}
	}
}
