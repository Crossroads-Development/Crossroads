package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class BeamSplitterTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private Triple<Color, Integer, Integer> trip;
	private Triple<Color, Integer, Integer> tripUp;
	private BeamManager beamer;
	private BeamManager beamerUp;
	
	public int redstone;
	
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
			beamer.emit(null, 0);
		}
		if(beamerUp != null){
			beamerUp.emit(null, 0);
		}
	}
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		sent = false;
		if(beamer == null){
			beamer = new BeamManager(EnumFacing.DOWN, pos, worldObj);
		}
		if(beamerUp == null){
			beamerUp = new BeamManager(EnumFacing.UP, pos, worldObj);
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
	
	private final IMagicHandler[] magicHandler = new MagicHandler[] {new MagicHandler(0), new MagicHandler(1), new MagicHandler(2), new MagicHandler(3)};
	
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
			return side == null ? (T) magicHandler[0] : (T) magicHandler[side.getIndex() - 2];
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
		for(int i = 0; i < 4; i++){
			if(recieved[i] != null){
				recieved[i].setNBT(nbt, "rec" + i);
			}
		}
		nbt.setIntArray("steps", steps);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		nbt.setInteger("memTripUp", beamerUp == null ? 0 : beamerUp.getPacket());
		nbt.setInteger("reds", redstone);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		steps = nbt.hasKey("steps") ? nbt.getIntArray("steps") : new int[6];
		for(int i = 0; i < 4; i++){
			recieved[i] = nbt.hasKey("rec" + i) ? MagicUnit.loadNBT(nbt, "rec" + i) : null;
		}
		memTrip = nbt.getInteger("memTrip");
		memTripUp = nbt.getInteger("memTripUp");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
			tripUp = BeamManager.getTriple(nbt.getInteger("beamUp"));
		}
		redstone = nbt.getInteger("reds");
	}
	
	private boolean sent;
	private final MagicUnit[] recieved = new MagicUnit[4];
	private int[] steps = new int[4];
	
	private class MagicHandler implements IMagicHandler{

		private final int index;
		
		private MagicHandler(int index){
			this.index = index;
		}
		
		@Override
		public void setMagic(MagicUnit mag, int step){
			if(beamer == null){
				return;
			}
			
			steps[index] = step;
			recieved[index] = mag;
			
			if(!sent || (recieved[0] == null && recieved[1] == null && recieved[2] == null && recieved[3] == null)){
				MagicUnit out = recieved[0] == null && recieved[1] == null && recieved[2] == null && recieved[3] == null ? null : new MagicUnit((recieved[0] == null ? 0 : recieved[0].getEnergy()) + (recieved[1] == null ? 0 : recieved[1].getEnergy()) + (recieved[2] == null ? 0 : recieved[2].getEnergy()) + (recieved[3] == null ? 0 : recieved[3].getEnergy()), (recieved[0] == null ? 0 : recieved[0].getPotential()) + (recieved[1] == null ? 0 : recieved[1].getPotential()) + (recieved[2] == null ? 0 : recieved[2].getPotential()) + (recieved[3] == null ? 0 : recieved[3].getPotential()), (recieved[0] == null ? 0 : recieved[0].getStability()) + (recieved[1] == null ? 0 : recieved[1].getStability()) + (recieved[2] == null ? 0 : recieved[2].getStability()) + (recieved[3] == null ? 0 : recieved[3].getStability()), (recieved[0] == null ? 0 : recieved[0].getVoid()) + (recieved[1] == null ? 0 : recieved[1].getVoid()) + (recieved[2] == null ? 0 : recieved[2].getVoid()) + (recieved[3] == null ? 0 : recieved[3].getVoid()));
				MagicUnit outMult = out == null ? null : out.mult(((double) redstone) / 15D);
				if(outMult == null || outMult.getPower() == 0){
					outMult = null;
				}
				if(out != null && outMult != null){
					out = new MagicUnit(out.getEnergy() - outMult.getEnergy(), out.getPotential() - outMult.getPotential(), out.getStability() - outMult.getStability(), out.getVoid() - outMult.getVoid());
					if(out.getPower() == 0){
						out = null;
					}
				}
				if(beamer.emit(outMult, Math.max(steps[0], Math.max(steps[1], Math.max(steps[2], steps[3]))))){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
				if(beamerUp.emit(out, Math.max(steps[0], Math.max(steps[1], Math.max(steps[2], steps[3]))))){
					ModPackets.network.sendToAllAround(new SendIntToClient("beamUp", beamerUp.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
				sent = true;
			}
		}
	}
} 
