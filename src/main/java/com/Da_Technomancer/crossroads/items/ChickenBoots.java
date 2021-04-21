package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

public class ChickenBoots extends ArmorItem{

	protected static final IArmorMaterial BOBO_MATERIAL = new BoboMat();

	protected ChickenBoots(){
		super(BOBO_MATERIAL, EquipmentSlotType.FEET, new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
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
		public int getDurabilityForSlot(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getDefenseForSlot(EquipmentSlotType slotIn){
			return 0;
		}

		@Override
		public int getEnchantmentValue(){
			return 0;
		}

		@Override
		public SoundEvent getEquipSound(){
			return SoundEvents.HORSE_DEATH;//I am not a benevolent overlord. RIP the ears of anyone who uses headphones
		}

		@Override
		public Ingredient getRepairIngredient(){
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
		public float getKnockbackResistance(){
			return 0;
		}
	}
}
