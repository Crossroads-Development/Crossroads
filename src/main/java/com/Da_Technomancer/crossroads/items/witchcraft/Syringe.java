package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class Syringe extends Item{

	public Syringe(){
		super(new Item.Properties().stacksTo(1).tab(CRItems.TAB_CROSSROADS));
		String name = "syringe";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public boolean isTreated(ItemStack stack){
		return stack.getOrCreateTag().getBoolean("extension_treated");
	}

	private DamageSource syringeAttackSource(Player player){
		return new EntityDamageSource("cr_syringe", player);
	}

	private InteractionResult interact(ItemStack stack, Player self, LivingEntity target){
		ItemStack offhand = self.getItemInHand(InteractionHand.OFF_HAND);
		if(!canActivateWith(stack, offhand)){
			return InteractionResult.FAIL;
		}
		//Apply the potion extension into the syringe
		if(offhand.getItem() == CRItems.potionExtension && !CRItems.potionExtension.isSpoiled(offhand, self.level)){
			offhand.shrink(1);
			stack.getOrCreateTag().putBoolean("extension_treated", true);
			self.setItemInHand(InteractionHand.MAIN_HAND, stack);
			return InteractionResult.SUCCESS;
		}
		//Take a blood sample
		if(offhand.getItem() == CRItems.bloodSampleEmpty){
			self.setItemInHand(InteractionHand.OFF_HAND, CRItems.bloodSample.withEntityData(CRItems.bloodSample.setSpoilTime(new ItemStack(CRItems.bloodSample, 1), CRItems.bloodSample.getLifetime(), self.level.getGameTime()), target));
			target.hurt(syringeAttackSource(self), 1);
			return InteractionResult.SUCCESS;
		}
		//Apply an injection
		Potion potion = PotionUtils.getPotion(offhand);
		if(potion != Potions.EMPTY){
			boolean treated = isTreated(stack);
			//Use up any treatment on the syringe
			stack.getOrCreateTag().putBoolean("extension_treated", false);
			self.setItemInHand(InteractionHand.MAIN_HAND, stack);
			self.setItemInHand(InteractionHand.OFF_HAND, new ItemStack(Items.GLASS_BOTTLE, 1));
			double multiplier = CRConfig.injectionEfficiency.get();
			if(treated){
				//The syringe has a treatment applied; applied effects are more powerful/longer lasting
				int penalty = CRConfig.injectionPermaPenalty.get();
				for(MobEffectInstance effect : potion.getEffects()){
					if(effect.getEffect().isInstantenous()){
						//Multiply intensity
						target.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), (int) Math.round(effect.getAmplifier() * multiplier), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}else if(CRPotions.applyAsPermanent(target, effect) && penalty > 0){
						//Make permanent, apply a penalty
						MobEffectInstance penaltyEffect = target.getEffect(CRPotions.HEALTH_PENALTY_EFFECT);
						int prevPenaltyIntensity = penaltyEffect != null ? penaltyEffect.getAmplifier() : -1;
						penaltyEffect = new MobEffectInstance(CRPotions.HEALTH_PENALTY_EFFECT, Integer.MAX_VALUE, (penalty - 1) + (prevPenaltyIntensity + 1));
						CRPotions.applyAsPermanent(target, penaltyEffect);
					}
				}
			}else{
				for(MobEffectInstance effect : potion.getEffects()){
					if(effect.getEffect().isInstantenous()){
						//Multiply intensity
						effect.getEffect().applyInstantenousEffect(self, self, target, (int) Math.round(effect.getAmplifier() * multiplier), 1);
//						target.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), (int) Math.round(effect.getAmplifier() * multiplier), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}else{
						//Multiply duration
						target.addEffect(new MobEffectInstance(effect.getEffect(), (int) Math.round(effect.getDuration() * multiplier), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}
				}
			}
			target.hurt(syringeAttackSource(self), 1);
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
		//Only target others if sneaking
		if(player.isShiftKeyDown()){
			return interact(stack, player, target);
		}
		return InteractionResult.PASS;
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		//Only target self if not sneaking
		if(!player.isShiftKeyDown()){
			InteractionResult resultType = interact(stack, player, player);
			return new InteractionResultHolder<>(resultType, stack);
		}
		return InteractionResultHolder.pass(stack);
	}

	private boolean canActivateWith(ItemStack syringeStack, ItemStack offhandStack){
		if(syringeStack.getItem() == this){
			if(offhandStack.getItem() == CRItems.bloodSampleEmpty || PotionUtils.getPotion(offhandStack) != Potions.EMPTY){
				return true;
			}
			return offhandStack.getItem() == CRItems.potionExtension && !isTreated(syringeStack);
		}
		return false;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(new TranslatableComponent("tt.crossroads.syringe.desc", CRConfig.injectionEfficiency.get()));
		tooltip.add(new TranslatableComponent("tt.crossroads.syringe.use"));
		tooltip.add(new TranslatableComponent("tt.crossroads.syringe.offhand"));
		tooltip.add(new TranslatableComponent("tt.crossroads.syringe.perm"));
	}
}
