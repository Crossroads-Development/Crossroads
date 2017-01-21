package com.Da_Technomancer.crossroads.API.enums;

import java.awt.Color;

public enum GearTypes{

	// The densities for the materials used here are kg/cubic meter of
	// the substance, for gears multiply by the number of cubic meters
	// it occupies.

	IRON(8000D, new Color(160, 160, 160)),
	GOLD(20000D, Color.YELLOW),
	COPPER(9000D, new Color(255, 120, 60)),
	TIN(7300D, new Color(240, 240, 240)),
	BRONZE(8800D, new Color(255, 160, 60));

	private final double density;
	private final Color color;

	GearTypes(double matDensity, Color matColor){
		density = matDensity;
		color = matColor;
	}

	public double getDensity(){
		return density;
	}

	public Color getColor(){
		return color;
	}

	/**This will return the name with all but the first char being lowercase,
	 * so COPPER becomes Copper, which is good for oreDict and registry
	 */
	@Override
	public String toString(){
		String name = name();
		char char1 = name.charAt(0);
		name = name.substring(1);
		name = name.toLowerCase();
		name = char1 + name;
		return name;
	}
}
