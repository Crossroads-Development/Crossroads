package com.Da_Technomancer.crossroads.entity.mob_effects;

import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import javax.annotation.Nullable;

public class Curative extends MobEffect{

	public Curative(){
		super(MobEffectCategory.NEUTRAL, 0xFFFFFF);
		setRegistryName(Crossroads.MODID, "curative");
		//Basically a milk bucket in potion form
	}

	@Override
	public void applyInstantenousEffect(@Nullable Entity p_180793_1_, @Nullable Entity p_180793_2_, LivingEntity self, int p_180793_4_, double p_180793_5_){
		if(!self.level.isClientSide){
			self.curePotionEffects(new ItemStack(Items.MILK_BUCKET));
		}
	}

	@Override
	public boolean isInstantenous(){
		return true;
	}
}
