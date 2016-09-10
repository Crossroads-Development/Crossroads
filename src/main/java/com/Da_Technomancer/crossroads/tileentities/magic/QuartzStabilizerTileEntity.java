package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
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
	private int[] stored = new int[4];
	private EnumFacing facing;
	
	@Override
	@Nullable
	public MagicUnit[] getLastSent(){
		return beamer == null || beamer.getLastSent() == null ? null : new MagicUnit[] {beamer.getLastSent()};
	}
	
	private Triple<Color, Integer, Integer> trip;
	
	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null, 0);
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
			if(stored[0] != 0 || stored[1] != 0 || stored[2] != 0 || stored[3] != 0){
				double mult = ((double) RATE[large ? 1 : 0]) / ((double) (stored[0] + stored[1] + stored[2] + stored[3]));
				MagicUnit mag = new MagicUnit(Math.min(MiscOp.safeRound((stored[0]) * mult), stored[0]), Math.min(MiscOp.safeRound((stored[1]) * mult), stored[1]), Math.min(MiscOp.safeRound((stored[2]) * mult), stored[2]), Math.min(MiscOp.safeRound((stored[3]) * mult), stored[3]));
				stored[0] -= mag.getEnergy();
				stored[1] -= mag.getPotential();
				stored[2] -= mag.getStability();
				stored[3] -= mag.getVoid();
				if(beamer.emit(mag, 0)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else{
				if(beamer.emit(null, 0)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}
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
		nbt.setIntArray("store", stored);
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
		stored = nbt.hasKey("store") ? nbt.getIntArray("store") : new int[4];
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
		public void setMagic(MagicUnit mag, int steps){
			if(mag == null){
				return;
			}
			MagicUnit magAdd = mag.mult(Math.min(((double) (LIMIT[large ? 1 : 0] - (stored[0] + stored[1] + stored[2] + stored[3]))) / ((double) mag.getPower()), 1));
			stored[0] += magAdd.getEnergy();
			stored[1] += magAdd.getPotential();
			stored[2] += magAdd.getStability();
			stored[3] += magAdd.getVoid();
		}
	}
} 
