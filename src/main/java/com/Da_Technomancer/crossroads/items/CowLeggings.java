package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class CowLeggings extends ArmorItem{

	protected CowLeggings(){
		super(ChickenBoots.BOBO_MATERIAL, EquipmentSlotType.LEGS, new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "cow_leggings";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		if(player.getEffect(Effects.POISON) != null || player.getEffect(Effects.WITHER) != null || player.getEffect(Effects.CONFUSION) != null || player.getEffect(Effects.BLINDNESS) != null || player.getEffect(Effects.MOVEMENT_SLOWDOWN) != null || player.getEffect(Effects.WEAKNESS) != null || player.getEffect(Effects.HUNGER) != null){
			player.removeEffect(Effects.POISON);
			player.removeEffect(Effects.WITHER);
			player.removeEffect(Effects.CONFUSION);
			player.removeEffect(Effects.BLINDNESS);
			player.removeEffect(Effects.MOVEMENT_SLOWDOWN);
			player.removeEffect(Effects.WEAKNESS);
			player.removeEffect(Effects.HUNGER);
			player.removeEffect(Effects.DIG_SLOWDOWN);
			player.removeEffect(Effects.UNLUCK);
			player.removeEffect(Effects.BAD_OMEN);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.COW_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
