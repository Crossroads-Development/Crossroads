package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum CRReflection implements ReflectionUtil.IReflectionKey{

	SET_CHAT(CRReflection::getChatClass, "func_238493_a_", "addMessage", "Update the chat log without spamming it"),
	CURE_ZOMBIE(ZombieVillagerEntity.class, "func_191991_a", "startConverting", "Cure zombie villagers with SO2"),
	EXPLOSION_POWER(Explosion.class, "field_77280_f", "radius", "Perpetuate explosions with Collapse beams (1)"),
	EXPLOSION_SMOKE(Explosion.class, "field_77286_a", "fire", "Perpetuate explosions with Collapse beams (2)"),
	EXPLOSION_MODE(Explosion.class, "field_222260_b", "blockInteraction", "Perpetuate explosions with Collapse beams (3)"),
//	SWING_TIME(LivingEntity.class, "field_184617_aD", "attackStrengthTicker", "Mechanical Arm attacking"),
	ENTITY_LIST(ServerWorld.class, "field_175741_N", "entitiesByUuid", "Prevent mob spawning with Closure beams, modify explosions with Collapse/Equilibrium beams"),
	LOADED_CHUNKS(ChunkManager.class, "func_223491_f ", "getChunks ", "Spawn lightning at high atmospheric charge"),
	LIGHTNING_POS(ServerWorld.class, "func_175736_a", "findLightingTargetAround", "Target lightning at high atmospheric charge"),
	SPAWN_RADIUS(ChunkManager.class, "func_219243_d", "noPlayersCloseForSpawning", "Spawn lightning at high atmospheric charge"),
	BIOME_ARRAY(BiomeContainer.class, "field_227054_f_", "biomes", "Terraforming alchemy reagents changing the biome");


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
