package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.world.item.Item.Properties;

public class MagentaBread extends Item{

	private static final Supplier<MobEffectInstance> jumpBoostSupplier = () -> new MobEffectInstance(MobEffects.JUMP, 3600, 20);
	private static final Supplier<MobEffectInstance> speedSupplier = () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 100);
	private static final Supplier<MobEffectInstance> nauseaSupplier = () -> new MobEffectInstance(MobEffects.CONFUSION, 3600, 10);

	protected MagentaBread(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).food(new FoodProperties.Builder().alwaysEat().nutrition(20).saturationMod(0.5F).effect(speedSupplier, 1).effect(jumpBoostSupplier, 1).effect(nauseaSupplier, 1).build()));
		String name = "magenta_bread";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public boolean isFoil(ItemStack stack){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.mag_bread.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
