package com.Da_Technomancer.crossroads.items;

import java.util.List;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseBeamToClient;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class StaffTechnomancy extends MagicUsingItem{

	public StaffTechnomancy(){
		String name = "staff_technomancy";
		setUnlocalizedName(name);
		setRegistryName(name);
		GameRegistry.register(this);
		setCreativeTab(ModItems.tabCrossroads);
	}
	
	@Override
	public void onUsingTick(ItemStack stack, EntityLivingBase player, int count){
		super.onUsingTick(stack, player, count);
		if(!player.world.isRemote && getMaxItemUseDuration(stack) - count >= 4){
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
						effect.doEffect(player.world, endPos, mag.getPower());
					}
					NBTTagCompound beamNBT = new NBTTagCompound();
					new LooseBeamRenderable(player.posX, player.posY + player.getEyeHeight(), player.posZ, (int) Math.sqrt(endPos.distanceSq(player.getPosition())), player.rotationPitch, player.rotationYawHead, ((byte) Math.pow(mag.getPower(), 1D / 3D)), mag.getRGB().getRGB()).saveToNBT(beamNBT);
					ModPackets.network.sendToAllAround(new SendLooseBeamToClient(beamNBT), new TargetPoint(player.dimension, player.posX, player.posY, player.posZ, 512));
					//The reason that the hand is reset each time is because otherwise it is possible to get stuck in the on position while
					//releasing right click and any other key at the same time, and also editing the itemstack. That actually happened a lot.
					player.resetActiveHand();
				}
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced){
		super.addInformation(stack, playerIn, tooltip, advanced);
		//TODO remove below, magic storage will be migrated to a separate item. 
		NBTTagCompound nbt = stack.getTagCompound();
		if(nbt == null){
			nbt = new NBTTagCompound();
		}
		tooltip.add("Energy stored: " + nbt.getInteger("stored_" + MagicElements.ENERGY.name()));
		tooltip.add("Potential stored: " + nbt.getInteger("stored_" + MagicElements.POTENTIAL.name()));
		tooltip.add("Stability stored: " + nbt.getInteger("stored_" + MagicElements.STABILITY.name()));
		tooltip.add("Void stored: " + nbt.getInteger("stored_" + MagicElements.VOID.name()));
	}
}
