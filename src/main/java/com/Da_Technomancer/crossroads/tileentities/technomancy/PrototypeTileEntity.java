package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTEBase;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.tuple.Triple;

import javax.annotation.Nullable;
import java.awt.*;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PrototypeTileEntity extends BeamRenderTEBase implements IPrototypeOwner, IIntReceiver, ITickable{

	private int index = -1;
	public String name = "";
	public String[] tooltips = new String[6];
	//For client side use only.
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
		for(EnumFacing dir : EnumFacing.VALUES){
			ModBlocks.prototype.neighborChanged(null, world, pos, ModBlocks.prototype, pos.offset(dir));
		}
		ModPackets.network.sendToAllAround(new SendIntToClient(6, orient, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
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

		PrototypeWorldProvider.tickChunk(chunk);
	}

	@Override
	public void onLoad(){
		if(!world.isRemote){
			if(selfDestruct){
				Main.logger.info("Removing an invalid Prototype at " + pos.toString() + ", with index: " + index + ", out of list size: " + PrototypeWorldSavedData.get(false).prototypes.size());
				index = -1;
				world.scheduleUpdate(pos, ModBlocks.prototype, 1);
			}else if(index != -1){
				ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get(false).prototypes;
				info.get(index).owner = new WeakReference<IPrototypeOwner>(this);
				EventHandlerCommon.updateLoadedPrototypeChunks();
			}
		}
	}

	@SideOnly(Side.CLIENT)
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

	private int[] memTrip = new int[6];

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("index", index);
		nbt.setString("name", name);
		for(int i = 0; i < 6; i++){
			if(tooltips[i] != null){
				nbt.setString("ttip" + i, tooltips[i]);
			}
			MagHandler h = getHandler(i, false);
			if(h != null){
				nbt.setInteger(i + "_memTrip", h.beam.getPacket());
			}
		}

		nbt.setInteger("orient", orient);
		return nbt;
	}

	@Override
	public void setWorldCreate(World worldIn){
		setWorld(worldIn);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		orient = nbt.getInteger("orient");
		if(!world.isRemote){
			for(int i = 0; i < 6; i++){
				if(nbt.hasKey("ttip" + i)){
					tooltips[i] = nbt.getString("ttip" + i);
				}
				memTrip[i] = nbt.getInteger(i + "memTrip");
			}
			index = nbt.getInteger("index");
			name = nbt.getString("name");
			ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get(false).prototypes;
			selfDestruct = index == -1 || info.size() < index + 1 || info.get(index) == null;//In this case, the prototype info is missing and this should self-destruct.
		}else{
			for(int i = 0; i < 6; i++){
				if(nbt.hasKey("port" + i)){
					ports[i] = PrototypePortTypes.valueOf(nbt.getString("port" + i));
				}
				if(nbt.hasKey(i + "beam")){
					trip[i] = BeamManager.getTriple(nbt.getInteger(i + "beam"));
				}
			}
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(index != -1){
			nbt.setInteger("orient", orient);
			PrototypePortTypes[] ports = PrototypeWorldSavedData.get(false).prototypes.get(index).ports;
			for(int i = 0; i < 6; i++){
				nbt.setInteger(i + "beam", memTrip[i]);
				if(ports[i] != null){
					nbt.setString("port" + i, ports[i].name());
				}
			}
		}
		return nbt;
	}

	public EnumFacing adjustSide(EnumFacing dir, boolean reverse){
		if(dir.getAxis() == EnumFacing.Axis.Y){
			return dir;
		}
		EnumFacing adj = dir;
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
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		//No capabilities are found on the client side because that would require the prototype dimension to be loaded on the client side, which it almost certainly won't be.
		if(side != null && index != -1 && !world.isRemote){
			WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			if(worldDim == null){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
				worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			}
			EnumFacing dir = adjustSide(side, true);
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
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		//No capabilities are found on the client side because that would require the prototype dimension to be loaded on the client side, which it almost certainly won't be.
		if(side != null && index != -1 && !world.isRemote){
			WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			if(worldDim == null){
				DimensionManager.initDimension(ModDimensions.PROTOTYPE_DIM_ID);
				worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			}
			EnumFacing dir = adjustSide(side, true);
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
	public boolean hasCap(Capability<?> cap, EnumFacing side){
		EnumFacing dir = adjustSide(side, false);
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY){
			return true;
		}
		TileEntity te = world.getTileEntity(pos.offset(dir));
		return te != null && !(te instanceof IPrototypeOwner) && te.hasCapability(cap, dir.getOpposite());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCap(Capability<T> cap, EnumFacing side) throws NullPointerException{
		EnumFacing dir = adjustSide(side, false);
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY){
			return (T) getHandler(dir.getIndex(), true);
		}
		return world.getTileEntity(pos.offset(dir)).getCapability(cap, dir.getOpposite());
	}

	@Override
	public void neighborChanged(EnumFacing fromSide, Block blockIn){
		EnumFacing dir = adjustSide(fromSide, false);
		world.getBlockState(pos.offset(dir)).neighborChanged(world, pos.offset(dir), blockIn, pos);
	}

	@SuppressWarnings("unchecked")
	private Triple<Color, Integer, Integer>[] trip = new Triple[6];

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier < 6 && identifier >= 0){
			trip[identifier] = BeamManager.getTriple(message);
		}
		if(identifier == 6){
			orient = message;
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		return trip;
	}

	@Nullable
	@Override
	public MagicUnit[] getLastFullSent(){
		MagicUnit[] out = new MagicUnit[6];
		for(int i = 0; i < 6; i++){
			MagHandler h = getHandler(i, false);
			if(h != null){
				out[i] = h.beam.getLastFullSent();
			}
		}
		return out;
	}

	@Override
	public void refresh(){
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

	private class MagHandler implements IMagicHandler{

		private final BeamManager beam;

		private MagHandler(int side){
			beam = new BeamManager(EnumFacing.getFront(side), pos);
		}

		@Override
		public void setMagic(@Nullable MagicUnit mag){
			beam.emit(mag, world);
		}
	}
}
