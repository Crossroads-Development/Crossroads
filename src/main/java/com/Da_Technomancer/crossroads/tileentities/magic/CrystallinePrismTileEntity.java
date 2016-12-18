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
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class CrystallinePrismTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{
	
	private Triple<Color, Integer, Integer> tripR;
	private Triple<Color, Integer, Integer> tripG;
	private Triple<Color, Integer, Integer> tripB;
	
	private BeamManager beamerR;
	private BeamManager beamerG;
	private BeamManager beamerB;
	
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return new MagicUnit[] {beamerR == null ? null : beamerR.getLastFullSent(), beamerG == null ? null : beamerG.getLastFullSent(), beamerB == null ? null : beamerB.getLastFullSent()};
	}
	
	@Override
	public void refresh(){
		if(beamerR != null){
			beamerR.emit(null);
		}
		if(beamerG != null){
			beamerG.emit(null);
		}
		if(beamerB != null){
			beamerB.emit(null);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		if(worldObj.getBlockState(pos).getBlock() != ModBlocks.crystallinePrism){
			return null;
		}
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite().getIndex()] = tripR;
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = tripG;
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getIndex()] = tripB;
		return out;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		if(beamerR == null){
			beamerR = new BeamManager(worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite(), pos, worldObj);
		}
		if(beamerG == null){
			beamerG = new BeamManager(worldObj.getBlockState(pos).getValue(Properties.FACING), pos, worldObj);
		}
		if(beamerB == null){
			beamerB = new BeamManager(worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y), pos, worldObj);
		}

		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){

			MagicUnit out = toSend.getOutput();
			
			if(beamerR.emit(out == null || out.getEnergy() == 0 ? null : out.mult(1, 0, 0, 0))){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamR", beamerR.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			if(beamerG.emit(out == null || out.getPotential() == 0 ? null : out.mult(0, 1, 0, 0))){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamG", beamerG.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			if(beamerB.emit(out == null || out.getStability() == 0 ? null : out.mult(0, 0, 1, 0))){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamB", beamerB.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			toSend.clear();
		}else if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			toSend.addMagic(recieved.getOutput());
			recieved.clear();
		}
	}

	@Override
	public void receiveInt(String context, int message){
		switch(context){
			case "beamR":
				tripR = BeamManager.getTriple(message);
				break;
			case "beamG":
				tripG = BeamManager.getTriple(message);
				break;
			case "beamB":
				tripB = BeamManager.getTriple(message);
				break;
			default:
				break;
		}
	}
	
	private int memTripR;
	private int memTripG;
	private int memTripB;
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setInteger("beamR", memTripR);
		nbt.setInteger("beamG", memTripG);
		nbt.setInteger("beamB", memTripB);
		return nbt;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		recieved.writeToNBT("rec", nbt);
		toSend.writeToNBT("sen", nbt);
		nbt.setInteger("memTripR", beamerR == null ? 0 : beamerR.getPacket());
		nbt.setInteger("memTripG", beamerG == null ? 0 : beamerG.getPacket());
		nbt.setInteger("memTripB", beamerB == null ? 0 : beamerB.getPacket());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		recieved = MagicUnitStorage.readFromNBT("rec", nbt);
		toSend = MagicUnitStorage.readFromNBT("sen", nbt);
		memTripR = nbt.getInteger("memTripR");
		memTripG = nbt.getInteger("memTripG");
		memTripB = nbt.getInteger("memTripB");
		if(nbt.hasKey("beamR")){
			tripR = BeamManager.getTriple(nbt.getInteger("beamR"));
			tripG = BeamManager.getTriple(nbt.getInteger("beamG"));
			tripB = BeamManager.getTriple(nbt.getInteger("beamB"));
		}
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == worldObj.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == worldObj.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
			return (T) magicHandler;
		}
		
		return super.getCapability(cap, side);
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
