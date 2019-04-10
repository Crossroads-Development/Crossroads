package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world));
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	private boolean hasRedstone(){
		IBlockState state = world.getBlockState(pos);
		if(state.getBlock() == ModBlocks.chronoHarness){
			return state.getValue(EssentialsProperties.REDSTONE_BOOL);
		}
		invalidate();
		return true;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		super.receiveLong(identifier, message, sendingPlayer);
		if(identifier == 4){
			running = message != 0L;
		}
	}

	@Override
	public void update(){
		super.update();

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
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 4, running ? 1 : 0, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		if(!world.isRemote && fe != 0){
			//Transer FE to a machine above
			TileEntity neighbor = world.getTileEntity(pos.offset(EnumFacing.UP));
			IEnergyStorage storage;
			if(neighbor != null && (storage = neighbor.getCapability(CapabilityEnergy.ENERGY, EnumFacing.DOWN)) != null){
				if(storage.canReceive()){
					fe -= storage.receiveEnergy(energyHandler.getEnergyStored(), false);
					markDirty();
				}
			}
			//Transfer FE to a machine below
			neighbor = world.getTileEntity(pos.offset(EnumFacing.DOWN));
			if(neighbor != null && (storage = neighbor.getCapability(CapabilityEnergy.ENERGY, EnumFacing.UP)) != null){
				if(storage.canReceive()){
					fe -= storage.receiveEnergy(energyHandler.getEnergyStored(), false);
					markDirty();
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == CapabilityEnergy.ENERGY){
			return (T) energyHandler;
		}

		return super.getCapability(cap, side);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("fe", fe);
		nbt.setFloat("partial_flux", partialFlux);
		nbt.setBoolean("running", running);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		fe = nbt.getInteger("fe");
		partialFlux = nbt.getFloat("partial_flux");
		running = nbt.getBoolean("running");
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("running", running);
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
