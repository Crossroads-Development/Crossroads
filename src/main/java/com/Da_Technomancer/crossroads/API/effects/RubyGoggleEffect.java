package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.fields.FieldWorldSavedData;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;
import com.Da_Technomancer.crossroads.API.packets.SendFieldDisableToClient;
import com.Da_Technomancer.crossroads.API.packets.SendFieldsToClient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class RubyGoggleEffect implements IGoggleEffect{

	/**
	 * Initial value chosen at random
	 */
	private static final int CHAT_ID = 587082;

	@Override
	public void armorTick(World world, EntityPlayer player){
		RayTraceResult ray = MiscOp.rayTrace(player, 8);
		//Notice that null side is used for finding the capability. This is because most blocks with heat support only have one side with the capability, which would normally be inaccessible. 
		if(ray != null && world.getTileEntity(ray.getBlockPos()) != null && world.getTileEntity(ray.getBlockPos()).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null)){
			ModPackets.network.sendTo(new SendChatToClient("Temp: " + world.getTileEntity(ray.getBlockPos()).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, null).getTemp() + "*C, Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(ray.getBlockPos()).getFloatTemperature(ray.getBlockPos()) + "*C", CHAT_ID), (EntityPlayerMP) player);
		}
		
		if(world.getTotalWorldTime() % 5 == 1){
			if(FieldWorldSavedData.get(world).fieldNodes.containsKey(FieldWorldSavedData.getLongFromPos(player.getPosition()))){
				ModPackets.network.sendTo(new SendFieldsToClient(FieldWorldSavedData.get(world).fieldNodes.get(FieldWorldSavedData.getLongFromPos(player.getPosition()))[0], (byte) 0, FieldWorldSavedData.getLongFromPos(player.getPosition())), (EntityPlayerMP) player);
			}else{
				ModPackets.network.sendTo(new SendFieldDisableToClient(FieldWorldSavedData.getLongFromPos(player.getPosition())), (EntityPlayerMP) player);
			}
		}
	}
}