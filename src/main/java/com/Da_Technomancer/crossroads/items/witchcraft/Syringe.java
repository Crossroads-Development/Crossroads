package com.Da_Technomancer.crossroads.items.witchcraft;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.witchcraft.IPerishable;
import com.Da_Technomancer.crossroads.entity.CRMobDamage;
import com.Da_Technomancer.crossroads.entity.mob_effects.CRPotions;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.alchemy.PoisonVodka;
import com.Da_Technomancer.essentials.api.BlockUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;

public class Syringe extends Item{

	public static final HashMap<Item, SyringeExtension> SYRINGE_ITEM_EFFECTS = new HashMap<>(6);

	private static void populateSyringeMap(){
		//Be careful about when this is called, that the item references are initialized beforehand
		SYRINGE_ITEM_EFFECTS.put(CRItems.potionExtension, (item, syringe, world, target, user) -> {
			if(!isTreated(syringe) && !IPerishable.isSpoiled(item, world)){
				setTreated(syringe, true);
				return InteractionResultHolder.success(ItemStack.EMPTY);
			}
			return InteractionResultHolder.fail(item);
		});
		SYRINGE_ITEM_EFFECTS.put(CRItems.bloodSampleEmpty, (item, syringe, world, target, user) -> {
			if(target != null){
				ItemStack drawn = drawBlood(item, target, user);
				return InteractionResultHolder.success(drawn);
			}
			return InteractionResultHolder.fail(item);
		});
		SYRINGE_ITEM_EFFECTS.put(CRItems.poisonVodka, (item, syringe, world, target, user) -> {
			if(target != null){
				PoisonVodka.applyToEntity(world, target, user, CRConfig.injectionEfficiency.get().floatValue());
				return InteractionResultHolder.success(new ItemStack(Items.GLASS_BOTTLE));
			}
			return InteractionResultHolder.fail(item);
		});
		SyringeExtension potionResult = (item, syringe, world, target, user) -> {
			Potion potion = PotionUtils.getPotion(item);
			if(potion != Potions.EMPTY && target != null){
				boolean treated = isTreated(syringe);
				//Use up any treatment on the syringe
				setTreated(syringe, false);
				item = new ItemStack(Items.GLASS_BOTTLE);
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
							effect.getEffect().applyInstantenousEffect(user, user, target, (int) Math.round(effect.getAmplifier() * multiplier), 1);
//						target.addEffect(new MobEffectInstance(effect.getEffect(), effect.getDuration(), (int) Math.round(effect.getAmplifier() * multiplier), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
						}else{
							//Multiply duration
							target.addEffect(new MobEffectInstance(effect.getEffect(), (int) Math.round(effect.getDuration() * multiplier), effect.getAmplifier(), effect.isAmbient(), effect.isVisible(), effect.showIcon()));
						}
					}
				}
				target.hurt(CRMobDamage.damageSource(CRMobDamage.CR_SYRINGE, world, user), 1);
				return InteractionResultHolder.success(item);
			}
			return InteractionResultHolder.fail(item);
		};
		SYRINGE_ITEM_EFFECTS.put(Items.POTION, potionResult);
		SYRINGE_ITEM_EFFECTS.put(Items.SPLASH_POTION, potionResult);
		SYRINGE_ITEM_EFFECTS.put(Items.LINGERING_POTION, potionResult);
	}

	public Syringe(){
		super(new Item.Properties().stacksTo(1));
		String name = "syringe";
		CRItems.queueForRegister(name, this);
		populateSyringeMap();
	}

	public static boolean isTreated(ItemStack stack){
		return stack.getOrCreateTag().getBoolean("extension_treated");
	}

	public static void setTreated(ItemStack stack, boolean treated){
		stack.getOrCreateTag().putBoolean("extension_treated", treated);
	}

	private InteractionResult interact(ItemStack stack, Player self, LivingEntity target){
		ItemStack offhand = self.getItemInHand(InteractionHand.OFF_HAND);
		ItemStack offhandCopy = offhand.copy();

		SyringeExtension extension = SYRINGE_ITEM_EFFECTS.get(offhand.getItem());
		if(extension == null){
			return InteractionResult.FAIL;
		}

		ItemStack syringe = stack.copy();
		InteractionResultHolder<ItemStack> result = extension.applySyringe(offhandCopy, syringe, self.level(), target, self);
		offhandCopy = result.getObject();
		switch(result.getResult()){
			case SUCCESS:
				if(!BlockUtil.sameItem(syringe, stack)){
					self.setItemInHand(InteractionHand.MAIN_HAND, syringe);
				}
				if(!BlockUtil.sameItem(offhand, offhandCopy)){
					if(offhand.getCount() <= 1){
						self.setItemInHand(InteractionHand.OFF_HAND, offhandCopy);
					}else{
						offhand.shrink(1);
						if(!self.getInventory().add(offhandCopy)){
							self.drop(offhandCopy, false);
						}
					}
				}
				return InteractionResult.SUCCESS;
			case FAIL:
				return InteractionResult.FAIL;
			default:
				throw new IllegalArgumentException(String.format("Invalid return type %s from syringe extension for offhand %s", result.getResult(), offhand.getItem().toString()));
		}
	}

	/**
	 * Attempts to draw blood from an entity
	 * @param samplePouch The original empty pouch item. Will not be modified
	 * @param target The entity having its blood drawn
	 * @param attacker The player drawing blood. Can be null.
	 * @return The filled blood sample pouch
	 */
	public static ItemStack drawBlood(ItemStack samplePouch, LivingEntity target, @Nullable Player attacker){
		ItemStack result = CRItems.bloodSample.withEntityData(IPerishable.setSpoilTime(new ItemStack(CRItems.bloodSample, 1), CRItems.bloodSample.getLifetime(), target.level().getGameTime()), target);
		target.hurt(CRMobDamage.damageSource(CRMobDamage.CR_SYRINGE, target.level(), attacker), 1);
		return result;
	}

	@Override
	public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand){
		return interact(stack, player, target);
	}

	@Override
	public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand){
		ItemStack stack = player.getItemInHand(hand);
		InteractionResult resultType = interact(stack, player, player);
		return new InteractionResultHolder<>(resultType, stack);
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag){
		tooltip.add(Component.translatable("tt.crossroads.syringe.desc", CRConfig.injectionEfficiency.get()));
		tooltip.add(Component.translatable("tt.crossroads.syringe.use"));
		tooltip.add(Component.translatable("tt.crossroads.syringe.offhand"));
		tooltip.add(Component.translatable("tt.crossroads.syringe.perm"));
	}

	public static interface SyringeExtension{

		/**
		 * Attempts to apply this item with a syringe.
		 * InteractionResult should be FAIL if this did nothing, or SUCCESS if it did something. Other values will throw an error.
		 * The itemstack in the returned value is the end value of the offhand item.
		 * @param item This itemstack. Can be modified freely.
		 * @param syringe The itemstack with the syringe. Changes will write back to the calling item.
		 * @param world The world
		 * @param target The targeted entity. Might be the same as user.
		 * @param user The player using the syringe.
		 * @return A result holder with what the resulting value of the 'item' should be. Status should be FAIL or SUCCESS.
		 */
		@Nonnull
		InteractionResultHolder<ItemStack> applySyringe(@Nonnull ItemStack item, @Nonnull ItemStack syringe, @Nonnull Level world, @Nullable LivingEntity target, @Nullable Player user);
	}
}
