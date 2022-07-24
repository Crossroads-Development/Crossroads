package com.Da_Technomancer.crossroads.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class CowLeggings extends ArmorItem{

	protected CowLeggings(){
		super(ChickenBoots.BOBO_MATERIAL, EquipmentSlot.LEGS, new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "cow_leggings";
		CRItems.toRegister.put(name, this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void onArmorTick(ItemStack stack, Level world, Player player){
		if(player.getEffect(MobEffects.POISON) != null || player.getEffect(MobEffects.WITHER) != null || player.getEffect(MobEffects.CONFUSION) != null || player.getEffect(MobEffects.BLINDNESS) != null || player.getEffect(MobEffects.MOVEMENT_SLOWDOWN) != null || player.getEffect(MobEffects.WEAKNESS) != null || player.getEffect(MobEffects.HUNGER) != null){
			player.removeEffect(MobEffects.POISON);
			player.removeEffect(MobEffects.WITHER);
			player.removeEffect(MobEffects.CONFUSION);
			player.removeEffect(MobEffects.BLINDNESS);
			player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
			player.removeEffect(MobEffects.WEAKNESS);
			player.removeEffect(MobEffects.HUNGER);
			player.removeEffect(MobEffects.DIG_SLOWDOWN);
			player.removeEffect(MobEffects.UNLUCK);
			player.removeEffect(MobEffects.BAD_OMEN);
			world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.COW_HURT, SoundSource.PLAYERS, 2.5F, 1F);
		}
	}
}
