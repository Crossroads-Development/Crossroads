package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public abstract class BeamRenderTE extends BeamRenderTEBase implements ITickable, IIntReceiver{

	@SuppressWarnings("unchecked")
	protected Triple<Color, Integer, Integer>[] trip = new Triple[6];
	protected BeamManager[] beamer;

	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		return trip;
	}
	
	protected MagicUnitStorage[] queued = {new MagicUnitStorage(), new MagicUnitStorage()};
	protected long activeCycle;
	protected int nextStage = 0;//TODO

	@Override
	public void refresh(){
		if(beamer != null){
			for(BeamManager beam : beamer){
				if(beam != null){
					beam.emit(null, world);
				}
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		/*TODO
		if(nextStage != BeamManager.beamStage){
			return;
		}else{
			nextStage++;
			nextStage %= BeamManager.BEAM_TIME;
			markDirty();
		}*/
		
		if(BeamManager.beamStage == 0){
			if(beamer == null){
				beamer = new BeamManager[6];
				boolean[] outputs = outputSides();
				for(int i = 0; i < 6; i++){
					if(outputs[i]){
						beamer[i] = new BeamManager(EnumFacing.getFront(i), pos);
					}
				}
			}

			MagicUnit out = shiftStorage();
			activeCycle = BeamManager.cycleNumber;
			doEmit(out);
		}
	}

	/**
	 * Moves over the magic in queued from index 1 to 0.
	 * @return The magic previously stored in queued[0].
	 */
	@Nullable
	protected MagicUnit shiftStorage(){
		MagicUnit out = queued[0].getOutput();
		queued[0].clear();
		queued[0].addMagic(queued[1]);
		queued[1].clear();
		markDirty();
		return out;
	}

	protected abstract void doEmit(@Nullable MagicUnit toEmit);

	@Override
	public void receiveInt(int identifier, int message, EntityPlayerMP player){
		if(identifier < 6 && identifier >= 0){
			trip[identifier] = BeamManager.getTriple(message);
		}
	}

	protected int[] memTrip = new int[6];

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			if(memTrip[i] != 0){
				nbt.setInteger(i + "_beamToClient", memTrip[i]);
			}
		}
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		queued[0].writeToNBT("queue0", nbt);
		queued[1].writeToNBT("queue1", nbt);
		nbt.setLong("cyc", activeCycle);
		nbt.setInteger("nextStage", nextStage);

		if(beamer != null){
			for(int i = 0; i < 6; i++){
				nbt.setInteger(i + "_memTrip", beamer[i] == null ? 0 : beamer[i].getPacket());
			}
		}

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		queued[0] = MagicUnitStorage.readFromNBT("queue0", nbt);
		queued[1] = MagicUnitStorage.readFromNBT("queue1", nbt);
		activeCycle = nbt.getLong("cyc");
		nextStage = nbt.getInteger("nextStage");

		for(int i = 0; i < 6; i++){
			memTrip[i] = nbt.getInteger(i + "_memTrip");
			if(nbt.hasKey(i + "_beamToClient")){
				trip[i] = BeamManager.getTriple(nbt.getInteger(i + "_beamToClient"));
			}
		}
	}

	/**
	 * For informational displays. 
	 */
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		if(beamer == null){
			return null;
		}
		MagicUnit[] out = new MagicUnit[6];
		for(int i = 0; i < 6; i++){
			if(beamer[i] != null){
				out[i] = beamer[i].getLastFullSent();
			}
		}
		return out;
	}

	/**
	 * @return A size 6 boolean[] where each boolean corresponds to the index of an EnumFacing.
	 */
	protected abstract boolean[] inputSides();

	/**
	 * @return A size 6 boolean[] where each boolean corresponds to the index of an EnumFacing.
	 */
	protected abstract boolean[] outputSides();

	public boolean hasCapability(Capability<?> cap, EnumFacing dir){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (dir == null || inputSides()[dir.getIndex()])){
			return true;
		}
		return super.hasCapability(cap, dir);
	}

	protected final MagicHandler handler = new MagicHandler();

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing dir){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && (dir == null || inputSides()[dir.getIndex()])){
			return (T) handler;
		}
		return super.getCapability(cap, dir);
	}

	protected class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(mag != null){
				queued[BeamManager.cycleNumber == activeCycle ? 0 : 1].addMagic(mag);
				markDirty();
			}
		}
	}
}
