package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
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

	public CompoundNBT getDropNBT(){
		CompoundNBT nbt = new CompoundNBT();
		heatHandler.init();
		nbt.setDouble("temp", temp);
		return nbt;
	}

	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (T) heatHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redstoneHandler;
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

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			heatHandler.init();
			return read ? HeatUtil.toKelvin(temp) : 0;
		}
	}
}
