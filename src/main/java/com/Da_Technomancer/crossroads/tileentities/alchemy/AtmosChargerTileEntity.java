package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AtmosChargerTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final double VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 20_000;
	private double voltusAmount = 0;
	private int fe = 0;

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.EMERALD){
			chat.add(voltusAmount + "/" + VOLTUS_CAPACITY + " Voltus");
			int charge = AtmosChargeSavedData.getCharge(world);
			chat.add(charge + "/" + AtmosChargeSavedData.CAPACITY + "FE in atmosphere (" + MiscOp.betterRound(100D * charge / AtmosChargeSavedData.CAPACITY, 1) + "%)");
		}
	}

	@Override
	public void update(){
		//TODO
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		voltusAmount = nbt.getDouble("voltus");
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("voltus", voltusAmount);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	private IChemicalHandler handler = new AlchHandler();
	private ElecHandler feHandler = new ElecHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return (cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side == EnumFacing.DOWN)) || (cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side == EnumFacing.DOWN)){
			return (T) handler;
		}
		if(cap == CapabilityEnergy.ENERGY && side != EnumFacing.UP){
			return (T) feHandler;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{


		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toMove = Math.min(FE_CAPACITY - fe, maxReceive);

			if(!simulate && toMove > 0){
				fe += toMove;
				markDirty();
			}

			return toMove;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return FE_CAPACITY;
		}

		@Override
		public boolean canExtract(){
			return false;
		}

		@Override
		public boolean canReceive(){
			return true;
		}
	}

	private class AlchHandler implements IChemicalHandler{

		@Override
		public EnumTransferMode getMode(EnumFacing side){
			return side == EnumFacing.DOWN ? EnumTransferMode.INPUT : EnumTransferMode.NONE;
		}

		@Override
		public EnumContainerType getChannel(EnumFacing side){
			return EnumContainerType.CRYSTAL;
		}

		@Override
		public double getContent(){
			return voltusAmount;
		}

		@Override
		public double getTransferCapacity(){
			return VOLTUS_CAPACITY;
		}

		@Override
		public double getHeat(){
			return 0;
		}

		@Override
		public void setHeat(double heatIn){

		}

		@Override
		public boolean insertReagents(ReagentStack[] reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			if(voltusAmount >= VOLTUS_CAPACITY || reag[36] == null){
				return false;
			}

			ReagentStack r = reag[36];
			double moved = Math.min(reag[36].getAmount(), VOLTUS_CAPACITY - voltusAmount);
			voltusAmount += moved;
			if(r.increaseAmount(-moved) <= 0){
				reag[36] = null;
			}
			if(caller != null){
				caller.addHeat(-moved * (caller.getTemp() + 273D));
			}
			markDirty();
			return true;
		}

		@Override
		public double getContent(int type){
			return type == 36 ? voltusAmount : 0;
		}
	}
}
