package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearSlaveTileEntity extends TileEntity implements IIntReceiver{

	private BlockPos masterPos;

	public void setInitial(BlockPos masPos){
		if(world.isRemote){
			return;
		}
		masterPos = masPos;
		long longPos = masterPos.toLong();
		SendIntToClient msg = new SendIntToClient((int) (longPos >> 32), (int) longPos, pos);
		ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
	}

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP sendingPlayer){
		//A BlockPos can be converted to and from a long, AKA 2 ints. The identifier is the first int, the message is the second.
		long longPos = (identifier << 32) | (message & 0xFFFFFFFFL);
		masterPos = BlockPos.fromLong(longPos);
	}

	public void passBreak(EnumFacing side, boolean drop){
		if(masterPos != null && world.getTileEntity(masterPos) instanceof LargeGearMasterTileEntity){
			((LargeGearMasterTileEntity) world.getTileEntity(masterPos)).breakGroup(side, drop);
		}
	}

	private boolean isEdge(){
		if(masterPos != null && masterPos.distanceSq(pos) == 1){
			return true;
		}
		return false;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(masterPos != null){
			nbt.setLong("mast", masterPos.toLong());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		this.masterPos = BlockPos.fromLong(nbt.getLong("mast"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		if(masterPos != null){
			nbt.setLong("mast", masterPos.toLong());
		}
		return nbt;
	}

	private final ICogHandler handler = new CogHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && world.getBlockState(pos).getValue(Properties.FACING) == facing){
			return true;
		}else{
			return super.hasCapability(capability, facing);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && world.getBlockState(pos).getValue(Properties.FACING) == facing){
			return (T) handler;
		}else{
			return super.getCapability(capability, facing);
		}
	}

	private class CogHandler implements ICogHandler{

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
			getAxle().propogate(masterIn, key, rotationRatioIn, lastRadius);
		}

		@Override
		public IAxleHandler getAxle(){
			return world.getTileEntity(masterPos) != null ? world.getTileEntity(masterPos).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, world.getBlockState(pos).getValue(Properties.FACING)) : null;
		}
	}
}
