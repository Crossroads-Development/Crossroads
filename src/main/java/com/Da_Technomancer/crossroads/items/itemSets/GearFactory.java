package com.Da_Technomancer.crossroads.items.itemSets;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class GearFactory{

	/*
	 * Instructions for adding a new GearMaterial (for addons):
	 * Initialize a GearMaterial instance with a unique name, and with shouldReload = false. If shouldReload is default (true), your material will get wiped every time the CR config reloads
	 * Call registerGearMaterial with the new instance
	 * Add localization for material.<name>
	 * Add recipes via JSON as desired
	 */

	private static final HashMap<String, GearMaterial> gearMats = new HashMap<>();
	//Do not use this list for saving/loading from disk! Order is not guaranteed between loads. It exists for server-client packets only
	private static final ArrayList<GearMaterial> gearMatList = new ArrayList<>();

	public static GearMaterial findMaterial(String id){
		return gearMats.getOrDefault(id, getDefaultMaterial());
	}

	public static Collection<GearMaterial> getMaterials(){
		return gearMats.values();
	}

	public static void registerGearMaterial(GearMaterial mat){
		gearMats.put(mat.getId(), mat);
		mat.pos = gearMatList.size();
		gearMatList.add(mat);
	}

	/**
	 * Fallback material if a material fails to load
	 * @return The Iron GearMaterial
	 */
	public static GearMaterial getDefaultMaterial(){
		return DEFAULT;
	}

	private static GearMaterial DEFAULT;

	/**
	 * Resets and rebuilds the registered gear materials from config
	 * Called on startup and when ModConfig.ConfigReloading fires with the Crossroads server config
	 */
	public static void init(){
		//Clear any previously registered GearMaterials if shouldReload is true. Otherwise, leave them registered
		//The old GearMaterials are wiped to allow them being defined by the server config, which could differ between the client and server
		gearMatList.removeIf(mat -> mat.shouldReload);
		gearMats.clear();
		gearMatList.forEach(mat -> gearMats.put(mat.getId(), mat));

		List<? extends String> rawInput = CRConfig.gearTypes.get();

		for(String raw : rawInput){
			int spaceIndex = raw.indexOf(' ');
			String metal = raw.substring(0, spaceIndex);
			Color col;

			String colorString = '#' + raw.substring(spaceIndex + 1, spaceIndex + 7);
			try{
				col = Color.decode(colorString);
			}catch(NumberFormatException e){
				//Pick a random color because the user messed up, and if the user ends up with hot-pink lead that's their problem
				col = Color.getHSBColor((float) Math.random(), 1F, 1F);
			}

			double density = Double.parseDouble(raw.substring(spaceIndex + 8));
			GearMaterial typ = new GearMaterial(metal, density, col);
			registerGearMaterial(typ);
		}

		DEFAULT = gearMats.get("iron");
		//If the config was modified to prevent iron being registered, crash
		if(DEFAULT == null){
			IllegalArgumentException e = new IllegalArgumentException("Default Gear Material not registered!");
			Crossroads.logger.error("Config Modified to prevent registering default gear material (iron)", e);
			throw e;
		}
	}

	public static class GearMaterial{

		// The densities for the materials used here are kg/cubic meter of the substance

//		IRON(8000D, new Color(160, 160, 160)),
//		GOLD(20000D, Color.YELLOW),
//		COPPER(9000D, new Color(255, 120, 60)),
//		TIN(7300D, new Color(240, 240, 240)),
//		BRONZE(8800D, new Color(255, 160, 60)),
//		COPSHOWIUM(0, new Color(255, 130, 0)),
//		LEAD(11000D, new Color(116, 105, 158)),
//		SILVER(10000D, new Color(189, 243, 238)),
//		NICKEL(9000D, new Color(241, 242, 196)),
//		INVAR(8000D, new Color(223, 237, 216)),
//		PLATINUM(21000D, new Color(116, 245, 255)),
//		ELECTRUM(15000D, new Color(254, 255, 138));

		private final String id;
		private final double density;
		private final Color color;
		private final boolean shouldReload;

		private int pos;

		public GearMaterial(String nameIn, double matDensity, Color matColor){
			this(nameIn, matDensity, matColor, true);
		}

		public GearMaterial(String nameIn, double matDensity, Color matColor, boolean shouldReload){
			id = nameIn;
			density = matDensity;
			color = matColor;
			this.shouldReload = shouldReload;
		}

		public String getId(){
			return id;
		}

		@OnlyIn(Dist.CLIENT)
		public String getName(){
			return MiscUtil.localize("material." + id);
		}

		public double getDensity(){
			return density;
		}

		public Color getColor(){
			return color;
		}

		/**
		 * For networking only! Not for saving/loading to disk
		 * @return An int that identifies this material
		 */
		public int serialize(){
			return pos;
		}

		public static GearMaterial deserialize(int serial){
			if(serial < 0 || serial >= gearMatList.size()){
				return getDefaultMaterial();
			}
			return gearMatList.get(serial);
		}
	}
}
