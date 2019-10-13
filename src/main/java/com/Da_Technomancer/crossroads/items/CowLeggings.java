package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CowLeggings extends ArmorItem{

	protected CowLeggings(){
		super(ChickenBoots.BOBO_MATERIAL, EquipmentSlotType.LEGS, CRItems.itemProp.maxStackSize(1));
		String name = "cow_leggings";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		if(player.getActivePotionEffect(Effects.POISON) != null || player.getActivePotionEffect(Effects.WITHER) != null || player.getActivePotionEffect(Effects.NAUSEA) != null || player.getActivePotionEffect(Effects.BLINDNESS) != null || player.getActivePotionEffect(Effects.SLOWNESS) != null || player.getActivePotionEffect(Effects.WEAKNESS) != null || player.getActivePotionEffect(Effects.HUNGER) != null){
			player.removePotionEffect(Effects.POISON);
			player.removePotionEffect(Effects.WITHER);
			player.removePotionEffect(Effects.NAUSEA);
			player.removePotionEffect(Effects.BLINDNESS);
			player.removePotionEffect(Effects.SLOWNESS);
			player.removePotionEffect(Effects.WEAKNESS);
			player.removePotionEffect(Effects.HUNGER);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_COW_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
