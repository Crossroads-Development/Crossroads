package com.Da_Technomancer.crossroads.API.magic;

import java.awt.Color;
import java.util.ArrayList;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

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

	/**
	 * The queued MagicUnits to be emitted. Can contain null MagicUnits. 
	 */
	protected ArrayList<MagicUnit> outputQueue = new ArrayList<MagicUnit>(2);
	protected long activeCycle;

	public BeamRenderTE(){
		super();
		outputQueue.add(null);
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(BeamManager.beamStage == 0 && canRun()){
			if(beamer == null){
				beamer = new BeamManager[6];
				boolean[] outputs = outputSides();
				for(int i = 0; i < 6; i++){
					if(outputs[i]){
						beamer[i] = new BeamManager(EnumFacing.getFront(i), pos);
					}
				}
			}

			MagicUnit out = outputQueue.remove(0);
			if((activeCycle != BeamManager.cycleNumber && outputQueue.size() < 2) || outputQueue.isEmpty()){
				outputQueue.add(null);
			}
			doEmit(out);
			activeCycle = BeamManager.cycleNumber;
			
			/*FOR TESTING if(world.provider.getDimension() == 27 || pos.equals(new BlockPos(200, 71, 290))){
				System.out.println("CYCLE #: " + activeCycle + ", dim: " + world.provider.getDimension());
			}*/
			
			markDirty();
		}
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

		//nbt.setInteger("queue_size", outputQueue.size());
		MagicUnit merged = new MagicUnit(0, 0, 0, 0);
		for(int i = 0; i < outputQueue.size(); i++){
			MagicUnit mag = outputQueue.get(i);
			if(mag != null){
				merged = new MagicUnit(merged.getEnergy() + mag.getEnergy(), merged.getPotential() + mag.getPotential(), merged.getStability() + mag.getStability(), merged.getVoid() + mag.getVoid());
			}
		}
		if(merged.getPower() != 0){
			nbt.setInteger("r", merged.getEnergy());
			nbt.setInteger("g", merged.getPotential());
			nbt.setInteger("b", merged.getStability());
			nbt.setInteger("v", merged.getVoid());
		}
		nbt.setLong("cyc", activeCycle);
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
		//int size = nbt.getInteger("queue_size");
		//for(int i = 0; i < size; i++){
		
		if(nbt.hasKey("r")){
			outputQueue.clear();
			outputQueue.add(new MagicUnit(nbt.getInteger("r"), nbt.getInteger("g"), nbt.getInteger("b"), nbt.getInteger("v")));
		}
		/*}
		if(size == 0){
			outputQueue.add(null);
		}*/
		activeCycle = nbt.getLong("cyc");

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
			if(activeCycle == BeamManager.cycleNumber || outputQueue.size() >= 2){
				MagicUnit prev = outputQueue.remove(outputQueue.size() - 1);
				MagicUnit combined = prev == null ? mag : mag == null ? prev : new MagicUnit(mag.getEnergy() + prev.getEnergy(), mag.getPotential() + prev.getPotential(), mag.getStability() + prev.getStability(), mag.getVoid() + prev.getVoid());
				outputQueue.add(combined);
				activeCycle = BeamManager.cycleNumber;
				markDirty();
				return;
			}
			outputQueue.add(mag);
			activeCycle = BeamManager.cycleNumber;
			markDirty();
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
