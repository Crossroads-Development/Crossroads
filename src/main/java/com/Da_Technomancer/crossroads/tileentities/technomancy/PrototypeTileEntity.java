package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.Main;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypePortTypes;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldProvider;
import com.Da_Technomancer.crossroads.dimensions.PrototypeWorldSavedData;

import net.minecraft.block.Block;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypeTileEntity extends TileEntity implements IPrototypeOwner, ITickable{

	private int index = -1;
	public String name = "";
	public String[] tooltips = new String[6];
	//For client side use only.
	private PrototypePortTypes[] ports = new PrototypePortTypes[6];
	private ChunkPos chunk = null;
	private boolean selfDestruct = false;

	public void setIndex(int index){
		this.index = index;
		markDirty();
	}

	public int getIndex(){
		return index;
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
		return ports;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("index", index);
		nbt.setString("name", name);
		for(int i = 0; i < 6; i++){
			if(tooltips[i] != null){
				nbt.setString("ttip" + i, tooltips[i]);
			}
		}
		return nbt;
	}

	@Override
	public void setWorldCreate(World worldIn){
		setWorld(worldIn);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(!world.isRemote){
			for(int i = 0; i < 6; i++){
				if(nbt.hasKey("ttip" + i)){
					tooltips[i] = nbt.getString("ttip" + i);
				}
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
			}
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(index != -1){
			PrototypePortTypes[] ports = PrototypeWorldSavedData.get(false).prototypes.get(index).ports;
			for(int i = 0; i < 6; i++){
				if(ports[i] != null){
					nbt.setString("port" + i, ports[i].name());
				}
			}
		}
		return nbt;
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
			PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
			if(info != null && info.ports[side.getIndex()] != null && info.ports[side.getIndex()].getCapability() == cap && info.ports[side.getIndex()].exposeExternal()){
				BlockPos relPos = info.portPos[side.getIndex()];
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
			PrototypeInfo info = PrototypeWorldSavedData.get(true).prototypes.get(index);
			if(info != null && info.ports[side.getIndex()] != null && info.ports[side.getIndex()].getCapability() == cap && info.ports[side.getIndex()].exposeExternal()){
				BlockPos relPos = info.portPos[side.getIndex()];
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
		TileEntity te = world.getTileEntity(pos.offset(side));
		return te != null && !(te instanceof IPrototypeOwner) && te.hasCapability(cap, side.getOpposite());
	}

	@Override
	public <T> T getCap(Capability<T> cap, EnumFacing side){
		return world.getTileEntity(pos.offset(side)).getCapability(cap, side.getOpposite());
	}

	@Override
	public void neighborChanged(EnumFacing fromSide, Block blockIn){
		world.getBlockState(pos.offset(fromSide)).neighborChanged(world, pos.offset(fromSide), blockIn, pos);
	}
}
