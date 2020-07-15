package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class DynamoTileEntity extends ModuleTE{

	@ObjectHolder("dynamo")
	public static TileEntityType<DynamoTileEntity> type = null;

	private static final int CHARGE_CAPACITY = 8_000;
	public static final int INERTIA = 200;

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

		int operations = (int) Math.abs(motData[1]);
		if(operations > 0){
			motData[1] -= operations * Math.signum(motData[1]);
			fe += operations * CRConfig.electPerJoule.get();
			fe = Math.min(fe, CHARGE_CAPACITY);
			markDirty();
		}

		Direction facing = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);
		TileEntity neighbor = world.getTileEntity(pos.offset(facing.getOpposite()));
		LazyOptional<IEnergyStorage> energyOpt;
		if(neighbor != null && (energyOpt = neighbor.getCapability(CapabilityEnergy.ENERGY, facing)).isPresent()){
			IEnergyStorage handler = energyOpt.orElseThrow(NullPointerException::new);
			if(handler.canReceive()){
				fe -= handler.receiveEnergy(fe, false);
				markDirty();
			}
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
		feOpt = LazyOptional.of(DynamoEnergyHandler::new);
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

	private LazyOptional<IEnergyStorage> feOpt = LazyOptional.of(DynamoEnergyHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXLE_CAPABILITY && (side == null || side == world.getBlockState(pos).get(CRProperties.HORIZ_FACING))){
			return (LazyOptional<T>) axleOpt;
		}
		if(cap == CapabilityEnergy.ENERGY && (side == null || side == world.getBlockState(pos).get(CRProperties.HORIZ_FACING).getOpposite())){
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
			markDirty();
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
			return fe > 0;
		}

		@Override
		public boolean canReceive(){
			return false;
		}
	}
}
