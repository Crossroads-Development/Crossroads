package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class PigZombieChestsplate extends ItemArmor{

	public PigZombieChestsplate(){
		super(ModItems.BOBO, 1, EntityEquipmentSlot.CHEST);
		setMaxStackSize(1);
		String name = "pig_zombie_chestplate";
		setUnlocalizedName(name);
		setRegistryName(name);
		setCreativeTab(ModItems.tabCrossroads);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack stack){
		//Believe it or not, it is possible to die of fire while im lava without burning (if it is raining on the player). There is an isInLava check for this reason. 
		if(stack.getItemDamage() != getMaxDamage(stack) && player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null && (player.isBurning() || player.isInLava())){
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 10, 0, false, false));
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_PIG_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
			stack.damageItem(1, player);
		}
	}
}
