package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatList;
import net.minecraft.util.FoodStats;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EdibleBlob extends ItemFood{

	public EdibleBlob(){
		super(0, 0, true);
		String name = "edible_blob";
		setUnlocalizedName(name);
		setRegistryName(name);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	/**
	 * This is not in a creative tab due to creative giving a version that has no NBT
	 */
	@Override
	protected boolean isInCreativeTab(CreativeTabs targetTab){
		return false;
	}

	@Override
	public int getHealAmount(ItemStack stack){
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("food") : 0;
	}

	/** Normally, this would return a float equal to (saturation restored / (2 * hunger restored)), but due to the fact that this item can sometimes have a hunger value of 0 (divide by 0 error),
	 * this is instead being used to return (saturation restored).
	 * This can mess with other mods (appleskin might show the wrong saturation value for example), but nothing too serious and this is probably the best way of doing this. */
	@Override
	public float getSaturationModifier(ItemStack stack){
		return stack.hasTagCompound() ? stack.getTagCompound().getInteger("sat") : 0;
	}

	@Override
	@Nullable
	public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving){
		if(entityLiving instanceof EntityPlayer){
			EntityPlayer entityplayer = (EntityPlayer) entityLiving;
			FoodStats food = entityplayer.getFoodStats();
			// The way saturation is coded is weird, and the best way to do this is through nbt.
			NBTTagCompound nbt = new NBTTagCompound();
			food.writeNBT(nbt);
			nbt.setInteger("foodLevel", Math.min(getHealAmount(stack) + food.getFoodLevel(), 20));
			nbt.setFloat("foodSaturationLevel", Math.min(20F, food.getSaturationLevel() + getSaturationModifier(stack)));
			food.readNBT(nbt);
			worldIn.playSound(null, entityplayer.posX, entityplayer.posY, entityplayer.posZ, SoundEvents.ENTITY_PLAYER_BURP, SoundCategory.PLAYERS, 0.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
			entityplayer.addStat(StatList.getObjectUseStats(this));
		}

		stack.shrink(1);
		return stack;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		tooltip.add(stack.hasTagCompound() ? "Food value: " + stack.getTagCompound().getInteger("food") : "ERROR");
		tooltip.add(stack.hasTagCompound() ? "Saturation value: " + stack.getTagCompound().getInteger("sat") : "ERROR");
		tooltip.add("Just like mama used to make.");
	}
}
