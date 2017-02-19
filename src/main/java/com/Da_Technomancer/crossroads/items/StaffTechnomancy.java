package com.Da_Technomancer.crossroads.items;

import java.util.List;

import com.Da_Technomancer.crossroads.Keys;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseBeamToClient;
import com.Da_Technomancer.crossroads.API.packets.SendStaffToServer;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StaffTechnomancy extends Item{

	public StaffTechnomancy(){
		String name = "staffTechnomancy";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		this.setCreativeTab(ModItems.tabCrossroads);
	}
	
	public static long lastRenderTick = 0;
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		if(player.worldObj.isRemote && getMaxItemUseDuration(stack) - count == 1){
			MagicElements elemChanged = null;
			if(Keys.staffEnergy.isKeyDown()){
				elemChanged = MagicElements.ENERGY;
			}else if(Keys.staffPotential.isKeyDown()){
				elemChanged = MagicElements.POTENTIAL;
			}else if(Keys.staffStability.isKeyDown()){
				elemChanged = MagicElements.STABILITY;
			}else if(Keys.staffVoid.isKeyDown()){
				elemChanged = MagicElements.VOID;
			}
			if(elemChanged != null && player instanceof EntityPlayer){
				player.worldObj.playSound((EntityPlayer) player, player.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 5, (float) Math.random());
				ModPackets.network.sendToServer(new SendStaffToServer(elemChanged.name(), player.isSneaking()));
				/*if(stack.getTagCompound() == null){
					stack.setTagCompound(new NBTTagCompound());
				}
				NBTTagCompound nbt = stack.getTagCompound();
				int i = nbt.getInteger(elemChanged.name());
				i += player.isSneaking() ? -1 : 1;
				i = Math.min(8, Math.max(i, 0));
				nbt.setInteger(elemChanged.name(), i);*/
				//return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
		}else if(!player.worldObj.isRemote && getMaxItemUseDuration(stack) - count >= 5 && count % 5 == 0){
			if(!stack.hasTagCompound()){
				return;
			}
			
			NBTTagCompound nbt = stack.getTagCompound();
			int energy = nbt.getInteger(MagicElements.ENERGY.name());
			int potential = nbt.getInteger(MagicElements.POTENTIAL.name());
			int stability = nbt.getInteger(MagicElements.STABILITY.name());
			int voi = nbt.getInteger(MagicElements.VOID.name());
			if(energy <= nbt.getInteger("stored_" + MagicElements.ENERGY.name()) && potential <= nbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) && stability <= nbt.getInteger("stored_" + MagicElements.STABILITY.name()) && voi <= nbt.getInteger("stored_" + MagicElements.VOID.name())){
				if(energy + potential + stability + voi > 0){
					nbt.setInteger("stored_" + MagicElements.ENERGY.name(), nbt.getInteger("stored_" + MagicElements.ENERGY.name()) - energy);
					nbt.setInteger("stored_" + MagicElements.POTENTIAL.name(), nbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) - potential);
					nbt.setInteger("stored_" + MagicElements.STABILITY.name(), nbt.getInteger("stored_" + MagicElements.STABILITY.name()) - stability);
					nbt.setInteger("stored_" + MagicElements.VOID.name(), nbt.getInteger("stored_" + MagicElements.VOID.name()) - voi);
					
					MagicUnit mag = new MagicUnit(energy, potential, stability, voi);
					RayTraceResult ray = MiscOp.rayTrace(player, 32);
					Vec3d lookVec = player.getLookVec().scale(32D);
					BlockPos endPos = ray == null ? player.getPosition().add(new Vec3i(lookVec.xCoord, lookVec.yCoord, lookVec.zCoord)) : ray.getBlockPos();
					IEffect effect = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
					if(effect != null){
						effect.doEffect(player.worldObj, endPos, mag.getPower());
					}
					NBTTagCompound beamNBT = new NBTTagCompound();
					new LooseBeamRenderable(player.posX, player.posY + player.getEyeHeight(), player.posZ, (int) Math.sqrt(endPos.distanceSq(player.getPosition())), player.rotationPitch, player.rotationYawHead, ((byte) Math.pow(mag.getPower(), 1D / 3D)), mag.getRGB().getRGB()).saveToNBT(beamNBT);
					ModPackets.network.sendToAllAround(new SendLooseBeamToClient(beamNBT), new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512));
				}
			}
		}
	}

	@Override
	public int getMaxItemUseDuration(ItemStack stack){
		return 72000;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack stack){
		return EnumAction.NONE;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			nbt = new NBTTagCompound();
		}
		tooltip.add("Energy output: " + nbt.getInteger(MagicElements.ENERGY.name()));
		tooltip.add("Energy stored: " + nbt.getInteger("stored_" + MagicElements.ENERGY.name()));
		tooltip.add("Potential output: " + nbt.getInteger(MagicElements.POTENTIAL.name()));
		tooltip.add("Potential stored: " + nbt.getInteger("stored_" + MagicElements.POTENTIAL.name()));
		tooltip.add("Stability output: " + nbt.getInteger(MagicElements.STABILITY.name()));
		tooltip.add("Stability stored: " + nbt.getInteger("stored_" + MagicElements.STABILITY.name()));
		tooltip.add("Void output: " + nbt.getInteger(MagicElements.VOID.name()));
		tooltip.add("Void stored: " + nbt.getInteger("stored_" + MagicElements.VOID.name()));
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand){
		playerIn.setActiveHand(hand);
		//TODO this thing sticks in the on position while changing settings. To reproduce, hold staffEnergyKey, right click, release staffEnergyKey for example.
		//Probably caused by item (nbt) changing while clicking it.
		/*
		if(worldIn.isRemote){
			MagicElements elemChanged = null;
			if(Keys.staffEnergy.isKeyDown()){
				elemChanged = MagicElements.ENERGY;
			}else if(Keys.staffPotential.isKeyDown()){
				elemChanged = MagicElements.POTENTIAL;
			}else if(Keys.staffStability.isKeyDown()){
				elemChanged = MagicElements.STABILITY;
			}else if(Keys.staffVoid.isKeyDown()){
				elemChanged = MagicElements.VOID;
			}
			if(elemChanged != null){
				worldIn.playSound(playerIn, playerIn.getPosition(), SoundEvents.UI_BUTTON_CLICK, SoundCategory.PLAYERS, 5, (float) Math.random());
				//ModPackets.network.sendToServer(new SendStaffToServer(elemChanged.name(), playerIn.isSneaking()));
				if(itemStackIn.getTagCompound() == null){
					itemStackIn.setTagCompound(new NBTTagCompound());
				}
				NBTTagCompound nbt = itemStackIn.getTagCompound();
				int i = nbt.getInteger(elemChanged.name());
				i += playerIn.isSneaking() ? -1 : 1;
				i = Math.min(8, Math.max(i, 0));
				nbt.setInteger(elemChanged.name(), i);
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
			}
		}*/
		
		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemStackIn);
	}
}
