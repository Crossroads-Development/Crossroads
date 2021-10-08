package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class DynamoTileEntity extends ModuleTE{

	@ObjectHolder("dynamo")
	public static BlockEntityType<DynamoTileEntity> type = null;

	private static final int CHARGE_CAPACITY = 8_000;
	public static final int INERTIA = 200;
	public static final double POWER_MULT = 20;

	private int fe = 0;

	public DynamoTileEntity(){
		super(type);
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
	public void tick(){
		super.tick();

		int operations = (int) Math.min(Math.abs(energy), POWER_MULT * Math.abs(axleHandler.getSpeed()));
		if(operations > 0){
			axleHandler.addEnergy(-operations, false);
			fe += operations * CRConfig.electPerJoule.get();
			fe = Math.min(fe, CHARGE_CAPACITY);
			setChanged();
		}

		//Transfer FE
		Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
		BlockEntity neighbor = level.getBlockEntity(worldPosition.relative(facing.getOpposite()));
		LazyOptional<IEnergyStorage> energyOpt;
		if(neighbor != null && (energyOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, facing)).isPresent()){
			IEnergyStorage handler = energyOpt.orElseThrow(NullPointerException::new);
			if(handler.canReceive()){
				fe -= handler.receiveEnergy(fe, false);
				setChanged();
			}
		}
	}

	@Override
	public void clearCache(){
		super.clearCache();
		axleOpt.invalidate();
		axleOpt = LazyOptional.of(() -> axleHandler);
		feOpt.invalidate();
		feOpt = LazyOptional.of(() -> energyHandler);
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		fe = nbt.getInt("charge");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putInt("charge", fe);

		return nbt;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		feOpt.invalidate();
	}

	private final IEnergyStorage energyHandler = new DynamoEnergyHandler();
	private LazyOptional<IEnergyStorage> feOpt = LazyOptional.of(() -> energyHandler);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == getBlockState().getValue(CRProperties.HORIZ_FACING))){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || side == getBlockState().getValue(CRProperties.HORIZ_FACING).getOpposite())){
			return (LazyOptional<T>) feOpt;
		}
		return super.getCapability(cap, side);
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
