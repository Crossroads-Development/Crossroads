package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class ChickenBoots extends ArmorItem{

	protected static final IArmorMaterial BOBO_MATERIAL = new BoboMat();

	protected ChickenBoots(){
		super(BOBO_MATERIAL, EquipmentSlotType.FEET, new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "chicken_boots";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
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
			return Crossroads.MODID + ":bobo";
		}

		@Override
		public float getToughness(){
			return 0;
		}

		@Override
		public float func_230304_f_(){
			return 0;//Knockback resistance
		}
	}
}
