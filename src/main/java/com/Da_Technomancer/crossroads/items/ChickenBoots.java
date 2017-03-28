package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ChickenBoots extends ItemArmor{

	public ChickenBoots(){
		super(ModItems.BOBO, 1, EntityEquipmentSlot.FEET);
		setMaxStackSize(1);
		String name = "chicken_boots";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		if(stack.getItemDamage() != getMaxDamage(stack) && player.fallDistance > 3){
			player.fallDistance = 0;
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
			stack.damageItem(1, player);
		}
	}
}
