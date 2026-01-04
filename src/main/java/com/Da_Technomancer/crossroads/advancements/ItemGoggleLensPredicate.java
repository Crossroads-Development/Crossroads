package com.Da_Technomancer.crossroads.advancements;

import com.Da_Technomancer.crossroads.api.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.technomancy.ArmorGoggles;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.item.ItemStack;

public record ItemGoggleLensPredicate(EnumGoggleLenses lens) implements SingleComponentItemPredicate<ArmorGoggles.LensesSet>{

	public static final Codec<ItemGoggleLensPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(EnumGoggleLenses.CODEC.fieldOf("lens").forGetter(ItemGoggleLensPredicate::lens)).apply(instance, ItemGoggleLensPredicate::new));

	@Override
	public DataComponentType<ArmorGoggles.LensesSet> componentType(){
		return CRItems.GOGGLE_LENSES_DATA.value();
	}

	@Override
	public boolean matches(ItemStack stack, ArmorGoggles.LensesSet lensesSet){
		return lensesSet.lenses().containsKey(lens);
	}
}
