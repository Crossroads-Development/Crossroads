package com.Da_Technomancer.crossroads.API.effects.goggles;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTEBase;
import com.Da_Technomancer.crossroads.API.magic.EnumMagicElements;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendFieldsToClient;
import com.Da_Technomancer.crossroads.API.packets.StoreNBTToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import java.util.ArrayList;

public class QuartzGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		if(ray == null){
			return;
		}
		TileEntity te = world.getTileEntity(ray.getBlockPos());
		if(te != null){
			if(te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
				IFluidHandler pipe = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				chat.add(pipe.getTankProperties().length + " internal tank" + (pipe.getTankProperties().length == 1 ? "." : "s."));
				for(IFluidTankProperties tank : pipe.getTankProperties()){
					chat.add("Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Type: " + (tank.getContents() == null ? "None" : tank.getContents().getLocalizedName()) + ", Capacity: " + tank.getCapacity());
				}
			}
			if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite())){
				IAxleHandler axle = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite());
				chat.add("Speed: " + axle.getMotionData()[0]);
				chat.add("Energy: " + axle.getMotionData()[1]);
				chat.add("Power: " + axle.getMotionData()[2]);
				chat.add("I: " + axle.getMoInertia() + ", Rotation Ratio: " + axle.getRotationRatio());
			}else if(te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit)){
				IAxleHandler axle = te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit);
				chat.add("Speed: " + axle.getMotionData()[0]);
				chat.add("Energy: " + axle.getMotionData()[1]);
				chat.add("Power: " + axle.getMotionData()[2]);
				chat.add("I: " + axle.getMoInertia() + ", Rotation Ratio: " + axle.getRotationRatio());
			}else if(te.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null)){
				chat.add("Total Energy: " + te.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null).getTotalEnergy());
			}

			if(te.hasCapability(CapabilityEnergy.ENERGY, null)){
				IEnergyStorage batt = te.getCapability(CapabilityEnergy.ENERGY, null);
				chat.add("Charge: " + batt.getEnergyStored() + "/" + batt.getMaxEnergyStored() + "FE");
			}
		}

		if(te instanceof BeamRenderTEBase){
			MagicUnit[] mag = ((BeamRenderTEBase) te).getLastFullSent();
			if(mag != null){
				NBTTagCompound nbt = MiscOp.getPlayerTag(player);
				if(!nbt.hasKey("elements")){
					nbt.setTag("elements", new NBTTagCompound());
				}
				nbt = nbt.getCompoundTag("elements");
				for(int i = 0; i < mag.length; i++){
					MagicUnit check = mag[i];
					if(check != null){
						if(!nbt.hasKey(EnumMagicElements.getElement(check).name())){
							nbt.setBoolean(EnumMagicElements.getElement(check).name(), true);
							//Doesn't use deletion-chat as the element discovery notification shouldn't be wiped away in 1 tick.
							player.sendMessage(new TextComponentString(TextFormatting.BOLD.toString() + "New Element Discovered: " + EnumMagicElements.getElement(check).toString() + TextFormatting.RESET.toString()));
							StoreNBTToClient.syncNBTToClient((EntityPlayerMP) player, false);
						}
						chat.add(EnumFacing.getFront(i).toString() + ": " + check.toString());
					}
				}
			}
		}

		if(te instanceof IInfoTE){
			((IInfoTE) te).addInfo(chat, player, ray.sideHit);
		}

		if(world.getTotalWorldTime() % 5 == 1){
			long key = MiscOp.getLongFromChunkPos(new ChunkPos(player.getPosition()));
			if(FieldWorldSavedData.get(world).fieldNodes.containsKey(key) && FieldWorldSavedData.get(world).fieldNodes.get(key).isActive){
				ModPackets.network.sendTo(new SendFieldsToClient(FieldWorldSavedData.get(world).fieldNodes.get(key), key), (EntityPlayerMP) player);
			}else{
				ModPackets.network.sendTo(new SendFieldsToClient(null, key), (EntityPlayerMP) player);
			}
		}
	}
}