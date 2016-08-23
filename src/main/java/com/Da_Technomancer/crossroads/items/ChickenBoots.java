package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.Main;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ChickenBoots extends ItemArmor{

	private static ArmorMaterial CHICKEN = EnumHelper.addArmorMaterial("CHICKEN", Main.MODID + ":chicken", 0, new int[4], 30, SoundEvents.ENTITY_CHICKEN_HURT, 0F);

	public ChickenBoots(){
		super(CHICKEN, 1, EntityEquipmentSlot.FEET);
		this.setMaxStackSize(1);
		String name = "chickenBoots";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	public void onArmorTick(World world, EntityPlayer player, ItemStack itemStack){
		if(player.fallDistance > 1){
			player.fallDistance = 0;
			world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_CHICKEN_HURT, SoundCategory.PLAYERS, 2.5F, 1F);
		}
	}
}
