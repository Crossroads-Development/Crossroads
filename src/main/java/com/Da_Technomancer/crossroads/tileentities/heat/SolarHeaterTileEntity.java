package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class SolarHeaterTileEntity extends ModuleTE{

	@Override
	protected boolean useHeat(){
		return true;
	}

	private boolean running = false;

	@Override
	public void update(){
		super.update();
		if(world.isRemote){
			return;
		}

		//Every 30 seconds, check if we still have sky view and cache the result
		if(world.getTotalWorldTime() % 600 == 0){
			running = world.canSeeSky(pos);
		}

		//This machine can share heat with other Solar Heaters in the same line, but only other Solar Heaters. Otherwise, a heat cable is needed like normal
		TileEntity adjTE = world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(EnumFacing.AxisDirection.NEGATIVE, world.getBlockState(pos).getValue(Properties.HORIZ_AXIS))));
		if(adjTE instanceof SolarHeaterTileEntity){
			SolarHeaterTileEntity otherTE = (SolarHeaterTileEntity) adjTE;
			temp += otherTE.temp;
			temp /= 2;
			otherTE.temp = temp;
			markDirty();
			otherTE.markDirty();
		}

		if(running && temp < 250D && world.isDaytime() && !world.isRaining()){
			temp = Math.min(250D, temp + 5D);
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		running = nbt.getBoolean("running");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("running", running);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZ_AXIS))){
			return (T) heatHandler;
		}
		return super.getCapability(capability, facing);
	}
}
