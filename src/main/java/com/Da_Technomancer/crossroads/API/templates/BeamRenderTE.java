package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUnitStorage;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public abstract class BeamRenderTE extends TileEntity implements IBeamRenderTE, ITickableTileEntity, IIntReceiver{

	protected int[] beamPackets = new int[6];
	protected BeamManager[] beamer;
	protected BeamUnitStorage[] queued = {new BeamUnitStorage(), new BeamUnitStorage()};
	protected long activeCycle;
	protected BeamUnit[] prevMag = new BeamUnit[] {BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY};//Stores the last non-null beams sent for information readouts

	public BeamRenderTE(TileEntityType<?> type){
		super(type);
	}

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

	@Override
	public AxisAlignedBB getRenderBoundingBox(){
		return INFINITE_EXTENT_AABB;
	}

	/**
	 * Sets the beamer variable to null, use whenever rotating the block. 
	 */
	public void resetBeamer(){
//		refresh();
		beamer = null;
		beamPackets = new int[6];
		for(int i = 0; i < 6; i++){
			prevMag[i] = BeamUnit.EMPTY;
			refreshBeam(i);
		}
		lazyOptional.invalidate();
		lazyOptional = LazyOptional.of(BeamHandler::new);
	}

	protected void refreshBeam(int index){
		int packet = beamer == null || beamer[index] == null ? 0 : beamer[index].genPacket();
		beamPackets[index] = packet;
		CrossroadsPackets.sendPacketAround(world, pos, new SendIntToClient((byte) index, packet, pos));
		if(beamer != null && beamer[index] != null && !beamer[index].getLastSent().isEmpty()){
			prevMag[index] = beamer[index].getLastSent();
		}
	}
	
	@Override
	public int[] getRenderedBeams(){
		return beamPackets;
	}

	@Override
	public void tick(){
		if(world.isRemote){
			return;
		}
		
		if(BeamManager.beamStage == 0){
			if(beamer == null){
				beamer = new BeamManager[6];
				boolean[] outputs = outputSides();
				for(int i = 0; i < 6; i++){
					if(outputs[i]){
						beamer[i] = new BeamManager(Direction.byIndex(i), pos);
					}
				}
			}

			BeamUnit out = shiftStorage();
			activeCycle = BeamManager.cycleNumber;
			if(out.getPower() > getLimit()){
				out = out.mult((float) getLimit() / (float) out.getPower(), true);
			}
			doEmit(out);
		}
	}

	/**
	 * Moves over the beams in queued from index 1 to 0.
	 * @return The beams previously stored in queued[0].
	 */
	@Nonnull
	protected BeamUnit shiftStorage(){
		BeamUnit out = queued[0].getOutput();
		queued[0].clear();
		queued[0].addBeam(queued[1]);
		queued[1].clear();
		markDirty();
		return out;
	}

	protected abstract void doEmit(@Nonnull BeamUnit toEmit);

	@Override
	public void receiveInt(byte identifier, int message, ServerPlayerEntity player){
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
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			if(beamPackets[i] != 0){
				nbt.putInt(i + "_beam_packet", beamPackets[i]);
			}
		}
		return nbt;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);

		queued[0].writeToNBT("queue0", nbt);
		queued[1].writeToNBT("queue1", nbt);
		nbt.putLong("cyc", activeCycle);

		if(beamer != null){
			for(int i = 0; i < 6; i++){
				nbt.putInt(i + "_beam_packet", beamPackets[i]);
			}
		}

		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		queued[0] = BeamUnitStorage.readFromNBT("queue0", nbt);
		queued[1] = BeamUnitStorage.readFromNBT("queue1", nbt);
		activeCycle = nbt.getLong("cyc");

		for(int i = 0; i < 6; i++){
			beamPackets[i] = nbt.getInt(i + "_beam_packet");
		}
	}

	protected LazyOptional<IBeamHandler> lazyOptional = LazyOptional.of(BeamHandler::new);

	@Override
	public void remove(){
		super.remove();
		lazyOptional.invalidate();
	}

//	protected final BeamHandler handler = new BeamHandler();

	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == Capabilities.BEAM_CAPABILITY && (dir == null || inputSides()[dir.getIndex()])){
			return (LazyOptional<T>) lazyOptional;
		}
		return super.getCapability(cap, dir);
	}

	protected class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(!mag.isEmpty()){
				queued[BeamManager.cycleNumber == activeCycle ? 0 : 1].addBeam(mag);
				markDirty();
			}
		}
	}
}
