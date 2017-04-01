package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.technomancy.IPrototypePort;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeInfo;
import com.Da_Technomancer.crossroads.API.technomancy.PrototypeWorldSavedData;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class PrototypePortTileEntity extends TileEntity implements IIntReceiver, IPrototypePort{

	private EnumFacing side = EnumFacing.DOWN;
	private PrototypePortTypes type = PrototypePortTypes.HEAT;
	private boolean active;
	private int index;

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("side", side.name());
		nbt.setString("type", type.name());
		nbt.setBoolean("act", active);
		nbt.setInteger("index", index);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		side = nbt.hasKey("side") ? EnumFacing.valueOf(nbt.getString("side")) : EnumFacing.DOWN;
		type = nbt.hasKey("type") ? PrototypePortTypes.valueOf(nbt.getString("type")) : PrototypePortTypes.HEAT;
		active = nbt.getBoolean("act");
		index = nbt.getInteger("index");
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setString("side", side.name());
		nbt.setString("type", type.name());
		return nbt;
	}

	public boolean isUsableByPlayer(EntityPlayer player){
		return world.getTileEntity(pos) == this && player.getDistanceSq(pos.add(0.5, 0.5, 0.5)) <= 64;
	}

	public PrototypePortTypes getType(){
		return type;
	}

	public void setType(PrototypePortTypes type){
		this.type = type;
	}

	public EnumFacing getSide(){
		return side;
	}

	public void setSide(EnumFacing side){
		this.side = side;
	}

	public void makeActive(){
		active = true;
	}

	public boolean isActive(){
		return active;
	}

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("side_type")){
			side = EnumFacing.getFront(message & 7);
			type = PrototypePortTypes.values()[message >> 3];
			markDirty();
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(active && type.getCapability() == cap && type.exposeInternal() && side == this.side){
			PrototypeInfo info = PrototypeWorldSavedData.get(world).prototypes.get(index);
			if(info != null && info.owner != null && info.owner.get() != null && info.owner.get().hasCap(cap, this.side)){
				return true;
			}
		}
		return super.hasCapability(cap, side);
	}

	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(active && type.getCapability() == cap && type.exposeInternal() && side == this.side){
			PrototypeInfo info = PrototypeWorldSavedData.get(world).prototypes.get(index);
			if(info != null && info.owner != null && info.owner.get() != null && info.owner.get().hasCap(cap, this.side)){
				return info.owner.get().getCap(cap, this.side);
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public boolean hasCapPrototype(Capability<?> cap){
		if(active && type.getCapability() == cap){
			TileEntity te = world.getTileEntity(pos.offset(side));
			return te != null && te.hasCapability(cap, side.getOpposite());
		}
		return false;
	}

	@Override
	public <T> T getCapPrototype(Capability<T> cap){
		if(active && type.getCapability() == cap){
			return world.getTileEntity(pos.offset(side)).getCapability(cap, side.getOpposite());
		}
		return null;
	}
}
