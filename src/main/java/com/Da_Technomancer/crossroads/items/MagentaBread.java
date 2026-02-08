package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.api.MiscUtil;
import com.Da_Technomancer.crossroads.blocks.rotary.WindTurbineTileEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class MagentaBread extends Item{

	private static final Supplier<MobEffectInstance> jumpBoostSupplier = () -> new MobEffectInstance(MobEffects.JUMP, 3600, 20);
	private static final Supplier<MobEffectInstance> speedSupplier = () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 100);
	private static final Supplier<MobEffectInstance> nauseaSupplier = () -> new MobEffectInstance(MobEffects.CONFUSION, 3600, 10);
	private static final Supplier<MobEffectInstance> heroSupplier = () -> new MobEffectInstance(MobEffects.HERO_OF_THE_VILLAGE, 3600, 1);
	private static final Supplier<MobEffectInstance> speedLessSupplier = () -> new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 3600, 10);
	private static final Supplier<MobEffectInstance> poisonSupplier = () -> new MobEffectInstance(MobEffects.POISON, 10, 4);
	private static final Supplier<MobEffectInstance> strengthSupplier = () -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 10, 5);

	private final FoodProperties murderEasterEggFoodProperties;

	protected MagentaBread(){
		super(new Properties().rarity(CRItems.BOBO_RARITY).food(new FoodProperties.Builder().alwaysEdible().nutrition(20).saturationModifier(0.5F).effect(speedSupplier, 1).effect(jumpBoostSupplier, 1).effect(nauseaSupplier, 1).build()));
		murderEasterEggFoodProperties = new FoodProperties.Builder().alwaysEdible().nutrition(20).saturationModifier(.5F).effect(heroSupplier, 1).effect(speedLessSupplier, 1).effect(poisonSupplier, 1).effect(strengthSupplier, 1).build();
		String name = "magenta_bread";
		CRItems.queueForRegister(name, this);
	}

	@Override
	public boolean isFoil(ItemStack stack){
		return true;
	}

	@Override
	public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.mag_bread.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public @Nullable FoodProperties getFoodProperties(ItemStack stack, @Nullable LivingEntity entity){
		return entity instanceof Player player && WindTurbineTileEntity.murderEasterEgg.equals((player).getGameProfile().getName()) ? murderEasterEggFoodProperties : super.getFoodProperties(stack, entity);
	}
}
