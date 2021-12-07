package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
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

	public static CompoundTag createNBT(@Nullable CompoundTag base, int hunger, int sat){
		if(base == null){
			base = new CompoundTag();
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
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		if(stack.hasTag()){
			tooltip.add(new TranslatableComponent("tt.crossroads.edible_blob.food", getHealAmount(stack)));
			tooltip.add(new TranslatableComponent("tt.crossroads.edible_blob.sat", getTrueSat(stack)));
			tooltip.add(new TranslatableComponent("tt.crossroads.edible_blob.quip").setStyle(MiscUtil.TT_QUIP));
		}else{
			tooltip.add(new TranslatableComponent("tt.crossroads.edible_blob.error"));
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	@Override
	public UseAnim getUseAnimation(ItemStack stack){
		return UseAnim.EAT;
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
	public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn){
		ItemStack itemstack = playerIn.getItemInHand(handIn);
		if(playerIn.canEat(false)){
			playerIn.startUsingItem(handIn);
			return new InteractionResultHolder<>(InteractionResult.SUCCESS, itemstack);
		}else{
			return new InteractionResultHolder<>(InteractionResult.FAIL, itemstack);
		}
	}

	/**
	 * Called when the player finishes using this Item (E.g. finishes eating.). Not called when the player stops using
	 * the Item before the action is complete.
	 */
	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving){
//		return this.isFood() ? entityLiving.onFoodEaten(worldIn, stack) : stack;
		if(entityLiving instanceof Player){
			FoodData stats = ((Player) entityLiving).getFoodData();

			MiscUtil.setPlayerFood((Player) entityLiving, stats.getFoodLevel() + getHealAmount(stack), stats.getSaturationLevel() + getTrueSat(stack));

			((Player) entityLiving).awardStat(Stats.ITEM_USED.get(this));
			worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), SoundEvents.PLAYER_BURP, SoundSource.PLAYERS, 0.5F, worldIn.random.nextFloat() * 0.1F + 0.9F);
			if(entityLiving instanceof ServerPlayer){
				CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer) entityLiving, stack);
			}
		}

		worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), entityLiving.getEatingSound(stack), SoundSource.NEUTRAL, 1.0F, 1.0F + (worldIn.random.nextFloat() - worldIn.random.nextFloat()) * 0.4F);
		stack.shrink(1);

		return stack;
	}
}
