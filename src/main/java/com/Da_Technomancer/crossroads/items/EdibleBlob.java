package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EdibleBlob extends Item{

	public EdibleBlob(){
		super(0, 0, true);
		String name = "edible_blob";
		setTranslationKey(name);
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	/**
	 * This is not in a creative tab due to creative giving a version that has no NBT
	 */
	@Override
	protected boolean isInCreativeTab(ItemGroup targetTab){
		return false;
	}

	@Override
	public int getHealAmount(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("food") : 0;
	}

	/**
	 * Returns (saturation restored / (2 * hunger restored)),
	 * because vanilla is weird.
	 */
	@Override
	public float getSaturationModifier(ItemStack stack){
		int hun = getHealAmount(stack);
		int sat = getTrueSat(stack);
		return hun != 0 && sat != 0 ? 0.5F * sat / hun : 0;
	}

	/**
	 * @param stack The edible blob stack
	 * @return The actual saturation restored
	 */
	private int getTrueSat(ItemStack stack){
		return stack.hasTag() ? stack.getTag().getInt("sat") : 0;
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving){
		if(entityLiving instanceof PlayerEntity){
			PlayerEntity entityplayer = (PlayerEntity) entityLiving;
			FoodStats food = entityplayer.getFoodStats();
			// The way saturation is coded is weird, and the best way to do this is through nbt.
			CompoundNBT nbt = new CompoundNBT();
			food.writeNBT(nbt);
			nbt.putInt("foodLevel", Math.min(getHealAmount(stack) + food.getFoodLevel(), 20));
			nbt.putFloat("foodSaturationLevel", Math.min(20F, food.getSaturationLevel() + getTrueSat(stack)));
			food.readNBT(nbt);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			entityplayer.addStat(Stats.getObjectUseStats(this));
		}

		stack.shrink(1);
		return stack;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		if(stack.hasTag()){
			tooltip.add("Food value: " + getHealAmount(stack));
			tooltip.add("Saturation value: " + getTrueSat(stack));
		}else{
			tooltip.add("Error");
		}
		tooltip.add("Just like mama used to make.");
	}
}
