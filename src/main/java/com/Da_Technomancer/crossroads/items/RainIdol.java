package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.ModConfig;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class RainIdol extends Item{


	protected RainIdol(){
		String name = "rain_idol";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
		maxStackSize = 1;
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(worldIn.isRemote){
			return;
		}

		if(isSelected && entityIn instanceof EntityPlayer){
			EntityPlayer play = (EntityPlayer) entityIn;
			if(!ModConfig.weatherControl.getBoolean()){
				play.sendMessage(new TextComponentString("This item was disabled in the config."));
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
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 20F, 1F);
						play.setHeldItem(EnumHand.MAIN_HAND, null);
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
						worldIn.playSound(null, play.posX, play.posY, play.posZ, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.PLAYERS, 3F, 1F);
						play.setHeldItem(EnumHand.MAIN_HAND, null);
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
