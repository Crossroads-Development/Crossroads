package com.Da_Technomancer.crossroads.crafting.loot_modifiers;

import com.Da_Technomancer.crossroads.crafting.CraftingUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.monster.piglin.PiglinEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.loot.functions.SetCount;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.List;

public class PiglinBarterLootModifier extends LootModifier{

	private final boolean active;
	private final LootPool pool;
	private final float overrideChance;

	private PiglinBarterLootModifier(ILootCondition[] conditions, boolean active, LootPool pool, float overrideChance){
		super(conditions);
		this.active = active;
		this.pool = pool;
		this.overrideChance = overrideChance;
	}

	@Nonnull
	@Override
	protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context){
		if(active && isPiglinBarter(context) && context.getRandom().nextFloat() < overrideChance){
			generatedLoot.clear();
			pool.generate(generatedLoot::add, context);
		}

		return generatedLoot;
	}

	private static boolean isPiglinBarter(LootContext context){
		return context.get(LootParameters.THIS_ENTITY) instanceof PiglinEntity && !context.has(LootParameters.field_237457_g_);
	}

	public static class Serializer extends GlobalLootModifierSerializer<PiglinBarterLootModifier>{

		/*
		 * Allows appending to the normal piglin trading loot table
		 *
		 * Format:
		 *
		 * "type": "crossroads:piglin_barter",
		 * "active": <boolean> //Optional, defaults to true. False will make this loot modifier be ignored
		 * "conditions": [...] //Same as normal
		 * "override_chance": <float> //[0.0, 1.0], chance of this loot table being used instead of vanilla/previous loot table
		 * "results": [ //Define the possible items. Note: This is not a real loot table, and is much more limited. It can't handle anything not specified below
		 * 		{
		 * 			"weight": <int> //Optional, default 1. Weight within this loot table pool
		 * 			"name": <string> //Item registry name
		 * 			"min": <int> //Optional, default 1. Minimum stacksize
		 * 			"max": <int> //Optional, default 1. Maximum stacksize
		 * 		},
		 * 		... //As many items as desired
		 * ]
		 */
		@Override
		public PiglinBarterLootModifier read(ResourceLocation name, JsonObject json, ILootCondition[] lootConditions){
			boolean active = CraftingUtil.isActiveJSON(json);//Can be disabled by having "active": false
			if(!active){
				return new PiglinBarterLootModifier(lootConditions, false, null, 0);
			}
			float overrideChance = JSONUtils.getFloat(json, "override_chance");//The chance [0-1] that this loot pool is used instead of the default piglin loot pool/previous pools
			LootPool.Builder poolBuilder = new LootPool.Builder();
			JsonArray entryArray = JSONUtils.getJsonArray(json, "results");
			for(JsonElement o : entryArray){
				JsonObject entry = o.getAsJsonObject();
				poolBuilder.addEntry(ItemLootEntry.builder(JSONUtils.getItem(entry, "name")).weight(JSONUtils.getInt(entry, "weight", 1)).acceptFunction(SetCount.builder(RandomValueRange.of(JSONUtils.getInt(entry, "min", 1), JSONUtils.getInt(entry, "max", 1)))));
			}

			return new PiglinBarterLootModifier(lootConditions, active, poolBuilder.build(), overrideChance);
		}
	}
}
