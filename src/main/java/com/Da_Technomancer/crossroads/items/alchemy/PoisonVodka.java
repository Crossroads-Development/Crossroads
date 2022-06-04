package com.Da_Technomancer.crossroads.items.alchemy;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class PoisonVodka extends Item{

	private static final int BASE_DURATION = 20 * 60 * 3;
	private static final int BASE_DAMAGE = 8;
	private static final DamageSource VODKA_DAMAGE = new DamageSource("vodka");

	public PoisonVodka(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS));
		String name = "poison_vodka";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public int getUseDuration(ItemStack stack){
		return 32;
	}

	@Override
	public UseAnim getUseAnimation(ItemStack stack){
		return UseAnim.DRINK;
	}

	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		playerIn.startUsingItem(handIn);
		return new InteractionResultHolder<>(InteractionResult.SUCCESS, playerIn.getItemInHand(handIn));
	}

	public static void applyToEntity(Level world, LivingEntity target, LivingEntity source, float multiplier){
		if(!world.isClientSide){
			if(target == source || source == null){
				target.hurt(VODKA_DAMAGE, BASE_DAMAGE * multiplier);
			}else{
				target.hurt(new EntityDamageSource("vodka", source), BASE_DAMAGE * multiplier);
			}

			int duration = (int) (BASE_DURATION * multiplier);
			target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 0));
			target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 3));
			target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 2));
			target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, duration, 2));
		}
	}

	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving){

		applyToEntity(worldIn, entityLiving, entityLiving, 1F);

		if(entityLiving instanceof Player player){
			player.awardStat(Stats.ITEM_USED.get(this));

			if(!player.isCreative()){
				stack.shrink(1);
				if(stack.isEmpty()){
					return new ItemStack(Items.GLASS_BOTTLE);
				}else{
					if(!player.getInventory().add(new ItemStack(Items.GLASS_BOTTLE))){
						player.drop(new ItemStack(Items.GLASS_BOTTLE), false);
					}
				}
			}
		}else{
			stack.shrink(1);
			if(stack.isEmpty()){
				return new ItemStack(Items.GLASS_BOTTLE);
			}
		}

		return stack;
	}

	@Override
	public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType){
		return 72000;
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.poison_vodka.info"));
		tooltip.add(new TranslatableComponent("tt.crossroads.poison_vodka.quip").setStyle(MiscUtil.TT_QUIP));
	}
}
