package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.electric.IEnergyCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.DynamoTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.neoforged.neoforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class LodestoneDynamoTileEntity extends ModuleTE implements IEnergyCapable{

	public static final BlockEntityType<LodestoneDynamoTileEntity> TYPE = CRTileEntity.createType(LodestoneDynamoTileEntity::new, CRBlocks.lodestoneDynamo);

	public static final double INERTIA = DynamoTileEntity.INERTIA;
	private static final int CHARGE_CAPACITY = 8_000;

	private int fe = 0;

	private IEnergyStorage energyHandler = new LodestoneDynamoEnergyHandler();

	public LodestoneDynamoTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		int power = CRConfig.lodestoneDynamo.get();
		int feCost = power * CRConfig.electPerJoule.get();
		if(axleHandler.axis != null && power > 0 && fe >= feCost){
			fe -= feCost;
			axleHandler.addEnergy(power * RotaryUtil.getCCWSign(getBlockState().getValue(CRProperties.HORIZ_FACING)), true);
			setChanged();
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

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == null || dir == getBlockState().getValue(CRProperties.HORIZ_FACING)){
			return axleHandler;
		}
		return null;
	}

	@Nullable
	@Override
	public IEnergyStorage getEnergyHandler(Direction dir){
		if(dir == null || dir == getBlockState().getValue(CRProperties.HORIZ_FACING).getOpposite()){
			return energyHandler;
		}
		return null;
	}

	private class LodestoneDynamoEnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			maxReceive = Math.min(maxReceive, CHARGE_CAPACITY - fe);
			if(!simulate){
				fe += maxReceive;
				setChanged();
			}

			return maxReceive;
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
			return CHARGE_CAPACITY;
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
}
