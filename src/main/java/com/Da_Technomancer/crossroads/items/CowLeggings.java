package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CowLeggings extends ItemArmor{

	public CowLeggings(){
		super(ChickenBoots.BOBO, 2, EntityEquipmentSlot.LEGS);
		this.setMaxStackSize(1);
		String name = "cowLeggings";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if(player.getActivePotionEffect(MobEffects.POISON) != null || player.getActivePotionEffect(MobEffects.WITHER) != null || player.getActivePotionEffect(MobEffects.NAUSEA) != null || player.getActivePotionEffect(MobEffects.BLINDNESS) != null || player.getActivePotionEffect(MobEffects.SLOWNESS) != null || player.getActivePotionEffect(MobEffects.WEAKNESS) != null || player.getActivePotionEffect(MobEffects.HUNGER) != null){
			player.removePotionEffect(MobEffects.POISON);
			player.removePotionEffect(MobEffects.WITHER);
			player.removePotionEffect(MobEffects.NAUSEA);
			player.removePotionEffect(MobEffects.BLINDNESS);
			player.removePotionEffect(MobEffects.SLOWNESS);
			player.removePotionEffect(MobEffects.WEAKNESS);
			player.removePotionEffect(MobEffects.HUNGER);
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_COW_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
