package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundEvents;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;

public class PigZombieChestsplate extends ArmorItem{

	public PigZombieChestsplate(){
		super(CRItems.BOBO, 1, EquipmentSlotType.CHEST);
		setMaxStackSize(1);
		String name = "pig_zombie_chestplate";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CRItems.TAB_CROSSROADS);
		CRItems.toRegister.add(this);
		CRItems.itemAddQue(this);
	}

	@Override
	public void onArmorTick(World world, PlayerEntity player, ItemStack stack){
		//Believe it or not, it is possible to die of fire while im lava without burning (if it is raining on the player). There is an isInLava check for this reason. 
		if(player.getActivePotionEffect(Effects.FIRE_RESISTANCE) == null && (player.isBurning() || player.isInLava())){
			player.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE, 10, 0, false, false));
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_PIG_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
