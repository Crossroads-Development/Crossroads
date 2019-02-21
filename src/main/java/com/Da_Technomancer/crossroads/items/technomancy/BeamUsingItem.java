package com.Da_Technomancer.crossroads.items.technomancy;

import java.util.List;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendBeamItemToServer;

import net.minecraft.client.Minecraft;
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

public abstract class BeamUsingItem extends Item{
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		if(player.world.isRemote && player == Minecraft.getMinecraft().player && getMaxItemUseDuration(stack) == count){
			EnumBeamAlignments elemChanged = null;
			if(Keys.controlEnergy.isKeyDown()){
				elemChanged = EnumBeamAlignments.ENERGY;
			}else if(Keys.controlPotential.isKeyDown()){
				elemChanged = EnumBeamAlignments.POTENTIAL;
			}else if(Keys.controlStability.isKeyDown()){
				elemChanged = EnumBeamAlignments.STABILITY;
			}else if(Keys.controlVoid.isKeyDown()){
				elemChanged = EnumBeamAlignments.VOID;
			}
			if(elemChanged != null && player instanceof EntityPlayer){
				player.world.playSound((EntityPlayer) player, player.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 5, (float) Math.random());
				ModPackets.network.sendToServer(new SendBeamItemToServer(elemChanged.name(), player.isSneaking()));
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
		tooltip.add("Energy usage: " + nbt.getInteger(EnumBeamAlignments.ENERGY.name()));
		tooltip.add("Potential usage: " + nbt.getInteger(EnumBeamAlignments.POTENTIAL.name()));
		tooltip.add("Stability usage: " + nbt.getInteger(EnumBeamAlignments.STABILITY.name()));
		tooltip.add("Void usage: " + nbt.getInteger(EnumBeamAlignments.VOID.name()));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand){
		playerIn.setActiveHand(hand);
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, playerIn.getHeldItem(hand));
	}
	
	/**
	 * Called BEFORE this item's nbt is changed via packet for the beams use settings.
	 */
	public abstract void preChanged(ItemStack stack, EntityPlayer player);
}
