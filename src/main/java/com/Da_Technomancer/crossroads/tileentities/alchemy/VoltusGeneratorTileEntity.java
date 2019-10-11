package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.alchemy.*;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class VoltusGeneratorTileEntity extends TileEntity implements ITickableTileEntity, IInfoTE{

	private static final int VOLTUS_CAPACITY = 100;
	private static final int FE_CAPACITY = 20_000;
	private int voltusAmount = 0;
	private int fe = 0;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add(voltusAmount + "/" + VOLTUS_CAPACITY + " Voltus");
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}

		int usedVoltus = Math.min(voltusAmount, (int) ((FE_CAPACITY - fe) * ((ForgeConfigSpec.DoubleValue) CRConfig.voltusUsage).get() / 1000D));

		if(usedVoltus > 0){
			voltusAmount -= usedVoltus;
			fe += usedVoltus * 1000D / ((ForgeConfigSpec.DoubleValue) CRConfig.voltusUsage).get();
			markDirty();
		}

		for(Direction dir : Direction.VALUES){
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
	public void read(CompoundNBT nbt){
		super.read(nbt);
		voltusAmount = nbt.getInt("voltus");
		fe = nbt.getInt("fe");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("voltus", voltusAmount);
		nbt.putInt("fe", fe);
		return nbt;
	}

	private IChemicalHandler handler = new AlchHandler();
	private ElecHandler feHandler = new ElecHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		return cap == Capabilities.CHEMICAL_CAPABILITY || cap == CapabilityEnergy.ENERGY || super.hasCapability(cap, side);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
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
		public EnumTransferMode getMode(Direction side){
			return side == Direction.DOWN ? EnumTransferMode.INPUT : EnumTransferMode.NONE;
		}

		@Override
		public EnumContainerType getChannel(Direction side){
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
		public boolean insertReagents(ReagentMap reag, Direction side, IChemicalHandler caller, boolean ignorePhase){
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
