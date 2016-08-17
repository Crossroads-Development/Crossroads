package com.Da_Technomancer.crossroads.API.enums;

public enum HeatConductors{

	QUARTZ(.05D, "gemQuartz"),
	IRON(.1D, "ingotIron"),
	COPPER(.33D, "ingotCopper"),
	DIAMOND(1D, "wireDiamond");

	private final double rate;
	private final String item;

	HeatConductors(double rate, String item){
		this.rate = rate;
		this.item = item;
	}

	public double getRate(){
		return rate;
	}

	public String getItem(){
		return item;
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
