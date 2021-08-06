package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtils;
import net.minecraft.potion.Potions;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

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

	private DamageSource syringeAttackSource(PlayerEntity player){
		return new EntityDamageSource("cr_syringe", player);
	}

	private ActionResultType interact(ItemStack stack, PlayerEntity self, LivingEntity target){
		ItemStack offhand = self.getItemInHand(Hand.OFF_HAND);
		if(!canActivateWith(stack, offhand)){
			return ActionResultType.FAIL;
		}
		//Apply the potion extension into the syringe
		if(offhand.getItem() == CRItems.potionExtension && !CRItems.potionExtension.isSpoiled(offhand, self.level)){
			offhand.shrink(1);
			stack.getOrCreateTag().putBoolean("extension_treated", true);
			return ActionResultType.SUCCESS;
		}
		//Take a blood sample
		if(offhand.getItem() == CRItems.bloodSampleEmpty){
			self.setItemInHand(Hand.OFF_HAND, CRItems.bloodSample.withEntityData(CRItems.bloodSample.setSpoilTime(new ItemStack(CRItems.bloodSample, 1), CRItems.bloodSample.getLifetime(), self.level.getGameTime()), target));
			target.hurt(syringeAttackSource(self), 1);
			return ActionResultType.SUCCESS;
		}
		//Apply an injection
		Potion potion = PotionUtils.getPotion(offhand);
		if(potion != Potions.EMPTY){
			boolean treated = isTreated(stack);
			//Use up any treatment on the syringe
			stack.getOrCreateTag().putBoolean("extension_treated", false);
			self.setItemInHand(Hand.OFF_HAND, new ItemStack(Items.GLASS_BOTTLE, 1));
			double multiplier = CRConfig.injectionEfficiency.get();
			if(treated){
				//The syringe has a treatment applied; applied effects are more powerful/longer lasting
				int penalty = CRConfig.injectionPermaPenalty.get();
				for(EffectInstance effect : potion.getEffects()){
					if(effect.getEffect().isInstantenous()){
						//Multiply intensity
						target.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration(), (int) Math.round(effect.getAmplifier() * multiplier), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}else if(CRPotions.applyAsPermanent(target, effect) && penalty > 0){
						//Make permanent, apply a penalty
						EffectInstance penaltyEffect = target.getEffect(CRPotions.HEALTH_PENALTY_EFFECT);
						int prevPenaltyIntensity = penaltyEffect != null ? penaltyEffect.getAmplifier() : -1;
						penaltyEffect = new EffectInstance(CRPotions.HEALTH_PENALTY_EFFECT, Integer.MAX_VALUE, (penalty - 1) + (prevPenaltyIntensity + 1));
						CRPotions.applyAsPermanent(target, penaltyEffect);
					}
				}
			}else{
				for(EffectInstance effect : potion.getEffects()){
					if(effect.getEffect().isInstantenous()){
						//Multiply intensity
						target.addEffect(new EffectInstance(effect.getEffect(), effect.getDuration(), (int) Math.round(effect.getAmplifier() * multiplier), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}else{
						//Multiply duration
						target.addEffect(new EffectInstance(effect.getEffect(), (int) Math.round(effect.getDuration() * multiplier), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
					}
				}
			}
			target.hurt(syringeAttackSource(self), 1);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	@Override
	public ActionResultType interactLivingEntity(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand){
		//Only target others if not sneaking
		if(!player.isShiftKeyDown()){
			return interact(stack, player, target);
		}
		return ActionResultType.PASS;
	}

	@Override
	public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand){
		ItemStack stack = player.getItemInHand(hand);
		ActionResultType resultType = interact(stack, player, player);
		return new ActionResult<>(resultType, stack);
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
	public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag){
		tooltip.add(new TranslationTextComponent("tt.crossroads.syringe.desc", CRConfig.injectionEfficiency.get()));
		tooltip.add(new TranslationTextComponent("tt.crossroads.syringe.use"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.syringe.offhand"));
		tooltip.add(new TranslationTextComponent("tt.crossroads.syringe.perm"));
	}
}
