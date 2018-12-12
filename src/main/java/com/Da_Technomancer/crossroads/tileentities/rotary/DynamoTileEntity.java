package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;

public class DynamoTileEntity extends ModuleTE{

	private static final int CHARGE_CAPACITY = 100;

	private static int efficiency = -1;
	
	/**
	 * For client side rendering. 0 on server side.
	 */
	public double angle = 0;
	/**
	 * For client side rendering. 0 on server side.
	 */
	public double nextAngle = 0;

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public double getMoInertia(){
		return 150;
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
			TileEntity neighbor = world.getTileEntity(pos.offset(facing));
			if(neighbor != null && neighbor.hasCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite())){
				IAxleHandler handler = neighbor.getCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite());
				angle = handler.getAngle();
				nextAngle = handler.getNextAngle();
			}else{
				angle = 0;
				nextAngle = 0;
			}
			
			return;
		}

		if(efficiency < 0){
			efficiency = ModConfig.getConfigInt(ModConfig.electPerJoule, false);
		}

		int operations = (int) Math.abs(motData[1]);
		if(operations > 0){
			motData[1] -= operations * Math.signum(motData[1]);
			energyHandler.setEnergy(energyHandler.getEnergyStored() + operations * efficiency);
			markDirty();
		}

		EnumFacing facing = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
		TileEntity neighbor = world.getTileEntity(pos.offset(facing.getOpposite()));
		if(neighbor != null && neighbor.hasCapability(CapabilityEnergy.ENERGY, facing)){
			IEnergyStorage handler = neighbor.getCapability(CapabilityEnergy.ENERGY, facing);
			if(handler.canReceive()){
				energyHandler.setEnergy(energyHandler.getEnergyStored() - handler.receiveEnergy(energyHandler.getEnergyStored(), false));
				markDirty();
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		energyHandler.setEnergy(nbt.getInteger("charge"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("charge", energyHandler.getEnergyStored());

		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == world.getBlockState(pos).getValue(Properties.HORIZ_FACING))){
			return (T) axleHandler;
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || side == world.getBlockState(pos).getValue(Properties.HORIZ_FACING).getOpposite())){
			return (T) energyHandler;
		}
		return super.getCapability(cap, side);
	}

	private final DynamoEnergyStorage energyHandler = new DynamoEnergyStorage(CHARGE_CAPACITY, 0, CHARGE_CAPACITY, 0);

	private static class DynamoEnergyStorage extends EnergyStorage{

		public DynamoEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy){
			super(capacity, maxReceive, maxExtract, energy);
		}

		public void setEnergy(int energyIn){
			this.energy = Math.max(0, Math.min(energyIn, capacity));
		}
	}
}
