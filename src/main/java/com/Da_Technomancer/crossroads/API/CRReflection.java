package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.ZombieVillager;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.chunk.ChunkBiomeContainer;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.LevelChunk;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum CRReflection implements ReflectionUtil.IReflectionKey{

	//TODO review

	SET_CHAT(CRReflection::getChatClass, "func_238493_a_", "addMessage", "Update the chat log without spamming it"),
	CURE_ZOMBIE(ZombieVillager.class, "func_191991_a", "startConverting", "Cure zombie villagers with SO2"),
	EXPLOSION_POWER(Explosion.class, "field_77280_f", "radius", "Perpetuate explosions with Collapse beams (1)"),
	EXPLOSION_SMOKE(Explosion.class, "field_77286_a", "fire", "Perpetuate explosions with Collapse beams (2)"),
	EXPLOSION_MODE(Explosion.class, "field_222260_b", "blockInteraction", "Perpetuate explosions with Collapse beams (3)"),
//	SWING_TIME(LivingEntity.class, "field_184617_aD", "attackStrengthTicker", "Mechanical Arm attacking"),
	ENTITY_LIST(ServerLevel.class, "field_175741_N", "entitiesByUuid", "Prevent mob spawning with Closure beams, modify explosions with Collapse/Equilibrium beams"),
	LOADED_CHUNKS(ChunkMap.class, "func_223491_f ", "getChunks ", "Spawn lightning at high atmospheric charge"),
	LIGHTNING_POS(ServerLevel.class, "func_175736_a", "findLightingTargetAround", "Target lightning at high atmospheric charge"),
	SPAWN_RADIUS(ChunkMap.class, "func_219243_d", "noPlayersCloseForSpawning", "Spawn lightning at high atmospheric charge"),
	BIOME_ARRAY(ChunkBiomeContainer.class, "field_227054_f_", "biomes", "Terraforming alchemy reagents changing the biome"),
	OFFSPRING_SPAWN_EGG(Mob.class, "func_213406_a", "onOffspringSpawnedFromEgg", "Imprinting on cloned foxes"),
	CHUNK_TICKER_MAP(LevelChunk.class, "f_156362_", "tickersInLevel", "Tick accelerating tile entities");


	private Class<?> clazz;
	@Nullable
	private final Supplier<Class<?>> clazzSupplier;
	public final String obf;//Obfuscated name
	public final String mcp;//Human readable mapped name
	private final String purpose;

	CRReflection(@Nonnull Supplier<Class<?>> clazz, String obf, String mcp, String purpose){
		//The supplier is for loading classes which are only in the client dist; This handles the exception and allows servers to start
		this.clazzSupplier = clazz;
		this.obf = obf;
		this.mcp = mcp;
		this.purpose = purpose;
	}

	CRReflection(@Nullable Class<?> clazz, String obf, String mcp, String purpose){
		this.clazz = clazz;
		clazzSupplier = null;
		this.obf = obf;
		this.mcp = mcp;
		this.purpose = purpose;
	}

	@Nullable
	@Override
	public Class<?> getSourceClass(){
		if(clazz == null){
			assert clazzSupplier != null;
			clazz = clazzSupplier.get();
		}
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

	private static Class<?> getChatClass(){
		return SafeCallable.getChatClass();
	}
}
