package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.electric.DynamoTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class LodestoneDynamoTileEntity extends ModuleTE{

	@ObjectHolder("lodestone_dynamo")
	public static TileEntityType<LodestoneDynamoTileEntity> type = null;

	public static final double INERTIA = DynamoTileEntity.INERTIA;
	private static final int CHARGE_CAPACITY = 8_000;

	private int fe = 0;

	public LodestoneDynamoTileEntity(){
		super(type);
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
	public void tick(){
		super.tick();

		int power = CRConfig.lodestoneDynamo.get();
		int feCost = power * CRConfig.electPerJoule.get();
		if(!world.isRemote && axleHandler.axis != null && power > 0 && fe >= feCost){
			fe -= feCost;
			axleHandler.addEnergy(power * RotaryUtil.getCCWSign(getBlockState().get(CRProperties.HORIZ_FACING)), true);
			markDirty();
		}
	}

	@Override
	protected AxleHandler createAxleHandler(){
		return new AngleAxleHandler();
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(this::createAxleHandler);
		feOpt.invalidate();
		feOpt = LazyOptional.of(LodestoneDynamoEnergyHandler::new);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		fe = nbt.getInt("charge");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("charge", fe);

		return nbt;
	}

	@Override
	public void remove(){
		super.remove();
		feOpt.invalidate();
	}

	private LazyOptional<IEnergyStorage> feOpt = LazyOptional.of(LodestoneDynamoEnergyHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == getBlockState().get(CRProperties.HORIZ_FACING))){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || side == getBlockState().get(CRProperties.HORIZ_FACING).getOpposite())){
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
				markDirty();
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
