package com.Da_Technomancer.crossroads.items;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;

public class SquidHelmet extends ArmorItem{

	protected SquidHelmet(){
		super(ChickenBoots.BOBO_MATERIAL, Type.HELMET, new Properties().stacksTo(1));
		String name = "squid_helmet";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void onInventoryTick(ItemStack stack, Level world, Player player, int slotIndex, int selectedIndex){
		if(slotIndex - 36 < 4){//Is equipped
			if(player.getAirSupply() <= 150){
				player.setAirSupply(300);
				world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.SQUID_DEATH, SoundSource.PLAYERS, 2.5F, 1F);
			}
		}
	}
}
