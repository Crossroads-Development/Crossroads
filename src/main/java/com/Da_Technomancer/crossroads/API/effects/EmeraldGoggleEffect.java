package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.packets.SendFieldDisableToClient;
import com.Da_Technomancer.crossroads.API.packets.SendFieldsToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class EmeraldGoggleEffect implements IGoggleEffect{

	/**
	 * Initial value chosen at random
	 */
	private static final int CHAT_ID = 246547;

	@Override
	public void armorTick(World world, EntityPlayer player){
		RayTraceResult ray = MiscOp.rayTrace(player, 8);
		if(ray != null && world.getTileEntity(ray.getBlockPos()) != null){
			if(world.getTileEntity(ray.getBlockPos()).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite())){
				IAxleHandler axle = world.getTileEntity(ray.getBlockPos()).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, ray.sideHit.getOpposite());
				String out = "Speed: " + axle.getMotionData()[0] + ",\n";
				out += "Energy: " + axle.getMotionData()[1] + ",\n";
				out += "Power: " + axle.getMotionData()[2] + ",\n";
				out += "I: " + axle.getPhysData()[1] + ", Rotation Ratio: " + axle.getRotationRatio();
				ModPackets.network.sendTo(new SendChatToClient(out, CHAT_ID), (EntityPlayerMP) player);
			}else if(world.getTileEntity(ray.getBlockPos()).hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null)){
				ModPackets.network.sendTo(new SendChatToClient("Total Energy: " + world.getTileEntity(ray.getBlockPos()).getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, null).getTotalEnergy(), CHAT_ID), (EntityPlayerMP) player);
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