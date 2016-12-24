package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.magic.MagicUnitStorage;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.magic.QuartzStabilizer;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class QuartzStabilizerTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private boolean large;
	private static final int[] LIMIT = new int[] {30, 150};
	private static final int[] RATE = new int[] {6, 15};
	private MagicUnitStorage recieved = new MagicUnitStorage();
	private MagicUnitStorage toSend = new MagicUnitStorage();
	private EnumFacing facing;
	
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return beamer == null || beamer.getLastFullSent() == null ? null : new MagicUnit[] {beamer.getLastFullSent()};
	}
	
	private Triple<Color, Integer, Integer> trip;
	
	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
	}
	
	public QuartzStabilizerTileEntity(){
		
	}
	
	public QuartzStabilizerTileEntity(boolean large){
		this.large = large;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		if(facing == null){
			return null;
		}
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[facing.getIndex()] = trip;
		return out;
	}
	
	@Override
	public void update(){
		if(facing == null){
			facing = worldObj.getBlockState(pos).getBlock() instanceof QuartzStabilizer ? worldObj.getBlockState(pos).getValue(Properties.FACING) : null;
		}
		
		if(worldObj.isRemote){
			return;
		}
		
		if(beamer == null){
			beamer = new BeamManager(facing, pos, worldObj);
		}
		
		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(!toSend.isEmpty()){
				double mult = Math.min(1, ((double) RATE[large ? 1 : 0]) / ((double) (toSend.getOutput().getPower())));
				MagicUnit mag = toSend.getOutput().mult(mult, true);
				toSend.subtractMagic(mag);
				if(beamer.emit(mag)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else{
				if(beamer.emit(null)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
		}else if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			MagicUnit magAdd = recieved.isEmpty() ? null : recieved.getOutput().mult(Math.min(((double) (LIMIT[large ? 1 : 0] - (toSend.isEmpty() ? 0 : toSend.getOutput().getPower()))) / ((double) recieved.getOutput().getPower()), 1), false);
			toSend.addMagic(magAdd);
			recieved.clear();
		}
	}
	
	private BeamManager beamer;

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			trip = BeamManager.getTriple(message);
		}
	}

	private int memTrip;
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("beam", memTrip);
		return nbt;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("large", large);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		recieved.writeToNBT("rec", nbt);
		toSend.writeToNBT("sen", nbt);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		large = nbt.getBoolean("large");
		memTrip = nbt.getInteger("memTrip");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
		}
		recieved = MagicUnitStorage.readFromNBT("rec", nbt);
		toSend = MagicUnitStorage.readFromNBT("sen", nbt);
	}
	
	private final IMagicHandler[] magicHandler = {new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler()};
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != facing){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != facing){
			return (T) magicHandler[side == null ? 0 : side.getIndex()];
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
			recieved.addMagic(mag);
		}
	}
} 
