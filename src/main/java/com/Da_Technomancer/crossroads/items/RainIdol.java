package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Rarity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class RainIdol extends Item{

	private static final String NBT_KEY = "rain_idol";
	private static final String NBT_KEY_TIME = "rain_idol_time";
	
	protected RainIdol(){
		super(CRItems.itemProp.maxStackSize(1));
		String name = "rain_idol";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn){
		tooltip.add(new TranslationTextComponent("tt.crossroads.rain_idol.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(worldIn.isRemote){
			return;
		}

		//Start rain if the player sneaks 5 times in a few seconds
		//Ends rain if the player jumps 5 times in a few seconds

		if(isSelected && entityIn instanceof PlayerEntity){
			PlayerEntity play = (PlayerEntity) entityIn;
			if(!play.getPersistentData().contains(NBT_KEY)){
				play.getPersistentData().putByte(NBT_KEY, (byte) 0);
				play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}

			if(System.currentTimeMillis() - play.getPersistentData().getLong(NBT_KEY_TIME) > 2_000){
				play.getPersistentData().putByte(NBT_KEY, (byte) 0);
				play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}
			byte count = play.getPersistentData().getByte(NBT_KEY);

			if(!play.isSneaking() && play.getMotion().y <= 0){
				if(Math.abs(count) % 2 == 1){
					count += count > 0 ? 1 : -1;
					play.getPersistentData().putByte(NBT_KEY, count);
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
						worldIn.getWorldInfo().setClearWeatherTime(0);
						worldIn.getWorldInfo().setRainTime(24000);
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1F, 1F);
						play.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
					}
					play.getPersistentData().putByte(NBT_KEY, count);
				}
			}else{
				if(count > 0){
					count = 0;
				}
				if(count % 2 == 0){
					if(--count <= -9){
						worldIn.getWorldInfo().setRaining(false);
						worldIn.getWorldInfo().setClearWeatherTime(24000);
						worldIn.getWorldInfo().setRainTime(0);
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1F, 1F);
						play.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
					}
					play.getPersistentData().putByte(NBT_KEY, count);
				}
			}
		}
	}
}	
