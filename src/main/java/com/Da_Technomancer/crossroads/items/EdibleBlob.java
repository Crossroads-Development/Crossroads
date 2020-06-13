package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.*;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class EdibleBlob extends Item{

	public EdibleBlob(){
		super(new Item.Properties());//Not in a creative tab due to creative giving a version that has no NBT
		String name = "edible_blob";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	public static CompoundNBT createNBT(@Nullable CompoundNBT base, int hunger, int sat){
		if(base == null){
			base = new CompoundNBT();
		}
		base.putInt("food", hunger);
		base.putInt("sat", sat);
		return base;
	}

	public static int getHealAmount(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("food") : 0;
	}

	/**
	 * @param stack The edible blob stack
	 * @return The actual saturation restored
	 */
	public static int getTrueSat(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("sat") : 0;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		if(stack.hasTag()){
			tooltip.add(new TranslationTextComponent("tt.crossroads.edible_blob.food", getHealAmount(stack)));
			tooltip.add(new TranslationTextComponent("tt.crossroads.edible_blob.sat", getTrueSat(stack)));
			tooltip.add(new TranslationTextComponent("tt.crossroads.edible_blob.quip").setStyle(MiscUtil.TT_QUIP));
		}else{
			tooltip.add(new TranslationTextComponent("tt.crossroads.edible_blob.error"));
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public UseAction getUseAction(ItemStack stack){
		return UseAction.EAT;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	@Override
	public int getUseDuration(ItemStack stack){
		return 32;
	}

	/**
	 * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
	 * {@link #onItemUse}.
	 */
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn){
		ItemStack itemstack = playerIn.getHeldItem(handIn);
		if(playerIn.canEat(false)){
			playerIn.setActiveHand(handIn);
			return new ActionResult<>(ActionResultType.SUCCESS, itemstack);
		}else{
			return new ActionResult<>(ActionResultType.FAIL, itemstack);
		}
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving){
//		return this.isFood() ? entityLiving.onFoodEaten(worldIn, stack) : stack;
		if(entityLiving instanceof PlayerEntity){
			FoodStats stats = ((PlayerEntity) entityLiving).getFoodStats();

			MiscUtil.setPlayerFood((PlayerEntity) entityLiving, stats.getFoodLevel() + getHealAmount(stack), stats.getSaturationLevel() + getTrueSat(stack));

			((PlayerEntity) entityLiving).addStat(Stats.ITEM_USED.get(this));
			worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			if(entityLiving instanceof ServerPlayerEntity){
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity) entityLiving, stack);
			}
		}

		worldIn.playSound(null, entityLiving.posX, entityLiving.posY, entityLiving.posZ, entityLiving.getEatSound(stack), SoundCategory.NEUTRAL, 1.0F, 1.0F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.4F);
		stack.shrink(1);

		return stack;
	}
}
