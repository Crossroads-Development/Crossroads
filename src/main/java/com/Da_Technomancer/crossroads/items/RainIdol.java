package com.Da_Technomancer.crossroads.items;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class RainIdol extends Item{

	private static final String NBT_KEY = "rain_idol";
	private static final String NBT_KEY_TIME = "rain_idol_time";
	
	protected RainIdol(){
		String name = "rain_idol";
		setTranslationKey(name);
		setRegistryName(name);
		setCreativeTab(CrossroadsItems.TAB_CROSSROADS);
		setMaxStackSize(1);
		CrossroadsItems.toRegister.add(this);
		CrossroadsItems.itemAddQue(this);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack stack, @Nullable IBlockReader world, List<ITextComponent> tooltip, ITooltipFlag advanced){
		tooltip.add("Have you heard the word of our lord and saviour, this blue rock with rain powers?");
	}

	@Override
	public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected){
		if(worldIn.isRemote){
			return;
		}

		if(isSelected && entityIn instanceof PlayerEntity){
			PlayerEntity play = (PlayerEntity) entityIn;
			if(!play.getEntityData().contains(NBT_KEY)){
				play.getEntityData().putByte(NBT_KEY, (byte) 0);
				play.getEntityData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}

			if(System.currentTimeMillis() - play.getEntityData().getLong(NBT_KEY_TIME) > 2_000){
				play.getEntityData().putByte(NBT_KEY, (byte) 0);
				play.getEntityData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
			}
			byte count = play.getEntityData().getByte(NBT_KEY);

			if(!play.isSneaking() && play.motionY <= 0){
				if(Math.abs(count) % 2 == 1){
					count += count > 0 ? 1 : -1;
					play.getEntityData().putByte(NBT_KEY, count);
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
						play.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getEntityData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
					}
					play.getEntityData().putByte(NBT_KEY, count);
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
						play.setHeldItem(Hand.MAIN_HAND, ItemStack.EMPTY);
						count = 0;
					}else{
						play.getEntityData().putLong(NBT_KEY_TIME, System.currentTimeMillis());
					}
					play.getEntityData().putByte(NBT_KEY, count);
				}
			}	
		}
	}
}	
