package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class SquidHelmet extends ItemArmor{

	public SquidHelmet(){
		super(ChickenBoots.BOBO, 1, EntityEquipmentSlot.HEAD);
		this.setMaxStackSize(1);
		String name = "squidHelmet";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if(player.getAir() <= 10){
			player.setAir(20);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_SQUID_DEATH, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
