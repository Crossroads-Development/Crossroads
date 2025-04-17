package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class DynamoTileEntity extends ModuleTE implements IEnergyCapable, IAxleCapable{

	public static final BlockEntityType<DynamoTileEntity> TYPE = CRTileEntity.createType(DynamoTileEntity::new, CRBlocks.dynamo);

	private static final int CHARGE_CAPACITY = 8_000;
	public static final int INERTIA = 200;
	public static final double POWER_MULT = 20;

	private int fe = 0;

	public DynamoTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	public double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		int operations = (int) Math.min(Math.abs(energy), POWER_MULT * Math.abs(axleHandler.getSpeed()));
		if(operations > 0){
			axleHandler.addEnergy(-operations, false);
			fe += operations * CRConfig.electPerJoule.get();
			fe = Math.min(fe, CHARGE_CAPACITY);
			setChanged();
		}

		//Transfer FE
		Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);

		BlockPos neighborPos = worldPosition.relative(facing.getOpposite());
		IEnergyStorage energyHandler;
		if((energyHandler = level.getCapability(Capabilities.EnergyStorage.BLOCK, neighborPos, facing)) != null){
			if(energyHandler.canReceive()){
				fe -= energyHandler.receiveEnergy(fe, false);
				setChanged();
			}
		}
	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		fe = nbt.getInt("charge");
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		nbt.putInt("charge", fe);

	}

	private final IEnergyStorage energyHandler = new DynamoEnergyHandler();

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir == getBlockState().getValue(CRProperties.HORIZ_FACING)){
			return axleHandler;
		}
		return null;
	}

	public @Nullable IEnergyStorage getEnergyHandler(Direction dir){
		if(dir == null || dir == getBlockState().getValue(CRProperties.HORIZ_FACING).getOpposite()){
			return energyHandler;
		}
		return null;
	}

	private class DynamoEnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			if(simulate){
				return Math.min(maxExtract, fe);
			}
			maxExtract = Math.min(maxExtract, fe);
			fe -= maxExtract;
			setChanged();
			return maxExtract;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return CHARGE_CAPACITY;
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
}
