package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class LargeGearSlaveTileEntity extends TileEntity implements IIntReceiver, IInfoTE{

	public BlockPos masterPos;//Defined relative to this block's position

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		IAxleHandler axle = handler.getAxle();
		if(axle == null){
			return;
		}

		chat.add("Speed: " + MiscUtil.betterRound(axle.getMotionData()[0], 3));
		chat.add("Energy: " + MiscUtil.betterRound(axle.getMotionData()[1], 3));
		chat.add("Power: " + MiscUtil.betterRound(axle.getMotionData()[2], 3));
		chat.add("I: " + axle.getMoInertia() + ", Rotation Ratio: " + axle.getRotationRatio());
	}

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
		long longPos = ((long) identifier << 32L) | (message & 0xFFFFFFFFL);
		masterPos = BlockPos.fromLong(longPos);
	}

	public void passBreak(EnumFacing side, boolean drop){
		if(masterPos != null){
			TileEntity te = world.getTileEntity(pos.add(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				((LargeGearMasterTileEntity) te).breakGroup(side, drop);
			}
		}
	}

	private boolean isEdge(){
		return masterPos != null && masterPos.distanceSq(BlockPos.ORIGIN) == 1;
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
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && world.getBlockState(pos).getValue(EssentialsProperties.FACING) == facing){
			return true;
		}else{
			return super.hasCapability(capability, facing);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && isEdge() && world.getBlockState(pos).getValue(EssentialsProperties.FACING) == facing){
			return (T) handler;
		}else{
			return super.getCapability(capability, facing);
		}
	}

	private class CogHandler implements ICogHandler{

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, EnumFacing cogOrient, boolean renderOffset){
			if(cogOrient == EnumFacing.getFacingFromVector(-masterPos.getX(), -masterPos.getY(), -masterPos.getZ())){
				getAxle().propogate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);
			}
		}

		@Override
		public IAxleHandler getAxle(){
			TileEntity te = world.getTileEntity(pos.add(masterPos));
			if(te instanceof LargeGearMasterTileEntity){
				return te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, world.getBlockState(pos).getValue(EssentialsProperties.FACING));
			}
			return null;
		}
	}
}
