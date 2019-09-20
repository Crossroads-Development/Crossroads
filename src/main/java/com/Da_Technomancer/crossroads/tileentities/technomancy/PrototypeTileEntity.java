package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTEBase;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.blocks.CrossroadsBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PrototypeTileEntity extends BeamRenderTEBase implements IPrototypeOwner, IIntReceiver, ITickableTileEntity{

	private int index = -1;
	public String name = "";
	public String[] tooltips = new String[6];
	//For render side use only.
	private PrototypePortTypes[] ports = new PrototypePortTypes[6];
	private ChunkPos chunk = null;
	private boolean selfDestruct = false;
	private int orient = 0;

	public void setIndex(int index){
		this.index = index;
		markDirty();
	}

	public int getIndex(){
		return index;
	}

	public void rotate(){
		orient++;
		orient %= 4;
		world.markBlockRangeForRenderUpdate(pos, pos);
		for(Direction dir : Direction.VALUES){
			CrossroadsBlocks.prototype.neighborChanged(null, world, pos, CrossroadsBlocks.prototype, pos.offset(dir));
		}
		CrossroadsPackets.network.sendToAllAround(new SendIntToClient((byte) 6, orient, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		refresh();
		markDirty();
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(chunk == null){
			chunk = new ChunkPos(((index % 100) * 2) - 99, (index / 50) - 99);
		}

		PrototypeWorldProvider.tickChunk(chunk.x, chunk.z);
	}

	@Override
	public void onLoad(){
		if(!world.isRemote){
			if(selfDestruct){
				Crossroads.logger.info("Removing an invalid Prototype at " + pos.toString() + ", with index: " + index + ", out of list size: " + PrototypeWorldSavedData.get(false).prototypes.size());
				index = -1;
				world.scheduleUpdate(pos, CrossroadsBlocks.prototype, 1);
			}else if(index != -1){
				ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get(false).prototypes;
				info.get(index).owner = new WeakReference<IPrototypeOwner>(this);
				EventHandlerCommon.updateLoadedPrototypeChunks();
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	public PrototypePortTypes[] getTypes(){
		if(orient == 0){
			return ports;
		}
		PrototypePortTypes[] shifted = new PrototypePortTypes[6];
		System.arraycopy(ports, 0, shifted, 0, 6);
		for(int i = 0; i < orient; i++){
			PrototypePortTypes p = shifted[2];
			shifted[2] = shifted[4];
			shifted[4] = shifted[3];
			shifted[3] = shifted[5];
			shifted[5] = p;
		}
		return shifted;
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("index", index);
		nbt.putString("name", name);
		for(int i = 0; i < 6; i++){
			if(tooltips[i] != null){
				nbt.putString("ttip" + i, tooltips[i]);
			}
			MagHandler h = getHandler(i, false);
			if(h != null){
				nbt.putInt(i + "_beam", beamPackets[i]);
			}
		}

		nbt.putInt("orient", orient);
		return nbt;
	}

	@Override
	public void setWorldCreate(World worldIn){
		setWorld(worldIn);
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		orient = nbt.getInt("orient");
		if(!world.isRemote){
			for(int i = 0; i < 6; i++){
				if(nbt.contains("ttip" + i)){
					tooltips[i] = nbt.getString("ttip" + i);
				}
			}
			index = nbt.getInt("index");
			name = nbt.getString("name");
			ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get(false).prototypes;
			selfDestruct = index == -1 || info.size() < index + 1 || info.get(index) == null;//In this case, the prototype info is missing and this should self-destruct.
		}else{
			for(int i = 0; i < 6; i++){
				if(nbt.contains("port" + i)){
					ports[i] = PrototypePortTypes.valueOf(nbt.getString("port" + i));
				}
			}
			world.markBlockRangeForRenderUpdate(pos, pos);
		}

		for(int i = 0; i < 6; i++){
			if(nbt.contains(i + "_beam")){
				beamPackets[i] = nbt.getInt(i + "_beam");
			}
		}
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		if(index != -1){
			nbt.putInt("orient", orient);
			PrototypePortTypes[] ports = PrototypeWorldSavedData.get(false).prototypes.get(index).ports;
			for(int i = 0; i < 6; i++){
				nbt.putInt(i + "_beam", beamPackets[i]);
				if(ports[i] != null){
					nbt.putString("port" + i, ports[i].name());
				}
			}
		}
		return nbt;
	}

	public Direction adjustSide(Direction dir, boolean reverse){
		if(dir.getAxis() == Direction.Axis.Y){
			return dir;
		}
		Direction adj = dir;
		for(int i = 0; i < orient; i++){
			if(reverse){
				adj = adj.rotateYCCW();
			}else{
				adj = adj.rotateY();
			}
		}
		return adj;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		//No capabilities are found on the render side because that would require the prototype dimension to be loaded on the render side, which it almost certainly won't be.
		if(side != null && index != -1 && !world.isRemote){
			ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			if(worldDim == null){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
				worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			}
			Direction dir = adjustSide(side, true);
			PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
			if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()].getCapability() == cap && info.ports[dir.getIndex()].exposeExternal()){
				BlockPos relPos = info.portPos[dir.getIndex()];
				TileEntity te = worldDim.getTileEntity(info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ()));
				if(!(te instanceof IPrototypePort)){
					return false;
				}
				IPrototypePort port = (IPrototypePort) te;
				return port.hasCapPrototype(cap);
			}
		}
		return super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		//No capabilities are found on the render side because that would require the prototype dimension to be loaded on the render side, which it almost certainly won't be.
		if(side != null && index != -1 && !world.isRemote){
			ServerWorld worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			if(worldDim == null){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
				worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			}
			Direction dir = adjustSide(side, true);
			PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
			if(info != null && info.ports[dir.getIndex()] != null && info.ports[dir.getIndex()].getCapability() == cap && info.ports[dir.getIndex()].exposeExternal()){
				BlockPos relPos = info.portPos[dir.getIndex()];
				IPrototypePort port = (IPrototypePort) worldDim.getTileEntity(info.chunk.getBlock(relPos.getX(), relPos.getY(), relPos.getZ()));
				if(port != null && port.hasCapPrototype(cap)){
					return port.getCapPrototype(cap);
				}
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean hasCap(Capability<?> cap, Direction side){
		Direction dir = adjustSide(side, false);
		if(cap == Capabilities.BEAM_CAPABILITY){
			return true;
		}
		TileEntity te = world.getTileEntity(pos.offset(dir));
		return te != null && !(te instanceof IPrototypeOwner) && te.hasCapability(cap, dir.getOpposite());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCap(Capability<T> cap, Direction side) throws NullPointerException{
		Direction dir = adjustSide(side, false);
		if(cap == Capabilities.BEAM_CAPABILITY){
			return (T) getHandler(dir.getIndex(), true);
		}
		return world.getTileEntity(pos.offset(dir)).getCapability(cap, dir.getOpposite());
	}

	@Override
	public void neighborChanged(Direction fromSide, Block blockIn){
		Direction dir = adjustSide(fromSide, false);
		world.getBlockState(pos.offset(dir)).neighborChanged(world, pos.offset(dir), blockIn, pos);
	}

	private int[] beamPackets = new int[6];

	@Override
	public void receiveInt(byte identifier, int message, @Nullable ServerPlayerEntity sendingPlayer){
		if(identifier < 6 && identifier >= 0){
			beamPackets[identifier] = message;
		}
		if(identifier == 6){
			orient = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public int[] getRenderedBeams(){
		return beamPackets;
	}

	@Nullable
	@Override
	public BeamUnit[] getLastSent(){
		BeamUnit[] out = new BeamUnit[6];
		for(int i = 0; i < 6; i++){
			MagHandler h = getHandler(i, false);
			if(h != null){
				out[i] = h.prevMag;
			}
		}
		return out;
	}

	private void refresh(){
		for(int i = 0; i < 6; i++){
			MagHandler h = getHandler(i, false);
			if(h != null){
				h.beam.emit(null, world);
			}
		}
	}

	/**
	 * @param side 0 <= side < 6
	 * @param create Whether to create a new MagHandler if none currently exist. This is nonnull if true, nullable if false
	 * @return The relevant MagHandler. Go through this instead of magHandlers
	 */
	private MagHandler getHandler(int side, boolean create){
		if(create && magHandlers[side] == null){
			magHandlers[side] = new MagHandler(side);
		}
		return magHandlers[side];
	}

	private final MagHandler[] magHandlers = new MagHandler[6];

	private class MagHandler implements IBeamHandler{

		private final BeamManager beam;
		private BeamUnit prevMag = null;
		private final int side;

		private MagHandler(int side){
			beam = new BeamManager(Direction.byIndex(side), pos);
			this.side = side;
		}

		@Override
		public void setMagic(@Nullable BeamUnit mag){
			if(beam.emit(mag, world)){
				beamPackets[side] = beam.genPacket();
				CrossroadsPackets.network.sendToAllAround(new SendIntToClient((byte) index, beam.genPacket(), pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				if(beam.getLastSent() != null){
					prevMag = beam.getLastSent();
				}
			}
		}
	}
}
