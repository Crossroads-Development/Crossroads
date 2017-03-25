package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.enums.PrototypePortTypes;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class PrototypePortTileEntity extends TileEntity implements IIntReceiver{

	private EnumFacing side = EnumFacing.DOWN;
	private PrototypePortTypes type = PrototypePortTypes.HEAT;
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("side", side.name());
		nbt.setString("type", type.name());
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		side = nbt.hasKey("side") ? EnumFacing.valueOf(nbt.getString("side")) : EnumFacing.DOWN;
		type = nbt.hasKey("type") ? PrototypePortTypes.valueOf(nbt.getString("type")) : PrototypePortTypes.HEAT;
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

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("side_type")){
			side = EnumFacing.getFront(message & 7);
			type = PrototypePortTypes.values()[message >> 3];
			markDirty();
		}
	}
}
