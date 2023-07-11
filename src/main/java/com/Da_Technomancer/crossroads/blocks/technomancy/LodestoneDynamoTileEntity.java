package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.electric.DynamoTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

public class LodestoneDynamoTileEntity extends ModuleTE{

	public static final BlockEntityType<LodestoneDynamoTileEntity> TYPE = CRTileEntity.createType(LodestoneDynamoTileEntity::new, CRBlocks.lodestoneDynamo);

	public static final double INERTIA = DynamoTileEntity.INERTIA;
	private static final int CHARGE_CAPACITY = 8_000;

	private int fe = 0;

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
	public void setBlockState(BlockState stateIn){
		super.setBlockState(stateIn);
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(this::createAxleHandler);
		feOpt.invalidate();
		feOpt = LazyOptional.of(LodestoneDynamoEnergyHandler::new);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		fe = nbt.getInt("charge");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("charge", fe);

	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		feOpt.invalidate();
	}

	private LazyOptional<IEnergyStorage> feOpt = LazyOptional.of(LodestoneDynamoEnergyHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == getBlockState().getValue(CRProperties.HORIZ_FACING))){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == ForgeCapabilities.ENERGY && (side == null || side == getBlockState().getValue(CRProperties.HORIZ_FACING).getOpposite())){
			return (LazyOptional<T>) feOpt;
		}
		return super.getCapability(cap, side);
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
