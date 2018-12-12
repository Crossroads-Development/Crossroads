package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class HeatReservoirTileEntity extends ModuleTE{

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new MassiveHeatHandler();
	}

	public NBTTagCompound getDropNBT(){
		NBTTagCompound nbt = new NBTTagCompound();
		heatHandler.init();
		nbt.setDouble("temp", temp);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class MassiveHeatHandler extends HeatHandler{

		@Override
		public void addHeat(double heat){
			init();
			temp += heat * 0.005D;
			markDirty();
		}
	}
}
