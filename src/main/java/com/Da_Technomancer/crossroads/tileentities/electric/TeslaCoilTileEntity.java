package com.Da_Technomancer.crossroads.tileentities.electric;

import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.blocks.electric.TeslaCoilTop;
import com.Da_Technomancer.crossroads.items.ModItems;
import com.Da_Technomancer.crossroads.items.LeydenJar;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TeslaCoilTileEntity extends TileEntity implements ITickable, IIntReceiver{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private int stored = 0;
	private Boolean hasJar = null;
	public static final int CAPACITY = 2000;
	public boolean redstone = false;

	public void syncState(){
		int message = 0;
		if(redstone){
			message |= 1;
		}
		message |= stored << 1;
		ModPackets.network.sendToAllAround(new SendIntToClient((byte) 0, message, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
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

	@Override
	public void receiveInt(byte identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 0){
			redstone = (message & 1) == 1;
			stored = message >>> 1;
		}
	}

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
		if(!redstone && world.getTotalWorldTime() % 10 == 0 && stored > 0){
			TileEntity topTE = world.getTileEntity(pos.up());
			if(topTE instanceof TeslaCoilTopTileEntity){
				((TeslaCoilTopTileEntity) topTE).jolt(this);
			}
		}

		if(world.isRemote){
			return;
		}

		if(!redstone && stored > 0){
			EnumFacing facing = world.getBlockState(pos).getValue(Properties.HORIZ_FACING);
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if(te != null && te.hasCapability(CapabilityEnergy.ENERGY, facing.getOpposite())){
				IEnergyStorage storage = te.getCapability(CapabilityEnergy.ENERGY, facing.getOpposite());
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

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("stored", stored);
		nbt.setBoolean("reds", redstone);
		return nbt;
	}

	@Nonnull
	public ItemStack removeJar(){
		ItemStack out = new ItemStack(ModItems.leydenJar, 1);
		LeydenJar.setCharge(out, Math.min(stored, LeydenJar.MAX_CHARGE));
		setStored(stored - Math.min(stored, LeydenJar.MAX_CHARGE));
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
