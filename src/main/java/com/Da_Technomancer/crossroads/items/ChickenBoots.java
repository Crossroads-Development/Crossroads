package com.Da_Technomancer.crossroads.items;

import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class ChickenBoots extends ArmorItem{

	protected static final IArmorMaterial BOBO_MATERIAL = new BoboMat();

	protected ChickenBoots(){
		super(BOBO_MATERIAL, EquipmentSlotType.FEET, CRItems.itemProp.maxStackSize(1));
		String name = "chicken_boots";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	private static class BoboMat implements IArmorMaterial{

		@Override
		public int getDurability(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getDamageReductionAmount(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getEnchantability(){
			return 0;
		}

		@Override
		public SoundEvent getSoundEvent(){
			return SoundEvents.ENTITY_HORSE_DEATH;//I am not a benevolent overlord. RIP the ears of anyone who uses headphones
		}

		@Override
		public Ingredient getRepairMaterial(){
			return Ingredient.EMPTY;
		}

		@Override
		public String getName(){
			return "bobo";
		}

		@Override
		public float getToughness(){
			return 0;
		}
	}
}
