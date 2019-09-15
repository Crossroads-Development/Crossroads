package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SquidHelmet extends ArmorItem{

	public SquidHelmet(){
		super(CrossroadsItems.BOBO, 1, EquipmentSlotType.HEAD);
		setMaxStackSize(1);
		String name = "squid_helmet";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack){
		if(player.getAir() <= 150){
			player.setAir(300);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SQUID_DEATH, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
