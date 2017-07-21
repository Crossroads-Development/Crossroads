package com.Da_Technomancer.crossroads.items;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendMagicItemToServer;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class MagicUsingItem extends Item{
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		if(player.world.isRemote && getMaxItemUseDuration(stack) == count){
			MagicElements elemChanged = null;
			if(Keys.controlEnergy.isKeyDown()){
				elemChanged = MagicElements.ENERGY;
			}else if(Keys.controlPotential.isKeyDown()){
				elemChanged = MagicElements.POTENTIAL;
			}else if(Keys.controlStability.isKeyDown()){
				elemChanged = MagicElements.STABILITY;
			}else if(Keys.controlVoid.isKeyDown()){
				elemChanged = MagicElements.VOID;
			}
			if(elemChanged != null && player instanceof EntityPlayer){
				player.world.playSound((EntityPlayer) player, player.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 5, (float) Math.random());
				ModPackets.network.sendToServer(new SendMagicItemToServer(elemChanged.name(), player.isSneaking()));
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		//In this case, any number much greater than 5 works. 72000 is used in vanilla code, so it's used here for consistency.
		return 72000;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag advanced){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			nbt = new NBTTagCompound();
		}
		tooltip.add("Energy usage: " + nbt.getInteger(MagicElements.ENERGY.name()));
		tooltip.add("Potential usage: " + nbt.getInteger(MagicElements.POTENTIAL.name()));
		tooltip.add("Stability usage: " + nbt.getInteger(MagicElements.STABILITY.name()));
		tooltip.add("Void usage: " + nbt.getInteger(MagicElements.VOID.name()));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	/**
	 * Called BEFORE this item's nbt is changed via packet for the magic use settings.
	 */
	public abstract void preChanged(ItemStack stack, EntityPlayer player);
}
