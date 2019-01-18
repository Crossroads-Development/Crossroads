package com.Da_Technomancer.crossroads.API.alchemy;

/**
 * This class stores ID names for built-in reagents. Not every reagent needs to be defined in this enum, and not every name in this enum needs a registered reagent. This class is provided as a helper, and not a core part of the API
 */
public enum EnumReagents{

	WATER("water"),
	PHELOSTOGEN("phelostogen"),
	AETHER("aether"),
	ADAMANT("adamant"),
	SULFUR("sulfur"),
	SULPHUR("sulfur"),
	QUICKSILVER("quicksilver"),
	ALCHEMICAL_SALT("alc_salt"),
	GUNPOWDER("gunpowder"),
	SLAG("slag"),
	REDSTONE("redstone"),
	SALT("salt"),
	BEDROCK("bedrock"),
	SULFUR_DIOXIDE("sulfur_dioxide"),
	SULFUR_TRIOXIDE("sulfur_trioxide"),
	SULFURIC_ACID("sulfuric_acid"),
	NITRIC_ACID("nitric_acid"),
	HYDROCHLORIC_ACID("hydrochloric_acid"),
	AQUA_REGIA("aqua_regia"),
	CRYSTAL("crystal"),
	PHILOSOPHER("philosopher"),
	PRACTITIONER("practitioner"),
	CHLORINE("chlorine"),
	IRON("iron"),
	COPPER("copper"),
	TIN("tin"),
	GOLD("gold"),
	RUBY("ruby"),
	EMERALD("emerald"),
	DIAMOND("diamond"),
	QUARTZ("quartz"),
	VANADIUM("vanadium"),
	DENSUS("densus"),
	ANTI_DENSUS("anti_densus"),
	CAVORITE("cavorite"),
	HELLFIRE("hellfire"),
	ELEM_LIGHT("elem_light"),
	ELEM_RIFT("elem_rift"),
	ELEM_EQUAL("elem_equalibrium"),
	ELEM_FUSION("elem_fusion"),
	ELEM_CHARGE("elem_charge"),
	ELEM_TIME("elem_time");

	private final String id;

	EnumReagents(String name){
		id = name;
	}

	public String id(){
		return id;
	}

	@Override
	public String toString(){
		return id();
	}
}
