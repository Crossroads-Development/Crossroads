package com.Da_Technomancer.crossroads.items;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class MagentaBread extends ItemFood{

	public MagentaBread(){
		super(20, .5F, false);
		String name = "magenta_bread";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean hasEffect(ItemStack stack){
		return true;
	}

	@Override
	protected void onFoodEaten(ItemStack stack, World worldIn, PlayerEntity player){
		if(!worldIn.isRemote){
			player.addPotionEffect(new EffectInstance(Effects.JUMP_BOOST, 3600, 20));
			player.addPotionEffect(new EffectInstance(Effects.SPEED, 3600, 100));
			player.addPotionEffect(new EffectInstance(Effects.NAUSEA, 3600, 10));
		}
	}
}
