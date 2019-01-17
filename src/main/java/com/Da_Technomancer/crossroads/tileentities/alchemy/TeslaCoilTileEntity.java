package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.alchemy.LeydenJar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;

public class TeslaCoilTileEntity extends TileEntity implements ITickable{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	protected int stored = 0;
	private Boolean hasJar = null;
	public static final int CAPACITY = 2000;
	public boolean redstone = false;

	private boolean hasJar(){
		if(hasJar == null){
			IBlockState state = world.getBlockState(pos);
			if(state.getBlock() != ModBlocks.teslaCoil){
				invalidate();
				return false;
			}
			hasJar = world.getBlockState(pos).getValue(Properties.ACTIVE);
		}
		return hasJar;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!redstone && world.getTotalWorldTime() % 10 == 0 && stored > 0){
			TileEntity topTE = world.getTileEntity(pos.up());
			if(topTE instanceof TeslaCoilTopTileEntity && ((TeslaCoilTopTileEntity) topTE).jolt(this)){
				markDirty();
			}
		}

		if(!redstone && stored > 0){
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())){
				IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
				int moved = storage.receiveEnergy(stored, false);
				if(moved > 0){
					stored -= moved;
					markDirty();
				}
			}
		}
	}

	public void addJar(ItemStack stack){
		stored = Math.min(stored + LeydenJar.getCharge(stack), CAPACITY + LeydenJar.MAX_CHARGE);
		hasJar = true;
		markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("stored", stored);
		nbt.setBoolean("reds", redstone);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stored = nbt.getInteger("stored");
		redstone = nbt.getBoolean("reds");
	}

	@Nonnull
	public ItemStack removeJar(){
		ItemStack out = new ItemStack(ModItems.leydenJar, 1);
		LeydenJar.setCharge(out, Math.min(stored, LeydenJar.MAX_CHARGE));
		stored -= Math.min(stored, LeydenJar.MAX_CHARGE);
		hasJar = false;
		markDirty();
		return out;
	}

	protected final EnergyHandlerIn handlerIn = new EnergyHandlerIn();
	private final EnergyHandlerOut handlerOut = new EnergyHandlerOut();

	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) (side == world.getBlockState(pos).getValue(Properties.HORIZ_FACING) ? handlerOut : handlerIn);
		}
		return super.getCapability(cap, side);
	}

	protected class EnergyHandlerIn implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toInsert = Math.min(maxReceive, getMaxEnergyStored() - stored);

			if(!simulate){
				stored += toInsert;
				markDirty();
			}
			return toInsert;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			return 0;
		}

		@Override
		public int getEnergyStored(){
			return stored;
		}

		@Override
		public int getMaxEnergyStored(){
			return hasJar() ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
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

	private class EnergyHandlerOut implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int toExtract = Math.min(stored, maxExtract);
			if(!simulate){
				stored -= toExtract;
				markDirty();
			}
			return toExtract;
		}

		@Override
		public int getEnergyStored(){
			return stored;
		}

		@Override
		public int getMaxEnergyStored(){
			return hasJar() ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
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
