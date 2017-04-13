package com.Da_Technomancer.crossroads.items;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLooseBeamToClient;
import com.Da_Technomancer.crossroads.API.technomancy.LooseBeamRenderable;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.registry.GameRegistry;

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
			ItemStack cage = player.getHeldItem(player.getActiveHand() == EnumHand.MAIN_HAND ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
			if(cage.getItem() != ModItems.beamCage || !cage.hasTagCompound()){
				player.resetActiveHand();
				return;
			}
			
			NBTTagCompound cageNbt = cage.getTagCompound();
			NBTTagCompound nbt = stack.getTagCompound();
			int energy = nbt.getInteger(MagicElements.ENERGY.name());
			int potential = nbt.getInteger(MagicElements.POTENTIAL.name());
			int stability = nbt.getInteger(MagicElements.STABILITY.name());
			int voi = nbt.getInteger(MagicElements.VOID.name());
			if(energy <= cageNbt.getInteger("stored_" + MagicElements.ENERGY.name()) && potential <= cageNbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) && stability <= cageNbt.getInteger("stored_" + MagicElements.STABILITY.name()) && voi <= cageNbt.getInteger("stored_" + MagicElements.VOID.name())){
				if(energy + potential + stability + voi > 0){
					cageNbt.setInteger("stored_" + MagicElements.ENERGY.name(), cageNbt.getInteger("stored_" + MagicElements.ENERGY.name()) - energy);
					cageNbt.setInteger("stored_" + MagicElements.POTENTIAL.name(), cageNbt.getInteger("stored_" + MagicElements.POTENTIAL.name()) - potential);
					cageNbt.setInteger("stored_" + MagicElements.STABILITY.name(), cageNbt.getInteger("stored_" + MagicElements.STABILITY.name()) - stability);
					cageNbt.setInteger("stored_" + MagicElements.VOID.name(), cageNbt.getInteger("stored_" + MagicElements.VOID.name()) - voi);
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
}
