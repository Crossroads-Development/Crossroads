package com.Da_Technomancer.crossroads.api;

import com.Da_Technomancer.essentials.api.ReflectionUtil;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.common.BiomeManager;

public enum CRReflection implements ReflectionUtil.IReflectionKey{

	CURE_ZOMBIE(ZombieVillager.class, "m_34383_", "startConverting", "Cure zombie villagers with SO2"),
//	SWING_TIME(LivingEntity.class, "field_184617_aD", "attackStrengthTicker", "Mechanical Arm attacking"),
//	ENTITY_LIST(ServerLevel.class, "field_175741_N", "entitiesByUuid", "Prevent mob spawning with Closure beams, modify explosions with Collapse/Equilibrium beams"),
	LOADED_CHUNKS(ChunkMap.class, "m_140416_", "getChunks", "Spawn lightning at high atmospheric charge"),
	LIGHTNING_POS(ServerLevel.class, "m_143288_", "findLightningTargetAround", "Target lightning at high atmospheric charge"),
	OFFSPRING_SPAWN_EGG(Mob.class, "m_5502_", "onOffspringSpawnedFromEgg", "Imprinting on cloned foxes"),
	CHUNK_TICKER_MAP(LevelChunk.class, "f_156362_", "tickersInLevel", "Tick accelerating tile entities"),
	BIOME_SEED(BiomeManager.class, "f_47863_", "biomeZoomSeed", "Terraforming alchemy reagents changing the biome at precise positions"),
	BIOME_TEMPERATURE_NO_CACHE(Biome.class, "m_47528_", "getHeightAdjustedTemperature", "Getting the biome temperature"),
	DISPENSER_BEHAVIOR_MAP(DispenserBlock.class, "f_52661_", "DISPENSER_REGISTRY", "Letting dispensers place items in embryo labs");

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
