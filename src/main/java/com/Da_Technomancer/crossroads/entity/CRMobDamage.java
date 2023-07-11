package com.Da_Technomancer.crossroads.entity;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class CRMobDamage{

	public static final ResourceKey<DamageType> SALT = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "salt"));
	public static final ResourceKey<DamageType> DRILL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "drill"));
	public static final ResourceKey<DamageType> CR_SYRINGE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "cr_syringe"));
	public static final ResourceKey<DamageType> VODKA = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "vodka"));
	public static final ResourceKey<DamageType> WINDMILL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "windmill"));
	public static final ResourceKey<DamageType> VOID = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "void"));
	public static final ResourceKey<DamageType> POTENTIALVOID = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "potentialvoid"));
	public static final ResourceKey<DamageType> CHEMICAL = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "chemical"));
	public static final ResourceKey<DamageType> NON_VIABLE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Crossroads.MODID, "non_viable"));

	private static Registry<DamageType> registryCache;

	public static Holder<DamageType> getDamageType(ResourceKey<DamageType> key, RegistryAccess access){
		if(registryCache == null){
			registryCache = access.registryOrThrow(Registries.DAMAGE_TYPE);
		}
		return registryCache.getHolderOrThrow(key);
	}

	public static DamageSource damageSource(ResourceKey<DamageType> typeKey, Level world){
		return new DamageSource(getDamageType(typeKey, world.registryAccess()));
	}

	public static DamageSource damageSource(ResourceKey<DamageType> typeKey, Level world, Entity attacker){
		return new DamageSource(getDamageType(typeKey, world.registryAccess()), attacker);
	}
}
