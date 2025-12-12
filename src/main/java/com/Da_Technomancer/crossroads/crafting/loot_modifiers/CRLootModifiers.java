package com.Da_Technomancer.crossroads.crafting.loot_modifiers;

import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;

import java.util.HashMap;

public final class CRLootModifiers{

	public static final HashMap<String, MapCodec<? extends IGlobalLootModifier>> toRegister = new HashMap<>(2);

	public static void init(){
		toRegister.put("piglin_barter", PiglinBarterLootModifier.MAP_CODEC);
		toRegister.put("sized_mob_drops", SizedMobDropsLootModifier.MAP_CODEC);
	}
}
