package com.Da_Technomancer.crossroads.API.effects;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendChatToClient;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DiamondGoggleEffect implements IGoggleEffect{
	
	/**
	 * Initial value chosen at random
	 */
	private static final int CHAT_ID = 928678;

	@Override
	public void armorTick(World world, EntityPlayer player){
		RayTraceResult ray = MiscOp.rayTrace(player, 8);
		//Notice that null side is used for finding the capability. This is because most blocks with fluid support only have one side with the capability, which would normally be inaccessible. 
		if(ray != null && world.getTileEntity(ray.getBlockPos()) != null && world.getTileEntity(ray.getBlockPos()).hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
			IFluidHandler fluids = world.getTileEntity(ray.getBlockPos()).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
			String out = "";
			for(IFluidTankProperties tank : fluids.getTankProperties()){
				if(!out.equals("")){
					out += "\n";
				}
				out += "Type: " + (tank.getContents() == null ? "None" : MiscOp.localizeEither(tank.getContents().getFluid().getUnlocalizedName())) + ", Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Capacity: " + tank.getCapacity() + ", Pressure: " + (tank.getContents() == null ? 0 : tank.getContents().amount) / tank.getCapacity();
			}
			ModPackets.network.sendTo(new SendChatToClient(out, CHAT_ID), (EntityPlayerMP) player);
		}
	}
}