package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class RainIdol extends Item{

	protected RainIdol(){
		String name = "rain_idol";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(ModItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		ModItems.toRegister.add(this);
		ModItems.itemAddQue(this);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		if(!ModConfig.getConfigBool(ModConfig.weatherControl, true)){
			tooltip.add("This item has been disabled in the config. It does nothing.");
		}
		tooltip.add("Have you heard the word of our lord and saviour, this blue rock with rain powers?");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(worldIn.isRemote){
			return;
		}

		if(isSelected && entityIn instanceof EntityPlayer){
			EntityPlayer play = (EntityPlayer) entityIn;
			if(!ModConfig.getConfigBool(ModConfig.weatherControl, false)){
				return;
			}

			if(!play.getEntityData().hasKey("rIdol")){
				play.getEntityData().setByte("rIdol", (byte) 0);
				play.getEntityData().setLong("rIdolTime", System.currentTimeMillis());
			}

			if(System.currentTimeMillis() - play.getEntityData().getLong("rIdolTime") > 2_000){
				play.getEntityData().setByte("rIdol", (byte) 0);
				play.getEntityData().setLong("rIdolTime", System.currentTimeMillis());
			}
			byte count = play.getEntityData().getByte("rIdol");

			if(!play.isSneaking() && play.motionY <= 0){
				if(Math.abs(count) % 2 == 1){
					count += count > 0 ? 1 : -1;
					play.getEntityData().setByte("rIdol", count);
				}
				return;
			}

			if(play.isSneaking()){
				if(count < 0){
					count = 0;
				}
				if(count % 2 == 0){
					if(++count >= 9){
						worldIn.getWorldInfo().setRaining(true);
						worldIn.getWorldInfo().setThundering(true);
						worldIn.getWorldInfo().setCleanWeatherTime(0);
						worldIn.getWorldInfo().setRainTime(24000);
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1F, 1F);
						play.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getEntityData().setLong("rIdolTime", System.currentTimeMillis());
					}
					play.getEntityData().setByte("rIdol", count);
				}
			}else{
				if(count > 0){
					count = 0;
				}
				if(count % 2 == 0){
					if(--count <= -9){
						worldIn.getWorldInfo().setRaining(false);
						worldIn.getWorldInfo().setCleanWeatherTime(24000);
						worldIn.getWorldInfo().setRainTime(0);
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1F, 1F);
						play.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getEntityData().setLong("rIdolTime", System.currentTimeMillis());
					}
					play.getEntityData().setByte("rIdol", count);
				}
			}	
		}
	}
}	
