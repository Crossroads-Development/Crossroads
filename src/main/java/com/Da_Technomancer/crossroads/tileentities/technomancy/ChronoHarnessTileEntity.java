package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class ChronoHarnessTileEntity extends ModuleTE{

	public static final int POWER = 100;
	private static final int CAPACITY = 10_000;
	public boolean running = false;//Used for rendering.

	private int fe = 0;
	private float partialFlux = 0;
	public float angle = 0;//Used for rendering. Client side only

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	private boolean hasRedstone(){
		BlockState state = world.getBlockState(pos);
		if(state.getBlock() == CrossroadsBlocks.chronoHarness){
			return state.get(EssentialsProperties.REDSTONE_BOOL);
		}
		invalidate();
		return true;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 4){
			running = message != 0L;
		}
	}

	@Override
	public void tick(){
		super.tick();

		if(!world.isRemote){
			if(EntropySavedData.getSeverity(world).getRank() >= EntropySavedData.Severity.DESTRUCTIVE.getRank()){
				FluxUtil.overloadFlux(world, pos);
				return;
			}

			boolean shouldRun = fe + POWER <= CAPACITY && !hasRedstone();

			if(shouldRun){
				fe += POWER;
				markDirty();
				partialFlux += (float) POWER / (float) FluxUtil.getFePerFlux(false);
				if(partialFlux >= 1F){
					int entropy = (int) Math.floor(partialFlux);
					partialFlux -= entropy;
					EntropySavedData.addEntropy(world, entropy);
				}
			}

			if(shouldRun ^ running){
				running = shouldRun;
				CrossroadsPackets.network.sendToAllAround(new SendLongToClient((byte) 4, running ? 1 : 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		if(!world.isRemote && fe != 0){
			//Transer FE to a machine above
			TileEntity neighbor = world.getTileEntity(pos.offset(Direction.UP));
			IEnergyStorage storage;
			if(neighbor != null && (storage = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.DOWN)) != null){
				if(storage.canReceive()){
					fe -= storage.receiveEnergy(energyHandler.getEnergyStored(), false);
					markDirty();
				}
			}
			//Transfer FE to a machine below
			neighbor = world.getTileEntity(pos.offset(Direction.DOWN));
			if(neighbor != null && (storage = neighbor.getCapability(CapabilityEnergy.ENERGY, Direction.UP)) != null){
				if(storage.canReceive()){
					fe -= storage.receiveEnergy(energyHandler.getEnergyStored(), false);
					markDirty();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) energyHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("fe", fe);
		nbt.putFloat("partial_flux", partialFlux);
		nbt.putBoolean("running", running);

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		fe = nbt.getInt("fe");
		partialFlux = nbt.getFloat("partial_flux");
		running = nbt.getBoolean("running");
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		nbt.putBoolean("running", running);
		return nbt;
	}

	private final EnergyHandler energyHandler = new EnergyHandler();

	private class EnergyHandler implements IEnergyStorage{

		@Override
		public int receiveEnergy(int maxReceive, boolean simulate){
			return 0;
		}

		@Override
		public int extractEnergy(int maxExtract, boolean simulate){
			int extracted = Math.min(maxExtract, fe);
			if(!simulate && extracted > 0){
				fe -= extracted;
				markDirty();
			}
			return extracted;
		}

		@Override
		public int getEnergyStored(){
			return fe;
		}

		@Override
		public int getMaxEnergyStored(){
			return CAPACITY;
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
