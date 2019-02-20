package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class SquidHelmet extends ItemArmor{

	public SquidHelmet(){
		super(ModItems.BOBO, 1, EntityEquipmentSlot.HEAD);
		setMaxStackSize(1);
		String name = "squid_helmet";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(player.getAir() <= 150){
			player.setAir(300);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SQUID_DEATH, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
