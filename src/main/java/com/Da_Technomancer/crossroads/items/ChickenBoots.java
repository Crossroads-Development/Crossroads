package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.EnumMap;
import java.util.List;

public class ChickenBoots extends ArmorItem{

	// TODO: this seems to be mandatory now? Probably shouldn't be in this class.
	protected static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister.create(BuiltInRegistries.ARMOR_MATERIAL, Crossroads.MODID);

	protected static final Holder<ArmorMaterial> BOBO_MATERIAL = ARMOR_MATERIALS.register("bobo", () -> new ArmorMaterial(
					Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
						map.put(Type.BOOTS, 0);
					}),
					0,
					SoundEvents.ARMOR_EQUIP_GENERIC, // lmao
					() -> Ingredient.EMPTY,
					null, //TODO: I have no idea what to pass here
					0,
					0
			)
	);

	protected ChickenBoots(){
		super(BOBO_MATERIAL, Type.BOOTS, new Properties().stacksTo(1).rarity(CRItems.BOBO_RARITY));
		String name = "chicken_boots";
		CRItems.queueForRegister(name, this);
	}
}
