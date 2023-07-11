package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.Ingredient;

public class ChickenBoots extends ArmorItem{

	protected static final ArmorMaterial BOBO_MATERIAL = new BoboMat();

	protected ChickenBoots(){
		super(BOBO_MATERIAL, Type.BOOTS, new Properties().stacksTo(1));
		String name = "chicken_boots";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	private static class BoboMat implements ArmorMaterial{

		@Override
		public int getDurabilityForType(Type type){
			return 0;
		}

		@Override
		public int getDefenseForType(Type type){
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
