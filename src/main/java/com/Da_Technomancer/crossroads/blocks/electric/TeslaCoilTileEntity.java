package com.Da_Technomancer.crossroads.blocks.electric;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.api.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.items.CRItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeslaCoilTileEntity extends BlockEntity implements ITickableTileEntity, IIntReceiver{

	public static final BlockEntityType<TeslaCoilTileEntity> TYPE = CRTileEntity.createType(TeslaCoilTileEntity::new, CRBlocks.teslaCoil);

	public static final int CAPACITY = 2000;

	private int stored = 0;
	private Boolean hasJar = null;
	public boolean redstone = false;

	public TeslaCoilTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public float getRedstone(){
		return stored;
	}

	public void syncState(){
		int message = 0;
		if(redstone){
			message |= 1;
		}
		message |= stored << 1;
		CRPackets.sendPacketAround(level, worldPosition, new SendIntToClient((byte) 0, message, worldPosition));
	}

	public void setStored(int storedIn){
		int prev = stored;
		stored = storedIn;
		if(!level.isClientSide && prev >= TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt ^ storedIn >= TeslaCoilTop.TeslaCoilVariants.DECORATIVE.joltAmt){
			syncState();
		}
		setChanged();
	}

	public int getStored(){
		return stored;
	}

	public int getCapacity(){
		return hasJar() ? CAPACITY + LeydenJar.MAX_CHARGE : CAPACITY;
	}

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayer sendingPlayer){
		if(identifier == 0){
			redstone = (message & 1) == 1;
			stored = message >>> 1;
		}
	}

	private boolean hasJar(){
		if(hasJar == null){
			BlockState state = level.getBlockState(worldPosition);
			if(state.getBlock() != CRBlocks.teslaCoil){
				setRemoved();
				return false;
			}
			hasJar = level.getBlockState(worldPosition).getValue(CRProperties.ACTIVE);
		}
		return hasJar;
	}

	@Override
	public void tick(){
		//This is a common tick method
		//Due to the use of RNG to generate the bolt on both sides, depending on the implementation of jolt(),
		//the timing of jolts may differ on the client vs server sides
		if(!redstone && level.random.nextInt(10) == 0 && stored > 0){
			BlockEntity topTE = level.getBlockEntity(worldPosition.above());
			if(topTE instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) topTE).jolt(this);
			}
		}
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();
		if(!redstone && stored > 0){
			Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
			BlockEntity te = level.getBlockEntity(worldPosition.relative(facing));
			LazyOptional<IEnergyStorage> energyOpt;
			if(te != null && (energyOpt = te.getCapability(ForgeCapabilities.ENERGY, facing.getOpposite())).isPresent()){
				IEnergyStorage storage = energyOpt.orElseThrow(NullPointerException::new);
				int moved = storage.receiveEnergy(stored, false);
				if(moved > 0){
					setStored(getStored() - moved);
					setChanged();
				}
			}
		}
	}

	public void addJar(ItemStack stack){
		setStored(Math.min(stored + LeydenJar.getCharge(stack), CAPACITY + LeydenJar.MAX_CHARGE));
		hasJar = true;
		setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("stored", stored);
		nbt.putBoolean("reds", redstone);
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		stored = nbt.getInt("stored");
		redstone = nbt.getBoolean("reds");
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
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
		setChanged();
		return out;
	}

	public void rotate(){
		optIn.invalidate();
		optIn = LazyOptional.of(EnergyHandlerIn::new);
		optOut.invalidate();
		optOut = LazyOptional.of(EnergyHandlerOut::new);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		optIn.invalidate();
		optOut.invalidate();
	}

	private LazyOptional<IEnergyStorage> optIn = LazyOptional.of(EnergyHandlerIn::new);
	private LazyOptional<IEnergyStorage> optOut = LazyOptional.of(EnergyHandlerOut::new);

	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == ForgeCapabilities.ENERGY){
			return (LazyOptional<T>) (side == level.getBlockState(worldPosition).getValue(CRProperties.HORIZ_FACING) ? optOut : optIn);
		}
		return super.getCapability(cap, side);
	}

	protected class EnergyHandlerIn implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			int toInsert = Math.min(maxReceive, getMaxEnergyStored() - stored);

			if(!simulate){
				setStored(stored + toInsert);
				setChanged();
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
				setChanged();
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
