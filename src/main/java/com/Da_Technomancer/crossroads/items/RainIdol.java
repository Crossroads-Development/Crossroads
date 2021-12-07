package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ServerLevelData;

import javax.annotation.Nullable;
import java.util.List;

public class RainIdol extends Item{

	private static final String NBT_KEY = "rain_idol";
	private static final String NBT_KEY_TIME = "rain_idol_time";
	
	protected RainIdol(){
		super(new Properties().tab(CRItems.TAB_CROSSROADS).stacksTo(1));
		String name = "rain_idol";
		setRegistryName(name);
		CRItems.toRegister.add(this);
	}

	@Override
	public Rarity getRarity(ItemStack stack){
		return CRItems.BOBO_RARITY;
	}

	@Override
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn){
		tooltip.add(new TranslatableComponent("tt.crossroads.rain_idol.quip").setStyle(MiscUtil.TT_QUIP));
	}

	@Override
	public void inventoryTick(ItemStack stack, Level worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(worldIn.isClientSide){
			return;
		}

		//Start rain if the player sneaks 5 times in a few seconds
		//Ends rain if the player jumps 5 times in a few seconds

		if(isSelected && entityIn instanceof Player){
			Player play = (Player) entityIn;
			if(!play.getPersistentData().contains(NBT_KEY)){
				play.getPersistentData().putByte(NBT_KEY, (byte) 0);
				play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}

			if(System.currentTimeMillis() - play.getPersistentData().getLong(NBT_KEY_TIME) > 2_000){
				play.getPersistentData().putByte(NBT_KEY, (byte) 0);
				play.getPersistentData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}
			byte count = play.getPersistentData().getByte(NBT_KEY);

			if(!play.isShiftKeyDown() && play.getDeltaMovement().y <= 0){
				if(Math.abs(count) % 2 == 1){
					count += count > 0 ? 1 : -1;
					play.getPersistentData().putByte(NBT_KEY, count);
				}
				return;
			}

			if(play.isShiftKeyDown()){
				if(count < 0){
					count = 0;
				}
				if(count % 2 == 0){
					if(++count >= 9){
						ServerLevelData worldInfo = (ServerLevelData) worldIn.getLevelData();
						worldInfo.setRaining(true);
						worldInfo.setThundering(true);
						worldInfo.setRainTime(0);
						worldInfo.setRainTime(24000);
						worldIn.playSound(null, play.getX(), play.getY(), play.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1F, 1F);
						play.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
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
						ServerLevelData worldInfo = (ServerLevelData) worldIn.getLevelData();
						worldInfo.setRaining(false);
						worldInfo.setRainTime(24000);
						worldInfo.setRainTime(0);
						worldIn.playSound(null, play.getX(), play.getY(), play.getZ(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1F, 1F);
						play.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
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
