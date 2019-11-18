package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@ObjectHolder(Crossroads.MODID)
public class TeslaCoilTileEntity extends TileEntity implements ITickableTileEntity, IIntReceiver{

	@ObjectHolder("tesla_coil")
	private static TileEntityType<TeslaCoilTileEntity> type = null;

	public static final int CAPACITY = 2000;

	private int stored = 0;
	private Boolean hasJar = null;
	public boolean redstone = false;

	public TeslaCoilTileEntity(){
		super(type);
	}

	public void syncState(){
		int message = 0;
		if(redstone){
			message |= 1;
		}
		message |= stored << 1;
		CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) 0, message, pos));
	}

	public void setStored(int storedIn){
		int prev = stored;
		stored = storedIn;
		if(!world.isRemote && prev >= TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt ^ storedIn >= TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt){
			syncState();
		}
		markDirty();
	}

	public int getStored(){
		return stored;
	}

	public int getCapacity(){
		return hasJar() ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier == 0){
			redstone = (message & 1) == 1;
			stored = message >>> 1;
		}
	}

	private boolean hasJar(){
		if(hasJar == null){
			BlockState state = world.getBlockState(pos);
			if(state.getBlock() != CrossroadsBlocks.teslaCoil){
				remove();
				return false;
			}
			hasJar = world.getBlockState(pos).get(CRProperties.ACTIVE);
		}
		return hasJar;
	}

	@Override
	public void tick(){
		if(!redstone && world.getGameTime() % 10 == 0 && stored > 0){
			TileEntity topTE = world.getTileEntity(pos.up());
			if(topTE instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) topTE).jolt(this);
			}
		}

		if(world.isRemote){
			return;
		}

		if(!redstone && stored > 0){
			Direction facing = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);
			TileEntity te = world.getTileEntity(pos.offset(facing));
			LazyOptional<IEnergyStorage> energyOpt;
			if(te != null && (energyOpt = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite())).isPresent()){
				IEnergyStorage storage = energyOpt.orElseThrow(NullPointerException::new);
				int moved = storage.receiveEnergy(stored, false);
				if(moved > 0){
					setStored(getStored() - moved);
					markDirty();
				}
			}
		}
	}

	public void addJar(ItemStack stack){
		setStored(Math.min(stored + LeydenJar.getCharge(stack), CAPACITY + LeydenJar.MAX_CHARGE));
		hasJar = true;
		markDirty();
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("stored", stored);
		nbt.putBoolean("reds", redstone);
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		stored = nbt.getInt("stored");
		redstone = nbt.getBoolean("reds");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putInt("stored", stored);
		nbt.putBoolean("reds", redstone);
		return nbt;
	}

	@Nonnull
	public ItemStack removeJar(){
		ItemStack out = new ItemStack(CRItems.leydenJar, 1);
		LeydenJar.setCharge(out, Math.min(stored, LeydenJar.MAX_CHARGE));
		setStored(stored - Math.min(stored, LeydenJar.MAX_CHARGE));
		hasJar = false;
		markDirty();
		return out;
	}

	public void rotate(){
		optIn.invalidate();
		optIn = LazyOptional.of(EnergyHandlerIn::new);
		optOut.invalidate();
		optOut = LazyOptional.of(EnergyHandlerOut::new);
	}

	@Override
	public void remove(){
		super.remove();
		optIn.invalidate();
		optOut.invalidate();
	}

	private LazyOptional<IEnergyStorage> optIn = LazyOptional.of(EnergyHandlerIn::new);
	private LazyOptional<IEnergyStorage> optOut = LazyOptional.of(EnergyHandlerOut::new);

	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY){
			return (LazyOptional<T>) (side == world.getBlockState(pos).get(CRProperties.HORIZ_FACING) ? optOut : optIn);
		}
		return super.getCapability(cap, side);
	}

	protected class EnergyHandlerIn implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toInsert = Math.min(maxReceive, getMaxEnergyStored() - stored);

			if(!simulate){
				setStored(stored + toInsert);
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
				setStored(stored - toExtract);
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
			return getCapacity();
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
