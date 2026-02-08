package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.essentials.api.ReflectionUtil;

public enum CRReflection implements ReflectionUtil.IReflectionKey{

	//For finding the obfuscated names, see the resource https://linkie.shedaniel.dev/mappings

//	CURE_ZOMBIE(ZombieVillager.class, "ckx.a", "startConverting", "Cure zombie villagers with SO2"),
	//	SWING_TIME(LivingEntity.class, "field_184617_aD", "attackStrengthTicker", "Mechanical Arm attacking"),
//	ENTITY_LIST(ServerLevel.class, "field_175741_N", "entitiesByUuid", "Prevent mob spawning with Closure beams, modify explosions with Collapse/Equilibrium beams"),
//	LOADED_CHUNKS(ChunkMap.class, "aqb.k", "getChunks", "Spawn lightning at high atmospheric charge"),
//	LIGHTNING_POS(ServerLevel.class, "aqu.b", "findLightningTargetAround", "Target lightning at high atmospheric charge"),
//	FOX_TRUSTED_UUID(Fox.class, "cfo.b", "addTrustedUUID", "Imprinting for cloned foxes"),
//	CHUNK_TICKER_MAP(LevelChunk.class, "dvi.p", "tickersInLevel", "Tick accelerating tile entities"),
//	BIOME_SEED(BiomeManager.class, "ddy.f", "biomeZoomSeed", "Terraforming alchemy reagents changing the biome at precise positions"),
//	BIOME_TEMPERATURE_NO_CACHE(Biome.class, "ddw.e", "getHeightAdjustedTemperature", "Getting the biome temperature"),
//	DISPENSER_BEHAVIOR_MAP(DispenserBlock.class, "did.d", "DISPENSER_REGISTRY", "Letting dispensers place items in embryo labs");
	;

	private final Class<?> clazz;
	public final String obf;//Obfuscated name
	public final String mcp;//Human readable mapped name
	private final String purpose;

	CRReflection(Class<?> clazz, String obf, String mcp, String purpose){
		this.clazz = clazz;
		this.obf = obf;
		this.mcp = mcp;
		this.purpose = purpose;
	}

	@Override
	public Class<?> getSourceClass(){
		return clazz;
	}

	@Override
	public String getObfName(){
		return obf;
	}

	@Override
	public String getMcpName(){
		return mcp;
	}

	@Override
	public String getPurpose(){
		return purpose;
	}
}
