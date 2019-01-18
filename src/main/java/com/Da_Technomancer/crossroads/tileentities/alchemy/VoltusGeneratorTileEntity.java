package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.ModConfig;
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

public class VoltusGeneratorTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final int VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 20_000;
	private int voltusAmount = 0;
	private int fe = 0;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add(voltusAmount + "/" + VOLTUS_CAPACITY + " Voltus");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		int usedVoltus = Math.min(voltusAmount, (int) ((FE_CAPACITY - fe) * ModConfig.getConfigDouble(ModConfig.voltusUsage, false) / 1000D));

		if(usedVoltus > 0){
			voltusAmount -= usedVoltus;
			fe += usedVoltus * 1000D / ModConfig.getConfigDouble(ModConfig.voltusUsage, false);
			markDirty();
		}

		for(EnumFacing dir : EnumFacing.VALUES){
			TileEntity te = world.getTileEntity(pos.offset(dir));
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, dir.getOpposite())){
				IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
				int moved = storage.receiveEnergy(fe, false);
				if(moved > 0){
					fe -= moved;
					markDirty();
				}
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		voltusAmount = nbt.getInteger("voltus");
		fe = nbt.getInteger("fe");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("voltus", voltusAmount);
		nbt.setInteger("fe", fe);
		return nbt;
	}

	private IChemicalHandler handler = new AlchHandler();
	private ElecHandler feHandler = new ElecHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return cap == Capabilities.CHEMICAL_CAPABILITY || cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY){
			return (T) handler;
		}
		if(cap == CapabilityEnergy.ENERGY){
			return (T) feHandler;
		}
		return super.getCapability(cap, side);
	}

	private class ElecHandler implements IEnergyStorage{


		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int toMove = Math.min(maxExtract, fe);
			if(!simulate){
				fe -= toMove;
				markDirty();
			}
			return toMove;
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
			return true;
		}

		@Override
		public boolean canReceive(){
			return false;
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
		public int getTransferCapacity(){
			return VOLTUS_CAPACITY;
		}

		@Override
		public double getTemp(){
			return HeatUtil.ABSOLUTE_ZERO;
		}

		@Override
		public boolean insertReagents(ReagentMap reag, EnumFacing side, IChemicalHandler caller, boolean ignorePhase){
			//Only allows insertion of voltus
			if(voltusAmount >= VOLTUS_CAPACITY || reag.getQty(EnumReagents.ELEM_CHARGE.id()) == 0){
				return false;
			}

			int moved = Math.min(reag.getQty(EnumReagents.ELEM_CHARGE.id()), VOLTUS_CAPACITY - voltusAmount);
			voltusAmount += moved;
			reag.removeReagent(EnumReagents.ELEM_CHARGE.id(), moved);
			markDirty();
			return true;
		}

		@Override
		public int getContent(IReagent type){
			return type == AlchemyCore.REAGENTS.get(EnumReagents.ELEM_CHARGE.id()) ? voltusAmount : 0;
		}
	}
}
