package com.Da_Technomancer.crossroads.API;

import com.Da_Technomancer.crossroads.API.packets.SafeCallable;
import com.Da_Technomancer.essentials.ReflectionUtil;
import net.minecraft.entity.monster.ZombieVillagerEntity;
import net.minecraft.world.Explosion;
import net.minecraft.world.biome.BiomeContainer;
import net.minecraft.world.biome.BiomeGenerationSettings;
import net.minecraft.world.server.ChunkManager;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public enum CRReflection implements ReflectionUtil.IReflectionKey{

	SET_CHAT(CRReflection::getChatClass, "NO_MCP_MAPPING", "func_238493_a_", "Update the chat log without spamming it"),
	CURE_ZOMBIE(ZombieVillagerEntity.class, "startConverting", "func_191991_a", "Cure zombie villagers with SO2"),
	EXPLOSION_POWER(Explosion.class, "size", "field_77280_f", "Perpetuate explosions with Collapse beams (1)"),
	EXPLOSION_SMOKE(Explosion.class, "causesFire", "field_77286_a", "Perpetuate explosions with Collapse beams (2)"),
	EXPLOSION_MODE(Explosion.class, "mode", "field_222260_b", "Perpetuate explosions with Collapse beams (3)"),
//	SWING_TIME(LivingEntity.class, "ticksSinceLastSwing", "field_184617_aD", "Mechanical Arm attacking"),
	ENTITY_LIST(ServerWorld.class, "entitiesByUuid", "field_175741_N", "Prevent mob spawning with Closure beams, modify explosions with Collapse/Equilibrium beams"),
	LOADED_CHUNKS(ChunkManager.class, "getLoadedChunksIterable", "func_223491_f ", "Spawn lightning at high atmospheric charge"),
	LIGHTNING_POS(ServerWorld.class, "adjustPosToNearbyEntity", "func_175736_a", "Target lightning at high atmospheric charge"),
	SPAWN_RADIUS(ChunkManager.class, "isOutsideSpawningRadius", "func_219243_d", "Spawn lightning at high atmospheric charge"),
	BIOME_ARRAY(BiomeContainer.class, "biomes", "field_227054_f_", "Terraforming alchemy reagents changing the biome"),
	BIOME_FEATURE_LIST(BiomeGenerationSettings.class, "NO_MCP_MAPPING", "field_242484_f", "Adding ore world generation");

	private Class<?> clazz;
	@Nullable
	private final Supplier<Class<?>> clazzSupplier;
	public final String obf;//Obfuscated name
	public final String mcp;//Human readable MCP name
	private final String purpose;

	CRReflection(@Nonnull Supplier<Class<?>> clazz, String mcp, String obf, String purpose){
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
