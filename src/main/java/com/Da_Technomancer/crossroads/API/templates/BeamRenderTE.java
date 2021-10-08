package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.*;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.ambient.sounds.CRSounds;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public abstract class BeamRenderTE extends BlockEntity implements IBeamRenderTE, TickableBlockEntity, IIntReceiver{

	protected int[] beamPackets = new int[6];
	protected BeamManager[] beamer;
	protected BeamUnitStorage[] queued = {new BeamUnitStorage(), new BeamUnitStorage()};
	protected long activeCycle;//To prevent tick acceleration and deal with some chunk loading weirdness
	protected BeamUnit[] prevMag = new BeamUnit[] {BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY, BeamUnit.EMPTY};//Stores the last non-null beams sent for information readouts

	public BeamRenderTE(BlockEntityType<?> type){
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
		return BeamUtil.POWER_LIMIT;
	}

	@Override
	public AABB getRenderBoundingBox(){
		//Expand the render box to include all possible beams from this block
		return new AABB(worldPosition.offset(-BeamUtil.MAX_DISTANCE, -BeamUtil.MAX_DISTANCE, -BeamUtil.MAX_DISTANCE), worldPosition.offset(1 + BeamUtil.MAX_DISTANCE, 1 + BeamUtil.MAX_DISTANCE, 1 + BeamUtil.MAX_DISTANCE));
	}

	@Override
	public void clearCache(){
		super.clearCache();
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
		if(!level.isClientSide){
			CRPackets.sendPacketAround(level, worldPosition, new SendIntToClient((byte) index, packet, worldPosition));
		}
		if(beamer != null && beamer[index] != null && !beamer[index].getLastSent().isEmpty()){
			prevMag[index] = beamer[index].getLastSent();
		}
	}
	
	@Override
	public int[] getRenderedBeams(){
		return beamPackets;
	}

	protected void playSounds(){
		//Can be called on both the virtual server and client side, but only actually does anything on the server side as the passed player is null
		if(CRConfig.beamSounds.get() && beamer != null && level.getGameTime() % 60 == 0){
			//Play a sound if ANY side is outputting a beam
			for(BeamManager beamManager : beamer){
				if(beamManager != null && !beamManager.getLastSent().isEmpty()){
					//The attenuation distance defined for this sound in sounds.json is significant, and makes the sound have a very short range
					CRSounds.playSoundServer(level, worldPosition, CRSounds.BEAM_PASSIVE, SoundSource.BLOCKS, 0.7F, 0.3F);
					break;
				}
			}
		}
	}

	@Override
	public void tick(){
		if(level.isClientSide){
			return;
		}
		
		if(level.getGameTime() % BeamUtil.BEAM_TIME == 0 && activeCycle != level.getGameTime()){
			if(beamer == null){
				beamer = new BeamManager[6];
				boolean[] outputs = outputSides();
				for(int i = 0; i < 6; i++){
					if(outputs[i]){
						beamer[i] = new BeamManager(Direction.from3DDataValue(i), worldPosition);
					}
				}
			}

			BeamUnit out = shiftStorage();
			activeCycle = level.getGameTime();
			if(out.getPower() > getLimit()){
				out = out.mult((float) getLimit() / (float) out.getPower(), true);
			}
			doEmit(out);
		}

		playSounds();
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
		setChanged();
		return out;
	}

	protected abstract void doEmit(@Nonnull BeamUnit toEmit);

	@Override
	public void receiveInt(byte identifier, int message, ServerPlayer player){
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
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		for(int i = 0; i < 6; i++){
			if(beamPackets[i] != 0){
				nbt.putInt(i + "_beam_packet", beamPackets[i]);
			}
		}
		return nbt;
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);

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
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		queued[0] = BeamUnitStorage.readFromNBT("queue0", nbt);
		queued[1] = BeamUnitStorage.readFromNBT("queue1", nbt);
		activeCycle = nbt.getLong("cyc");

		for(int i = 0; i < 6; i++){
			beamPackets[i] = nbt.getInt(i + "_beam_packet");
		}
	}

	protected LazyOptional<IBeamHandler> lazyOptional = LazyOptional.of(BeamHandler::new);

	@Override
	public void setRemoved(){
		super.setRemoved();
		lazyOptional.invalidate();
		if(beamer != null && level != null){
			for(BeamManager manager : beamer){
				if(manager != null){
					manager.emit(BeamUnit.EMPTY, level);
				}
			}
		}
	}

//	protected final BeamHandler handler = new BeamHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == Capabilities.BEAM_CAPABILITY && (dir == null || inputSides()[dir.get3DDataValue()])){
			return (LazyOptional<T>) lazyOptional;
		}
		return super.getCapability(cap, dir);
	}

	protected class BeamHandler implements IBeamHandler{

		@Override
		public void setBeam(@Nonnull BeamUnit mag){
			if(!mag.isEmpty()){
				queued[level.getGameTime() == activeCycle ? 0 : 1].addBeam(mag);
				setChanged();
			}
		}
	}
}
