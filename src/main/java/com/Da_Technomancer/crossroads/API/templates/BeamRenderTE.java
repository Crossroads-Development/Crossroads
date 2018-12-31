package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public abstract class BeamRenderTE extends BeamRenderTEBase implements ITickable, IIntReceiver{

	protected int[] beamPackets = new int[6];
	protected BeamManager[] beamer;
	protected BeamUnitStorage[] queued = {new BeamUnitStorage(), new BeamUnitStorage()};
	protected long activeCycle;
	protected BeamUnit[] prevMag = new BeamUnit[6];//Stores the last non-null beams sent for information readouts

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
		resetBeamer();
		world.destroyBlock(pos, true);
		world.playSound(null, pos, SoundEvents.BLOCK_GLASS_BREAK, SoundCategory.BLOCKS, 5F, (float) Math.random());
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
		int packet = beamer == null || beamer[index] == null ? 0 : beamer[index].genPacket();
		beamPackets[index] = packet;
		ModPackets.network.sendToAllAround(new SendIntToClient(index, packet, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		if(beamer != null && beamer[index] != null && beamer[index].getLastSent() != null){
			prevMag[index] = beamer[index].getLastSent();
		}
	}
	
	@Override
	public int[] getRenderedBeams(){
		return beamPackets;
	}

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
						beamer[i] = new BeamManager(EnumFacing.byIndex(i), pos);
					}
				}
			}

			BeamUnit out = shiftStorage();
			activeCycle = BeamManager.cycleNumber;
			if(out != null && out.getPower() > getLimit()){
				overload();
				return;
			}
			doEmit(out);
		}
	}

	/**
	 * Moves over the beams in queued from index 1 to 0.
	 * @return The beams previously stored in queued[0].
	 */
	@Nullable
	protected BeamUnit shiftStorage(){
		BeamUnit out = queued[0].getOutput();
		queued[0].clear();
		queued[0].addBeam(queued[1]);
		queued[1].clear();
		markDirty();
		return out;
	}

	protected abstract void doEmit(@Nullable BeamUnit toEmit);

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
	public BeamUnit[] getLastSent(){
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
		queued[0] = BeamUnitStorage.readFromNBT("queue0", nbt);
		queued[1] = BeamUnitStorage.readFromNBT("queue1", nbt);
		activeCycle = nbt.getLong("cyc");

		for(int i = 0; i < 6; i++){
			beamPackets[i] = nbt.getInteger(i + "_beam_packet");
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		return getCapability(cap, side) != null || super.hasCapability(cap, side);
	}

	protected final BeamHandler handler = new BeamHandler();

	@SuppressWarnings("unchecked")
	public <T> T getCapability(Capability<T> cap, EnumFacing dir){
		if(cap == Capabilities.BEAM_CAPABILITY && (dir == null || inputSides()[dir.getIndex()])){
			return (T) handler;
		}
		return super.getCapability(cap, dir);
	}

	protected class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null){
				queued[BeamManager.cycleNumber == activeCycle ? 0 : 1].addBeam(mag);
				markDirty();
			}
		}
	}
}
