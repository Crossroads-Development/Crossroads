package com.Da_Technomancer.crossroads.API.effects.goggles;

import java.util.ArrayList;

import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class DiamondGoggleEffect implements IGoggleEffect{

	@Override
	public void armorTick(World world, EntityPlayer player, ArrayList<String> chat, RayTraceResult ray){
		//Notice that null side is used for finding the capability. This is because most blocks with fluid support only have one side with the capability, which would normally be inaccessible. 
		if(ray != null){
			TileEntity te = world.getTileEntity(ray.getBlockPos());
			if(te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)){
				IFluidHandler fluids = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
				for(IFluidTankProperties tank : fluids.getTankProperties()){
					chat.add("Type: " + (tank.getContents() == null ? "None" : tank.getContents().getLocalizedName()) + ", Amount: " + (tank.getContents() == null ? 0 : tank.getContents().amount) + ", Capacity: " + tank.getCapacity() + ", Pressure: " + ((double) (tank.getContents() == null ? 0 : tank.getContents().amount)) / ((double) tank.getCapacity()));
				}
			}
			if(te instanceof IGoggleInfoTE){
				((IGoggleInfoTE) te).addInfo(chat, GoggleLenses.DIAMOND, player, ray.sideHit);
			}
		}
	}
}