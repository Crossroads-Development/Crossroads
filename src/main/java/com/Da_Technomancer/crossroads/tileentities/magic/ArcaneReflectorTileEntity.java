package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.blocks.magic.ArcaneReflector;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ArcaneReflectorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private Triple<Color, Integer, Integer> trip;
	private BeamManager beamer;
	
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
			beamer.emit(null, 0);
		}
	}

	private EnumFacing facing;
	
	@Override
	public void update(){
		if(facing == null){
			facing = worldObj.getBlockState(pos).getBlock() instanceof ArcaneReflector ? worldObj.getBlockState(pos).getValue(Properties.FACING) : null;
		}
		
		if(worldObj.isRemote){
			return;
		}
		
		sent = false;
		if(beamer == null){
			beamer = new BeamManager(facing, pos, worldObj);
		}
	}
	
	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			trip = BeamManager.getTriple(message);
		}
	}
	
	private final IMagicHandler[] magicHandler = new MagicHandler[] {new MagicHandler(0), new MagicHandler(1), new MagicHandler(2), new MagicHandler(3), new MagicHandler(4), new MagicHandler(5)};
	
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
			return side == null ? (T) magicHandler[0] : (T) magicHandler[side.getIndex()];
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
		for(int i = 0; i < 6; i++){
			if(recieved[i] != null){
				recieved[i].setNBT(nbt, "rec" + i);
			}
		}
		nbt.setIntArray("steps", steps);
		nbt.setInteger("memTrip", beamer == null ? 0 : beamer.getPacket());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		steps = nbt.hasKey("steps") ? nbt.getIntArray("steps") : new int[6];
		for(int i = 0; i < 6; i++){
			recieved[i] = nbt.hasKey("rec" + i) ? MagicUnit.loadNBT(nbt, "rec" + i) : null;
		}
		memTrip = nbt.getInteger("memTrip");
		if(nbt.hasKey("beam")){
			trip = BeamManager.getTriple(nbt.getInteger("beam"));
		}
	}
	
	private boolean sent;
	private final MagicUnit[] recieved = new MagicUnit[6];
	private int[] steps = new int[6];
	
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
			
			if(!sent){
				if(beamer.emit(recieved[0] == null && recieved[1] == null && recieved[2] == null && recieved[3] == null && recieved[4] == null && recieved[5] == null ? null : new MagicUnit((recieved[0] == null ? 0 : recieved[0].getEnergy()) + (recieved[1] == null ? 0 : recieved[1].getEnergy()) + (recieved[2] == null ? 0 : recieved[2].getEnergy()) + (recieved[3] == null ? 0 : recieved[3].getEnergy()) + (recieved[4] == null ? 0 : recieved[4].getEnergy()) + (recieved[5] == null ? 0 : recieved[5].getEnergy()), (recieved[0] == null ? 0 : recieved[0].getPotential()) + (recieved[1] == null ? 0 : recieved[1].getPotential()) + (recieved[2] == null ? 0 : recieved[2].getPotential()) + (recieved[3] == null ? 0 : recieved[3].getPotential()) + (recieved[4] == null ? 0 : recieved[4].getPotential()) + (recieved[5] == null ? 0 : recieved[5].getPotential()), (recieved[0] == null ? 0 : recieved[0].getStability()) + (recieved[1] == null ? 0 : recieved[1].getStability()) + (recieved[2] == null ? 0 : recieved[2].getStability()) + (recieved[3] == null ? 0 : recieved[3].getStability()) + (recieved[4] == null ? 0 : recieved[4].getStability()) + (recieved[5] == null ? 0 : recieved[5].getStability()), (recieved[0] == null ? 0 : recieved[0].getVoid()) + (recieved[1] == null ? 0 : recieved[1].getVoid()) + (recieved[2] == null ? 0 : recieved[2].getVoid()) + (recieved[3] == null ? 0 : recieved[3].getVoid()) + (recieved[4] == null ? 0 : recieved[4].getVoid()) + (recieved[5] == null ? 0 : recieved[5].getVoid())), Math.max(steps[0], Math.max(steps[1], Math.max(steps[2], Math.max(steps[3], Math.max(steps[4], steps[5]))))))){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", beamer.getPacket(), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				}
				sent = true;
			}
		}
	}
} 
