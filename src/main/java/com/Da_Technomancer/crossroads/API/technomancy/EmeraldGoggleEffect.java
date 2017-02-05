package com.Da_Technomancer.crossroads.API.technomancy;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendFieldDisableToClient;
import com.Da_Technomancer.crossroads.API.packets.SendFieldsToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EmeraldGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat){
		RayTraceResult ray = MiscOp.rayTrace(player, 8);
		if(ray != null && world.getTileEntity(ray.getBlockPos()) != null){
			if(world.getTileEntity(ray.getBlockPos()).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite())){
				IAxleHandler axle = world.getTileEntity(ray.getBlockPos()).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite());
				chat.add("Speed: " + axle.getMotionData()[0]);
				chat.add("Energy: " + axle.getMotionData()[1]);
				chat.add("Power: " + axle.getMotionData()[2]);
				chat.add("I: " + axle.getPhysData()[1] + ", Rotation Ratio: " + axle.getRotationRatio());
			}else if(world.getTileEntity(ray.getBlockPos()).hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null)){
				chat.add("Total Energy: " + world.getTileEntity(ray.getBlockPos()).getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null).getTotalEnergy());
			}
		}

		if(world.getTotalWorldTime() % 5 == 1){
			if(FieldWorldSavedData.get(world).fieldNodes.containsKey(FieldWorldSavedData.getLongFromPos(player.getPosition()))){
				ModPackets.network.sendTo(new SendFieldsToClient(FieldWorldSavedData.get(world).fieldNodes.get(FieldWorldSavedData.getLongFromPos(player.getPosition()))[1], (byte) 1, FieldWorldSavedData.getLongFromPos(player.getPosition())), (EntityPlayerMP) player);
			}else{
				ModPackets.network.sendTo(new SendFieldDisableToClient(FieldWorldSavedData.getLongFromPos(player.getPosition())), (EntityPlayerMP) player);
			}
		}
	}
}