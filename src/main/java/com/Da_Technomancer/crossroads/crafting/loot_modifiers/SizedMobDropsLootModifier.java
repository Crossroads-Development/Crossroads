package com.Da_Technomancer.crossroads.crafting.loot_modifiers;

import com.Da_Technomancer.crossroads.api.crafting.CraftingUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class SizedMobDropsLootModifier extends LootModifier{

	protected static final MapCodec<SizedMobDropsLootModifier> MAP_CODEC = RecordCodecBuilder.mapCodec(inst -> codecStart(inst)
			.and(inst.group(Codec.BOOL.optionalFieldOf("active", true).forGetter(SizedMobDropsLootModifier::isActive),
					Codec.BOOL.fieldOf("can_scale_up").forGetter(SizedMobDropsLootModifier::canScaleUp),
					Codec.BOOL.fieldOf("can_scale_down").forGetter(SizedMobDropsLootModifier::canScaleDown),
					TagKey.codec(Registries.ITEM).fieldOf("item_filter").forGetter(SizedMobDropsLootModifier::getItemFilter),
					Codec.BOOL.fieldOf("is_item_filter_allowlist").forGetter(SizedMobDropsLootModifier::isItemFilterAllowlist),
					TagKey.codec(Registries.ENTITY_TYPE).fieldOf("entity_filter").forGetter(SizedMobDropsLootModifier::getEntityFilter),
					Codec.BOOL.fieldOf("is_entity_filter_allowlist").forGetter(SizedMobDropsLootModifier::isEntityFilterAllowlist)))
			.apply(inst, SizedMobDropsLootModifier::new));


	private final boolean active;
	private final boolean canScaleUp;
	private final boolean canScaleDown;
	private final TagKey<Item> itemFilter;
	private final boolean isItemFilterAllowlist;
	private final TagKey<EntityType<?>> entityFilter;
	private final boolean isEntityFilterAllowlist;

	private SizedMobDropsLootModifier(LootItemCondition[] conditionsIn, boolean active, boolean canScaleUp, boolean canScaleDown, TagKey<Item> itemFilter, boolean isItemFilterAllowlist, TagKey<EntityType<?>> entityFilter, boolean isEntityFilterAllowlist){
		super(conditionsIn);
		this.active = active;
		this.canScaleUp = canScaleUp;
		this.canScaleDown = canScaleDown;
		this.itemFilter = itemFilter;
		this.isItemFilterAllowlist = isItemFilterAllowlist;
		this.entityFilter = entityFilter;
		this.isEntityFilterAllowlist = isEntityFilterAllowlist;
	}

	private boolean isActive(){
		return active;
	}

	private boolean canScaleUp(){
		return canScaleUp;
	}

	private boolean canScaleDown(){
		return canScaleDown;
	}

	private TagKey<Item> getItemFilter(){
		return itemFilter;
	}

	private boolean isItemFilterAllowlist(){
		return isItemFilterAllowlist;
	}

	private TagKey<EntityType<?>> getEntityFilter(){
		return entityFilter;
	}

	private boolean isEntityFilterAllowlist(){
		return isEntityFilterAllowlist;
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
		if(active && isMobDrop(context) && !generatedLoot.isEmpty() && context.getParam(LootContextParams.THIS_ENTITY) instanceof LivingEntity living && (living.getType().is(entityFilter) == isEntityFilterAllowlist)){
			AttributeInstance scaleAttribute = living.getAttributes().getInstance(Attributes.SCALE);
			double baseScale;
			if(scaleAttribute != null && (baseScale = scaleAttribute.getBaseValue()) > 0){
				//Scales with the cube of size
				double scale = Math.pow(Math.max(0, scaleAttribute.getValue() / baseScale), 3D);
				if(scale > 1.1F && canScaleUp || scale < 0.9F && canScaleDown){
					for(int i = 0; i < generatedLoot.size(); i++){
						ItemStack generated = generatedLoot.get(i);
						if(CraftingUtil.tagContains(itemFilter, generated.getItem()) == isItemFilterAllowlist){
							int newCount = scaleCount(generated.getCount(), scale);
							if(newCount == 0){
								generatedLoot.remove(i--);
							}else{
								//When increasing size of drops, need to split it into stacks
								final int maxStackSize = generated.getMaxStackSize();
								while(newCount > maxStackSize){
									generatedLoot.add(i++, generated.copyWithCount(maxStackSize));
									newCount -= maxStackSize;
								}
								generated.setCount(newCount);
							}
						}
					}
				}
			}
		}

		return generatedLoot;
	}

	private static int scaleCount(int baseCount, double scale){
		double scaled = baseCount * scale;
		int result = (int) scaled;
		double chance = scaled - result;
		if(Math.random() < chance){
			result += 1;
		}
		return result;
	}

	private static boolean isMobDrop(LootContext context){
		//Not really sure how to filter these properly
		//The whole loot context system seems half-baked
		return context.hasParam(LootContextParams.THIS_ENTITY) && context.hasParam(LootContextParams.DAMAGE_SOURCE);
	}

	@Override
	public MapCodec<? extends IGlobalLootModifier> codec(){
		return MAP_CODEC;
	}
}
