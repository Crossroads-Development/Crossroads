package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearMasterTileEntity extends TileEntity implements IDoubleReceiver, ITickable, IStringReceiver{

	private int ticksExisted = 0;
	private EnumFacing side;
	private GearTypes type;
	private double[] motionData = new double[4];
	private double[] physData = new double[2];
	private boolean borken = false;
	/**
	 * 0: angle, 1: clientW
	 */
	private double[] angleW = new double[2];

	public void initSetup(GearTypes typ, EnumFacing sid){
		side = sid;
		type = typ;

		handlerMain.updateStates();
	}

	public GearTypes getMember(){
		return type;
	}
	
	public EnumFacing getSide(){
		return side;
	}
	
	public void breakGroup(){
		if(borken){
			return;
		}
		borken = true;
		for(int i = -1; i < 2; ++i){
			for(int j = -1; j < 2; ++j){
				worldObj.setBlockToAir(pos.offset(side.getAxis() == Axis.X ? EnumFacing.UP : EnumFacing.EAST, i).offset(side.getAxis() == Axis.Z ? EnumFacing.UP : EnumFacing.NORTH, j));
			}
		}
		worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(GearFactory.largeGears.get(type), 1)));
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			if(angleW[1] == Double.POSITIVE_INFINITY){
				angleW[0] = 0;
			}else if(angleW[1] == Double.NEGATIVE_INFINITY){
				angleW[0] = 22.5;
			}else{
				angleW[0] += angleW[1] * 9D / Math.PI;
			}
		}

		if(!worldObj.isRemote){
			sendWPacket();
		}

		if(++ticksExisted % 200 == 1){
			handlerMain.updateStates();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		// motionData
		NBTTagCompound innerMot = nbt.getCompoundTag("motionData");
		for(int j = 0; j < 4; j++){
			this.motionData[j] = (innerMot.hasKey(j + "motion")) ? innerMot.getDouble(j + "motion") : 0;
		}
		// member
		this.type = nbt.hasKey("memb") ? GearTypes.valueOf(nbt.getString("memb")) : null;

		this.side = EnumFacing.getFront(nbt.getByte("side"));
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int j = 0; j < 3; j++){
			if(motionData[j] != 0)
				motionTags.setDouble(j + "motion", motionData[j]);
		}
		nbt.setTag("motionData", motionTags);

		// member
		if(type != null){
			nbt.setString("memb", type.name());
		}

		nbt.setByte("side", (byte) side.getIndex());

		return nbt;
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(type != null){
			nbt.setString("memb", type.name());
		}
		nbt.setByte("side", (byte) side.getIndex());
		return nbt;
	}

	private final int tiers = ModConfig.speedTiers.getInt();

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("w")){
			angleW[1] = message;
		}
	}

	private void sendWPacket(){
		boolean flag = false;
		if(angleW[1] == Double.POSITIVE_INFINITY || angleW[1] == Double.NEGATIVE_INFINITY){
			flag = true;
		}else if(MiscOp.centerCeil(motionData[0], tiers) != angleW[1]){
			flag = true;
			angleW[1] = MiscOp.centerCeil(motionData[0], tiers);
		}

		if(flag){
			SendDoubleToClient msg = new SendDoubleToClient("w", angleW[1], pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));

			if(angleW[1] == Double.POSITIVE_INFINITY || angleW[1] == Double.NEGATIVE_INFINITY){
				angleW[1] = 0;
			}
		}
	}

	@Override
	public void receiveString(String context, String message){
		switch(context){
			case "memb":
				type = message.equals("") ? null : GearTypes.valueOf(message);
				break;
			case "side":
				side = message.equals("") ? null : EnumFacing.valueOf(message);
				break;
		}
		if(context.equals("memb")){
			type = message.equals("") ? null : GearTypes.valueOf(message);
		}
	}

	private final AxleHandler handlerMain = new AxleHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == side){
			return type != null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing == side){
			return (T) handlerMain;
		}
		return super.getCapability(capability, facing);
	}

	private class AxleHandler implements IAxleHandler{
		
		private byte updateKey;
		private double rotRatio;
		
		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(ITileMasterAxis masterIn, byte key, double rotRatioIn, double lastRadius){
			if(type == null || ticksExisted == 0){
				return;
			}
			
			if(lastRadius != 0){
				rotRatioIn *= -lastRadius / 1.5D;
			}else if(side.getAxisDirection() == AxisDirection.POSITIVE){
				rotRatioIn *= -1D;
			}
			
			//If true, this has already been checked.
			if(key == updateKey){
				//If true, there is rotation conflict.
				if(rotRatio != rotRatioIn){
					masterIn.lock();
				}
				return;
			}
			
			if(masterIn.addToList(this)){
				return;
			}

			rotRatio = rotRatioIn;
			
			if(updateKey == 0){
				resetAngle();
			}
			updateKey = key;

			if(worldObj.getTileEntity(pos.offset(side)) instanceof ITileMasterAxis){
				((ITileMasterAxis) worldObj.getTileEntity(pos.offset(side))).trigger(key, masterIn, side.getOpposite());
			}

			for(EnumFacing sideN : EnumFacing.values()){
				if(sideN != side && sideN != side.getOpposite()){
					// Adjacent gears
					if(worldObj.getTileEntity(pos.offset(sideN, 2)) != null && worldObj.getTileEntity(pos.offset(sideN, 2)).hasCapability(Capabilities.COG_HANDLER_CAPABILITY, side)){
						worldObj.getTileEntity(pos.offset(sideN, 2)).getCapability(Capabilities.COG_HANDLER_CAPABILITY, side).connect(masterIn, key, rotRatio, 1.5D);
					}

					// Diagonal gears
					if(!worldObj.getBlockState(pos.offset(sideN, 2)).getBlock().isNormalCube(worldObj.getBlockState(pos.offset(sideN, 2)), worldObj, pos.offset(sideN, 2)) && worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)) != null && worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)).hasCapability(Capabilities.COG_HANDLER_CAPABILITY, sideN.getOpposite())){
						worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)).getCapability(Capabilities.COG_HANDLER_CAPABILITY, sideN.getOpposite()).connect(masterIn, key, rotRatio, 1.5D);
					}
				}
			}
			
			if(worldObj.getTileEntity(pos.offset(side)) != null && worldObj.getTileEntity(pos.offset(side)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite())){
				worldObj.getTileEntity(pos.offset(side)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side.getOpposite()).propogate(masterIn, key, side.getAxisDirection() == AxisDirection.POSITIVE ? -rotRatio : rotRatio, 0);
			}
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public void resetAngle(){
			if(!worldObj.isRemote){
				angleW[1] = (MiscOp.posOrNeg(rotRatio) == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public double getAngle(){
			return angleW[0];
		}

		private void updateStates(){
			if(!worldObj.isRemote){
				SendStringToClient msg = new SendStringToClient("memb", type == null ? "" : type.name(), pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
				SendStringToClient msgOther = new SendStringToClient("side", side == null ? "" : side.name(), pos);
				ModPackets.network.sendToAllAround(msgOther, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			}

			physData[0] = type == null ? 0 : MiscOp.betterRound(type.getDensity() * 4.5D, 2);
			physData[1] = physData[0] * 1.125D;
			//1.125 because r*r/2 so 1.5*1.5/2
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * MiscOp.posOrNeg(motionData[1]);
			}else if(absolute){
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && MiscOp.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) MiscOp.posOrNeg(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(MiscOp.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}
	}
}
