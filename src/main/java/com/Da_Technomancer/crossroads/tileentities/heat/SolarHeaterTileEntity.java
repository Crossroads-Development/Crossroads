package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.*;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import java.util.ArrayList;

public class SolarHeaterTileEntity extends TileEntity implements ITickable, IInfoTE{

	private double temp;
	private boolean init = false;
	private boolean running = false;

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		chat.add("Temp: " + MiscOp.betterRound(handler.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		//Every 30 seconds, check if we still have sky view and cache the result
		if(!init || world.getTotalWorldTime() % 600 == 0){
			running = world.canSeeSky(pos);
		}

		if(!init){
			temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
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

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		running = nbt.getBoolean("running");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", init);
		nbt.setDouble("temp", temp);
		nbt.setBoolean("running", running);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZ_AXIS))){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private IHeatHandler handler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && (facing == null || facing.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZ_AXIS))){
			return (T) handler;
		}
		return super.getCapability(capability, facing);
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				init = true;
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}
	}
}
