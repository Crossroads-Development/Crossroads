package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.technomancy.FluxTE;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public abstract class AbstractFluxStabilizerTE extends FluxTE{

	public boolean clientRunning;
	protected boolean crystal;

	public AbstractFluxStabilizerTE(){
		super();
	}

	public AbstractFluxStabilizerTE(boolean crystal){
		this();
		this.crystal = crystal;
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier == 4){
			clientRunning = message == 1L;
		}
		super.receiveLong(identifier, message, sendingPlayer);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("crystal", crystal);
		nbt.setBoolean("client_running", clientRunning);
		return nbt;
	}

	@Override public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		crystal = nbt.getBoolean("crystal");
		clientRunning = nbt.getBoolean("client_running");
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("client_running", clientRunning);
		return nbt;
	}

	@Override
	public int addFlux(int fluxIn){
		flux += fluxIn;
		markDirty();
		return flux;
	}

	@Override
	public boolean isFluxEmitter(){
		return false;
	}

	@Override
	public int canAccept(){
		return getCapacity() - flux;
	}

	@Override
	public boolean isFluxReceiver(){
		return true;
	}
}
