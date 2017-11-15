package com.Da_Technomancer.crossroads.tileentities;

import com.Da_Technomancer.crossroads.API.Properties;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

public class PortExtenderTileEntity extends TileEntity{

	/**
	 * The purpose of this variable is to prevent chaining of port extenders or infinite loops. 
	 */
	private static boolean extensionInProgress = false;
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(!extensionInProgress && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			TileEntity te = world.getTileEntity(pos.offset(world.getBlockState(pos).getValue(Properties.FACING)));
			if(te != null){
				extensionInProgress = true;
				boolean hasCap = te.hasCapability(cap, side);
				extensionInProgress = false;
				return hasCap;
			}
		}	

		return super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(!extensionInProgress && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
			TileEntity te = world.getTileEntity(pos.offset(world.getBlockState(pos).getValue(Properties.FACING)));
			if(te != null){
				extensionInProgress = true;
				T obtainedCap = te.getCapability(cap, side);
				extensionInProgress = false;
				return obtainedCap;
			}
		}	

		return super.getCapability(cap, side);
	}	
}
