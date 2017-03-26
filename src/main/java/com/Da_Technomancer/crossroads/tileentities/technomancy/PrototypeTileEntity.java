package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;

public class PrototypeTileEntity extends TileEntity implements IPrototypeOwner{

	private long chunk;
	public String name;
	
	public void setChunk(long chunk){
		this.chunk = chunk;
		markDirty();
	}
	
	public long getChunk(){
		return chunk;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setLong("chunk", chunk);
		nbt.setString("name", name);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(!world.isRemote){
			chunk = nbt.getLong("chunk");
			name = nbt.getString("name");
			if(!PrototypeWorldSavedData.get(DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID)).prototypeInfo.containsKey(chunk)){
				//In this case, the prototype info is missing and this should self-destruct.
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(side != null){
			WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			PrototypeInfo info = PrototypeWorldSavedData.get(worldDim).prototypeInfo.get(chunk);
			if(info != null && info.ports[side.getIndex()] != null && info.ports[side.getIndex()].getCapability() == cap && info.ports[side.getIndex()].exposeExternal()){
				IPrototypePort port = (IPrototypePort) worldDim.getTileEntity(info.portPos[side.getIndex()]);
				return port != null && port.hasCapPrototype(cap);
			}
		}
		return super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(side != null){
			WorldServer worldDim = DimensionManager.getWorld(ModDimensions.PROTOTYPE_DIM_ID);
			PrototypeInfo info = PrototypeWorldSavedData.get(worldDim).prototypeInfo.get(chunk);
			if(info != null && info.ports[side.getIndex()] != null && info.ports[side.getIndex()].getCapability() == cap && info.ports[side.getIndex()].exposeExternal()){
				IPrototypePort port = (IPrototypePort) worldDim.getTileEntity(info.portPos[side.getIndex()]);
				if(port != null && port.hasCapPrototype(cap)){
					return port.getCapPrototype(cap);
				}
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean hasCap(Capability<?> cap, EnumFacing side){
		return MiscOp.safeHasCap(world, pos.offset(side), cap, side.getOpposite());
	}

	@Override
	public <T> T getCap(Capability<T> cap, EnumFacing side){
		return world.getTileEntity(pos.offset(side)).getCapability(cap, side.getOpposite());
	}
}
