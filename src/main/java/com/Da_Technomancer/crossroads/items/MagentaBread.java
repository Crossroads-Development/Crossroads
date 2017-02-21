package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagentaBread extends ItemFood{

	public MagentaBread(){
		super(20, .5F, false);
		String name = "magenta_bread";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, EntityPlayer player){
		if(!worldIn.isRemote){
			player.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST, 3600, 20));
			player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 3600, 100));
			player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 3600, 10));
		}
	}
}
