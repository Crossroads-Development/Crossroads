package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;
import java.util.ArrayList;

public abstract class AbstractStabilizerTileEntity extends ModuleTE{

	public static final int DRAIN_CAP = 8;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world));
		chat.add("Temporal Entropy Stabilization: -" + MiscUtil.betterRound(EntropySavedData.getPercentage(drained), 3));
		chat.add("Efficiency: " + MiscUtil.betterRound(getEfficiency(runTicks) * 100D, 3) + "%");
		super.addInfo(chat, player, side, hitX, hitY, hitZ);
	}

	public AbstractStabilizerTileEntity(){
		super();
	}

	public AbstractStabilizerTileEntity setCrystal(boolean crystal){
		this.crystal = crystal;
		return this;
	}

	protected boolean crystal;
	protected int runTicks = 0;
	protected int drained = 0;
	public boolean clientRunning;

	protected double getEfficiency(int runningTime){
		return Math.max(0.5D, 1D - (double) runningTime / 10_000D);
	}

	@Override
	public void update(){
		super.update();
		if(world.isRemote || world.getTotalWorldTime() % FluxUtil.FLUX_TIME == 0){
			return;
		}

		if(EntropySavedData.getSeverity(world).getRank() >= (crystal ? EntropySavedData.Severity.DESTRUCTIVE.getRank() : EntropySavedData.Severity.HARMFUL.getRank())){
			FluxUtil.overloadFlux(world, pos);
			return;
		}

		int drain = drainFuel();

		if(drain == 0 ^ runTicks == 0){
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 4, drain != 0 ? 1L : 0L, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		drained = drain;

		if(drain > 0){
			drain *= getEfficiency(runTicks);
			runTicks++;
			EntropySavedData.addEntropy(world, -drain);
			markDirty();
		}else{
			runTicks = Math.max(0, Math.min(runTicks - 5, 5_000));
		}
	}

	/**
	 * Consumes one operation worth of whatever this block is using as fuel
	 * @return The maximum amount of entropy to drain this cycle, in pts
	 */
	protected abstract int drainFuel();

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		crystal = nbt.getBoolean("crystal");
		clientRunning = nbt.getBoolean("client_running");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("crystal", crystal);
		nbt.setBoolean("client_running", clientRunning);
		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("client_running", clientRunning);
		return nbt;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 4){
			clientRunning = message == 1L;
		}
		super.receiveLong(identifier, message, sendingPlayer);
	}

	protected final RedsHandler redsHandler = new RedsHandler();

	@Nullable
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing facing){
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
		}
		return super.getCapability(cap, facing);
	}

	protected class RedsHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? getEfficiency(runTicks) : 0;
		}
	}
}
