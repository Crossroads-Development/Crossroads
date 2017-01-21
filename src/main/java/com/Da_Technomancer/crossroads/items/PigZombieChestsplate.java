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
import net.minecraftforge.fml.common.registry.GameRegistry;

public class PigZombieChestsplate extends ItemArmor{

	public PigZombieChestsplate(){
		super(ModItems.BOBO, 1, EntityEquipmentSlot.CHEST);
		this.setMaxStackSize(1);
		String name = "pigZombieChestplate";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if(player.isBurning() && player.getActivePotionEffect(MobEffects.FIRE_RESISTANCE) == null){
			player.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 5, 0, false, false));
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ZOMBIE_PIG_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
