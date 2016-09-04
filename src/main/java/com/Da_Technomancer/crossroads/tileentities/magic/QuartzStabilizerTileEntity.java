package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

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

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class QuartzStabilizerTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private boolean large;
	private static final int[] LIMIT = new int[] {30, 90};
	private static final int[] RATE = new int[] {3, 9};
	private int[] stored = new int[4];
	
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
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = trip;
		return out;
	}

	private int ticksExisted;
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		//This exists to prevent a case where the first stabilizer in a chain will have an invisible beam on relog if it has a constant output. 
		if(++ticksExisted == 10){
			ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		
		if(beamer == null){
			beamer = new BeamManager(worldObj.getBlockState(pos).getValue(Properties.FACING), pos, worldObj);
		}
		
		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(stored[0] != 0 || stored[1] != 0 || stored[2] != 0 || stored[3] != 0){
				double mult = ((double) RATE[large ? 1 : 0]) / ((double) (stored[0] + stored[1] + stored[2] + stored[3]));
				MagicUnit mag = new MagicUnit(Math.min(MiscOp.safeRound(((double) stored[0]) * mult), stored[0]), Math.min(MiscOp.safeRound(((double) stored[1]) * mult), stored[1]), Math.min(MiscOp.safeRound(((double) stored[2]) * mult), stored[2]), Math.min(MiscOp.safeRound(((double) stored[3]) * mult), stored[3]));
				stored[0] -= mag.getEnergy();
				stored[1] -= mag.getPotential();
				stored[2] -= mag.getStability();
				stored[3] -= mag.getVoid();
				if(beamer.emit(mag)){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
			}else{
				if(beamer.emit(null)){
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

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("large", large);
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		large = nbt.getBoolean("large");
	}
	
	private final IMagicHandler[] magicHandler = {new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler(), new MagicHandler()};
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != worldObj.getBlockState(pos).getValue(Properties.FACING)){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != worldObj.getBlockState(pos).getValue(Properties.FACING)){
			return (T) magicHandler[side == null ? 0 : side.getIndex()];
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
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
