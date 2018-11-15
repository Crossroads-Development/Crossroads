package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
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
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public abstract class BeamRenderTE extends BeamRenderTEBase implements ITickable, IIntReceiver{

	protected int[] beamPackets = new int[6];
	protected BeamManager[] beamer;
	protected MagicUnitStorage[] queued = {new MagicUnitStorage(), new MagicUnitStorage()};
	protected long activeCycle;
	protected MagicUnit[] prevMag = new MagicUnit[6];//Stores the last non-null magic sent for information readouts

	/**
	 * @return A size 6 boolean[] where each boolean corresponds to the index of an EnumFacing.
	 */
	protected abstract boolean[] inputSides();

	/**
	 * @return A size 6 boolean[] where each boolean corresponds to the index of an EnumFacing.
	 */
	protected abstract boolean[] outputSides();

	protected int getLimit(){
		return 64_000;
	}

	protected void overload(){
		world.setBlockToAir(pos);
		//TODO sound, smoke, etc.
	}

	/**
	 * Sets the beamer variable to null, use whenever rotating the block. 
	 */
	public void resetBeamer(){
//		refresh();
		beamer = null;
		beamPackets = new int[6];
		for(int i = 0; i < 6; i++){
			prevMag[i] = null;
			refreshBeam(i);
		}
	}

	protected void refreshBeam(int index){
		ModPackets.network.sendToAllAround(new SendIntToClient(index, beamer == null || beamer[index] == null ? 0 : beamer[index].genPacket(), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		if(beamer != null && beamer[index] != null && beamer[index].getLastSent() != null){
			prevMag[index] = beamer[index].getLastSent();
		}
	}
	
	@Override
	public int[] getRenderedBeams(){
		return beamPackets;
	}
//
//	@Override
//	public void refresh(){
//		if(beamer != null){
//			for(BeamManager beam : beamer){
//				if(beam != null){
//					beam.emit(null, world);
//				}
//			}
//		}
//	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		
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
			if(out != null && out.getPower() > getLimit()){
				overload();
			}
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
			beamPackets[identifier] = message;
		}
	}

	/**
	 * For informational displays.
	 */
	@Override
	public MagicUnit[] getLastSent(){
		return prevMag;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			if(beamPackets[i] != 0){
				nbt.setInteger(i + "_beam_packet", beamPackets[i]);
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

		if(beamer != null){
			for(int i = 0; i < 6; i++){
				nbt.setInteger(i + "_beam_packet", beamPackets[i]);
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

		for(int i = 0; i < 6; i++){
			beamPackets[i] = nbt.getInteger(i + "_beam_packet");
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return getCapability(cap, side) != null || super.hasCapability(cap, side);
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
