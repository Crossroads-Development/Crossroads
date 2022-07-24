package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class MagentaBread extends Item{

	private static final Supplier<MobEffectInstance> jumpBoostSupplier = () -> new MobEffectInstance(MobEffects.JUMP, 3600, 20);
	private static final Supplier<MobEffectInstance> speedSupplier = () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 100);
	private static final Supplier<MobEffectInstance> nauseaSupplier = () -> new MobEffectInstance(MobEffects.CONFUSION, 3600, 10);

	protected MagentaBread(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).food(new FoodProperties.Builder().alwaysEat().nutrition(20).saturationMod(0.5F).effect(speedSupplier, 1).effect(jumpBoostSupplier, 1).effect(nauseaSupplier, 1).build()));
		String name = "magenta_bread";
		CRItems.toRegister.put(name, this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public boolean isFoil(ItemStack stack){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(Component.translatable("tt.crossroads.mag_bread.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
