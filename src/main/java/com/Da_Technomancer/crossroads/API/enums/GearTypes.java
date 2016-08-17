package com.Da_Technomancer.crossroads.API.enums;

import java.awt.Color;

public enum GearTypes{

	// The densities for the materials used here are kg/cubic meter of
	// the substance, for gears multiply by the number of cubic meters
	// it occupies.

	IRON(8000D, new Color(128, 128, 128)),
	GOLD(20000D, Color.YELLOW),
	COPPER(9000D, new Color(255, 40, 0)),
	TIN(7300D, new Color(200, 200, 200)),
	BRONZE(8800D, new Color(255, 80, 0));

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

	@Override
	public String toString(){
		// This will return the name with all but the first char being
		// lowercase,
		// so COPPER becomes Copper, which is good for oreDict and registry
		// names.
		String char1;
		String name = name();
		char1 = name.substring(0, 1);
		name = name.substring(1);
		name = name.toLowerCase();
		name = char1 + name;
		return name;
	}

}
