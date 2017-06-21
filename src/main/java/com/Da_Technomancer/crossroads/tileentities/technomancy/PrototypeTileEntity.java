package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.Da_Technomancer.crossroads.EventHandlerCommon;
import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypeOwner;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;
import com.Da_Technomancer.crossroads.dimensions.ModDimensions;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PrototypeTileEntity extends TileEntity implements IPrototypeOwner{

	private int index = -1;
	public String name = "";
	//For client side use only.
	private PrototypePortTypes[] ports = new PrototypePortTypes[6];
	
	public void setIndex(int index){
		this.index = index;
		markDirty();
	}
	
	public int getIndex(){
		return index;
	}

	@Override
	public void onLoad(){
		if(!world.isRemote && index != -1){
			ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get().prototypes;
			info.get(index).owner = new WeakReference<IPrototypeOwner>(this);
			EventHandlerCommon.updateLoadedPrototypeChunks();
		}
	}

	@Override
	public void onChunkUnload(){
		if(!world.isRemote && index != -1){
			ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get().prototypes;
			info.get(index).owner = null;
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
			index = nbt.getInteger("index");
			name = nbt.getString("name");
			ArrayList<PrototypeInfo> info = PrototypeWorldSavedData.get().prototypes;
			if(info.size() < index + 1 || info.get(index) == null){
				//In this case, the prototype info is missing and this should self-destruct.
				world.setBlockState(pos, Blocks.AIR.getDefaultState());
			}
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
			PrototypePortTypes[] ports = PrototypeWorldSavedData.get().prototypes.get(index).ports;
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
			PrototypeInfo info = PrototypeWorldSavedData.get().prototypes.get(index);
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
			PrototypeInfo info = PrototypeWorldSavedData.get().prototypes.get(index);
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
	public boolean loadTick(){
		return false;
	}

	@Override
	public void neighborChanged(EnumFacing fromSide, Block blockIn){
		world.getBlockState(pos.offset(fromSide)).neighborChanged(world, pos.offset(fromSide), blockIn, pos);
	}
}
