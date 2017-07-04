package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.tileentities.magic.ArcaneReflectorTileEntity;

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

	protected int ticksExisted = 0;
	protected int outputTime = -1;
	protected MagicUnit queued = null;
	protected long lastTick = -1;

	@Override
	public void update(){	
		if(world.isRemote || lastTick == world.getTotalWorldTime() || !canRun()){
			return;
		}

		ticksExisted++;
		lastTick = world.getTotalWorldTime();

		if(outputTime <= ticksExisted){
			if(beamer == null){
				beamer = new BeamManager[6];
				boolean[] outputs = outputSides();
				for(int i = 0; i < 6; i++){
					if(outputs[i]){
						beamer[i] = new BeamManager(EnumFacing.getFront(i), pos);
					}
				}
			}
			outputTime = ticksExisted + BeamManager.BEAM_TIME;
			MagicUnit emit = queued;
			queued = null;	
			doEmit(emit);
		}
		markDirty();
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
		if(queued != null){
			nbt.setInteger("r", queued.getEnergy());
			nbt.setInteger("g", queued.getPotential());
			nbt.setInteger("b", queued.getStability());
			nbt.setInteger("v", queued.getVoid());
		}
		nbt.setInteger("existed", ticksExisted);
		nbt.setInteger("outputTime", outputTime);
		
		nbt.setBoolean("TEST", world.provider.getDimensionType() == ModDimensions.workspaceDimType && this instanceof ArcaneReflectorTileEntity);//TODO
		if(nbt.getBoolean("TEST")){//TODO TEMP FOR TESTING
			System.out.println("SAVED: POS: " + pos.toString() + "; QUEUED: " + (queued == null ? "null" : queued.toString()) + "; EXISTED: " + ticksExisted + "; TIME_OUT: " + outputTime);
		}
		
		
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
		queued = nbt.hasKey("r") ? new MagicUnit(nbt.getInteger("r"), nbt.getInteger("g"), nbt.getInteger("b"), nbt.getInteger("v")) : null;
		ticksExisted = nbt.getInteger("existed");
		outputTime = nbt.getInteger("outputTime");

		if(nbt.getBoolean("TEST")){//TODO TEMP FOR TESTING
			System.out.println("LOADED: POS: " + pos.toString() + "; QUEUED: " + (queued == null ? "null" : queued.toString()) + "; EXISTED: " + ticksExisted + "; TIME_OUT: " + outputTime);
		}
		
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
				if(lastTick != world.getTotalWorldTime()){
					update();
				}
				if(queued == null){
					queued = mag;
					outputTime = ticksExisted + BeamManager.BEAM_TIME;
					markDirty();
					return;
				}

				queued = new MagicUnit(mag.getEnergy() + queued.getEnergy(), mag.getPotential() + queued.getPotential(), mag.getStability() + queued.getStability(), mag.getVoid() + queued.getVoid());
				markDirty();
			}
		}
	}

	/**
	 * @return Whether this device should be able to run. 
	 * 
	 * Several parts of the magic system are extremely sensitive to loading delays, especially through prototypes. This helps with that by returning false if a device should pretend it isn't loaded. 
	 */
	protected boolean canRun(){
		if(world.provider.getDimension() == ModDimensions.PROTOTYPE_DIM_ID){
			PrototypeWorldSavedData data = PrototypeWorldSavedData.get(false);

			//This calculates the prototype index based on the chunk's position. 
			int index = ((pos.getZ() >> 4) * 50) + (99 * 50);
			int holder = index - (index % 100) + (((pos.getX() >> 4) + 99) / 2);
			if(holder < index){
				holder += 100;
			}
			index = holder;

			if(data.prototypes.size() <= index){
				return false;
			}
			PrototypeInfo datum = data.prototypes.get(index);

			return datum != null && datum.owner != null && datum.owner.get() != null && datum.owner.get().shouldRun();
		}

		return true;
	}
}
