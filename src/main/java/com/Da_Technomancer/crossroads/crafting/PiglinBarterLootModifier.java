package com.Da_Technomancer.crossroads.crafting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;

public class PiglinBarterLootModifier extends LootModifier{

	protected static final Codec<PiglinBarterLootModifier> CODEC = RecordCodecBuilder.create(inst -> codecStart(inst)
		.and(Codec.BOOL.optionalFieldOf("active", true).forGetter(PiglinBarterLootModifier::isActive))
		.and(Codec.STRING.fieldOf("item_name").forGetter(PiglinBarterLootModifier::getItem))
		.and(Codec.INT.optionalFieldOf("min", 1).forGetter(PiglinBarterLootModifier::getMin))
		.and(Codec.INT.optionalFieldOf("max", 1).forGetter(PiglinBarterLootModifier::getMax))
		.and(Codec.FLOAT.fieldOf("override_chance").forGetter(PiglinBarterLootModifier::getOverrideChance))
		.apply(inst, PiglinBarterLootModifier::new));

	private final boolean active;
	private final String itemName;
	private final Item item;
	private final int min;
	private final int max;
	private final float overrideChance;

	private PiglinBarterLootModifier(LootItemCondition[] conditions, boolean active, String itemName, int min, int max, float overrideChance){
		super(conditions);
		this.active = active;
		this.itemName = itemName;
		this.item = CraftingHelper.getItem(itemName, true);
		this.min = min;
		this.max = max;
		this.overrideChance = overrideChance;
		assert min <= max && min > 0;
	}

	private boolean isActive(){
		return active;
	}

	private String getItem(){
		return itemName;
	}

	private int getMin(){
		return min;
	}

	private int getMax(){
		return max;
	}

	private float getOverrideChance(){
		return overrideChance;
	}

	@Nonnull
	@Override
	protected ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context){
		if(active && isPiglinBarter(context) && context.getRandom().nextFloat() < overrideChance){
			generatedLoot.clear();
			generatedLoot.add(new ItemStack(item, context.getRandom().nextIntBetweenInclusive(min, max)));
		}

		return generatedLoot;
	}

	private static boolean isPiglinBarter(LootContext context){
		return context.getParamOrNull(LootContextParams.THIS_ENTITY) instanceof Piglin && !context.hasParam(LootContextParams.ORIGIN);
	}

	@Override
	public Codec<? extends IGlobalLootModifier> codec(){
		return CODEC;
	}
}
