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

import net.minecraft.entity.player.EntityPlayerMP;
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
		if(world.getBlockState(pos).getBlock() != ModBlocks.crystallinePrism){
			return null;
		}
		out[world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite().getIndex()] = tripR;
		out[world.getBlockState(pos).getValue(Properties.FACING).getIndex()] = tripG;
		out[world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getIndex()] = tripB;
		return out;
	}

	private boolean primed;
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(beamerR == null){
			beamerR = new BeamManager(world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite(), pos, world);
		}
		if(beamerG == null){
			beamerG = new BeamManager(world.getBlockState(pos).getValue(Properties.FACING), pos, world);
		}
		if(beamerB == null){
			beamerB = new BeamManager(world.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y), pos, world);
		}

		if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0 && primed){

			MagicUnit out = toSend.getOutput();

			if(beamerR.emit(out == null || out.getEnergy() == 0 ? null : out.mult(1, 0, 0, 0, false)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamR", beamerR.getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			if(beamerG.emit(out == null || out.getPotential() == 0 ? null : out.mult(0, 1, 0, 0, false)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamG", beamerG.getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			if(beamerB.emit(out == null || out.getStability() == 0 ? null : out.mult(0, 0, 1, 0, false)) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
				ModPackets.network.sendToAllAround(new SendIntToClient("beamB", beamerB.getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			toSend.clear();
			primed = false;
			markDirty();
		}else if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			toSend.addMagic(recieved.getOutput());
			recieved.clear();
			primed = true;
			markDirty();
		}
	}

	@Override
	public void receiveInt(String context, int message, EntityPlayerMP player){
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
		nbt.setBoolean("primed", primed);
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
		primed = nbt.getBoolean("primed");
	}

	private final IMagicHandler magicHandler = new MagicHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == world.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
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
			if(mag != null){
				markDirty();
			}
		}
	}
} 
