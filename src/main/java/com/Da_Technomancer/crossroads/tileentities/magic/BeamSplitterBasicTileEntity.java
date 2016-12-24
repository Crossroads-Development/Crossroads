package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.magic.MagicUnitStorage;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class BeamSplitterBasicTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private Triple<Color, Integer, Integer> trip;
	private Triple<Color, Integer, Integer> tripUp;
	private BeamManager beamer;
	private BeamManager beamerUp;
	
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return new MagicUnit[] {beamer == null ? null : beamer.getLastFullSent(), beamerUp == null ? null : beamerUp.getLastFullSent()};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[EnumFacing.DOWN.getIndex()] = trip;
		out[EnumFacing.UP.getIndex()] = tripUp;
		return out;
	}
	
	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
		if(beamerUp != null){
			beamerUp.emit(null);
		}
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		if(beamer == null){
			beamer = new BeamManager(EnumFacing.DOWN, pos, worldObj);
		}
		if(beamerUp == null){
			beamerUp = new BeamManager(EnumFacing.UP, pos, worldObj);
		}

		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			MagicUnit out = toSend.getOutput();
			MagicUnit outMult = out == null ? null : out.mult(.5D, false);
			if(outMult == null || outMult.getPower() == 0){
				outMult = null;
			}
			if(out != null && outMult != null){
				out = new MagicUnit(out.getEnergy() - outMult.getEnergy(), out.getPotential() - outMult.getPotential(), out.getStability() - outMult.getStability(), out.getVoid() - outMult.getVoid());
				if(out.getPower() == 0){
					out = null;
				}
			}
			if(beamer.emit(outMult)){
				ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			if(beamerUp.emit(out)){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamUp", beamerUp.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			toSend.clear();
		}else if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			toSend.addMagic(recieved.getOutput());
			recieved.clear();
		}
	}

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			trip = BeamManager.getTriple(message);
		}else if(context.equals("beamUp")){
			tripUp = BeamManager.getTriple(message);
		}
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != EnumFacing.UP && side != EnumFacing.DOWN){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != EnumFacing.UP && side != EnumFacing.DOWN){
			return (T) magicHandler;
		}
		
		return super.getCapability(cap, side);
	}

	private int memTrip;
	private int memTripUp;
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("beam", memTrip);
		nbt.setInteger("beamUp", memTripUp);
		return nbt;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		recieved.writeToNBT("rec", nbt);
		toSend.writeToNBT("sen", nbt);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		nbt.setInteger("memTripUp", beamerUp == null ? 0 : beamerUp.getPacket());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		recieved = MagicUnitStorage.readFromNBT("rec", nbt);
		toSend = MagicUnitStorage.readFromNBT("sen", nbt);
		memTrip = nbt.getInteger("memTrip");
		memTripUp = nbt.getInteger("memTripUp");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
			tripUp = BeamManager.getTriple(nbt.getInteger("beamUp"));
		}
	}
	
	private MagicUnitStorage recieved = new MagicUnitStorage();
	private MagicUnitStorage toSend = new MagicUnitStorage();
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
			recieved.addMagic(mag);
		}
	}
} 
