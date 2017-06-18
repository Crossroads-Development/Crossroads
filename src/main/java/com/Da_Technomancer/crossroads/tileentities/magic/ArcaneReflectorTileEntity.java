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

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ArcaneReflectorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private Triple<Color, Integer, Integer> trip;
	private BeamManager beamer;

	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return beamer == null || beamer.getLastFullSent() == null ? null : new MagicUnit[] {beamer.getLastFullSent()};
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
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
	}

	private EnumFacing facing;

	@Override
	public void update(){
		if(facing == null){
			facing = world.getBlockState(pos).getValue(Properties.FACING);
		}

		if(world.isRemote){
			return;
		}

		if(beamer == null){
			beamer = new BeamManager(facing, pos, world);
		}

		if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(beamer.emit(toSend.getOutput()) || world.getTotalWorldTime() % (IMagicHandler.BEAM_TIME * 20) == 0){
				ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
			toSend.clear();
			markDirty();
		}else if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			toSend.addMagic(recieved.getOutput());
			recieved.clear();
			markDirty();
		}
	}

	@Override
	public void receiveInt(String context, int message, EntityPlayerMP player){
		if(context.equals("beam")){
			trip = BeamManager.getTriple(message);
		}
	}

	private final IMagicHandler magicHandler = new MagicHandler();

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
			return (T) magicHandler;
		}

		return super.getCapability(cap, side);
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
		recieved.writeToNBT("rec", nbt);
		toSend.writeToNBT("sen", nbt);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		recieved = MagicUnitStorage.readFromNBT("rec", nbt);
		toSend = MagicUnitStorage.readFromNBT("sen", nbt);
		memTrip = nbt.getInteger("memTrip");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
		}
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
