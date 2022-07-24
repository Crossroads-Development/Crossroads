package com.Da_Technomancer.crossroads.crafting;

import com.mojang.serialization.Codec;
import net.minecraftforge.common.loot.IGlobalLootModifier;

import java.util.HashMap;

public final class CRLootModifiers{

	public static final HashMap<String, Codec<? extends IGlobalLootModifier>> toRegister = new HashMap<>(1);

	public static void init(){
		toRegister.put("piglin_barter", PiglinBarterLootModifier.CODEC);
	}
}
