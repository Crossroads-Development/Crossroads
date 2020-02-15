package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class SquidHelmet extends ArmorItem{

	protected SquidHelmet(){
		super(ChickenBoots.BOBO_MATERIAL, EquipmentSlotType.HEAD, new Properties().group(CRItems.TAB_CROSSROADS).maxStackSize(1));
		String name = "squid_helmet";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void onArmorTick(ItemStack stack, World world, PlayerEntity player){
		if(player.getAir() <= 150){
			player.setAir(300);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SQUID_DEATH, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}

}
