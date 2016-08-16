package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOperators;
import com.Da_Technomancer.crossroads.API.enums.GearTypes;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;
import com.Da_Technomancer.crossroads.blocks.rotary.MasterAxis;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class LargeGearMasterTileEntity extends TileEntity implements IDoubleReceiver, ITickable,
		IStringReceiver{

	private int ticksExisted = 0;
	private EnumFacing side;
	private GearTypes type;
	private double[] motionData = new double[4];
	private double[] physData = {1.5, 0, 0};
	private boolean borken = false;
	/**
	 * 0: angle, 1: Q, 2: clientQ
	 */
	private double[] angleQ = new double[3];
	private int updateKey;

	public void initSetup(GearTypes typ, EnumFacing sid){
		side = sid;
		type = typ;

		handlerMain.updateStates();
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
		ticksExisted++;

		if(getWorld().isRemote){
			if(angleQ[2] == Double.POSITIVE_INFINITY){
				angleQ[0] = 0;
			}else if(angleQ[2] == Double.NEGATIVE_INFINITY){
				angleQ[0] = 22.5;
			}else{
				angleQ[0] += angleQ[2] * 9 / (physData[0] * Math.PI);
			}
		}

		if(!getWorld().isRemote){
			sendQPacket();
		}

		if(ticksExisted % 200 == 1){
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

	private final int tiers = ModConfig.getConfigInt(MasterAxis.speedTiers);

	@Override
	public void receiveDouble(String context, double message){
		if(context.equals("Q")){
			handlerMain.setQ(message, true);
		}
	}

	private void sendQPacket(){
		boolean flag = false;
		if(angleQ[2] == Double.POSITIVE_INFINITY || angleQ[2] == Double.NEGATIVE_INFINITY){
			flag = true;
		}else if(MiscOperators.centerCeil(angleQ[1], tiers) * handlerMain.keyType() != angleQ[2]){
			flag = true;
			angleQ[2] = MiscOperators.centerCeil(angleQ[1], tiers) * handlerMain.keyType();
		}

		if(flag){
			SendDoubleToClient msg = new SendDoubleToClient("Q", angleQ[2], this.getPos());
			ModPackets.network.sendToAllAround(msg, new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));

			if(angleQ[2] == Double.POSITIVE_INFINITY || angleQ[2] == Double.NEGATIVE_INFINITY){
				angleQ[2] = 0;
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

	private final GearHandler handlerMain = new GearHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY && (facing == null || facing == side)){
			return type != null;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY && (facing == null || facing == side)){
			return (T) handlerMain;
		}
		return super.getCapability(capability, facing);
	}

	private class GearHandler implements IRotaryHandler{

		@Override
		public double[] getMotionData(){
			return motionData;
		}

		@Override
		public void propogate(int key, ITileMasterAxis masterIn){
			if(type == null || ticksExisted == 0){
				return;
			}

			if(key * -1 == updateKey){
				masterIn.lock();
				return;
			}else if(key == updateKey){
				return;
			}

			if(masterIn.addToList(handlerMain)){
				return;
			}

			if(updateKey == 0){
				updateKey = key;
				resetAngle();
			}else{
				updateKey = key;
			}

			if(worldObj.getTileEntity(pos.offset(side)) instanceof ITileMasterAxis){
				((ITileMasterAxis) worldObj.getTileEntity(pos.offset(side))).trigger(key, masterIn, side.getOpposite());
			}

			for(EnumFacing sideN : EnumFacing.values()){
				if(sideN != side && sideN != side.getOpposite()){
					// Adjacent gears
					if(worldObj.getTileEntity(pos.offset(sideN, 2)) != null && worldObj.getTileEntity(pos.offset(sideN, 2)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side)){
						worldObj.getTileEntity(pos.offset(sideN, 2)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, side).propogate(key * -1, masterIn);
					}

					// Diagonal gears
					if(!worldObj.getBlockState(pos.offset(sideN, 2)).getBlock().isNormalCube(worldObj.getBlockState(pos.offset(sideN, 2)), worldObj, pos.offset(sideN, 2)) && worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)) != null && worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, sideN.getOpposite())){
						worldObj.getTileEntity(pos.offset(sideN, 2).offset(side)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, sideN.getOpposite()).propogate(key * -1, masterIn);
					}
				}
			}
		}

		@Override
		public void setMotionData(double[] dataIn){
			motionData = dataIn;
		}

		@Override
		public double[] getPhysData(){
			return physData;
		}

		@Override
		public void setPhysData(double[] dataIn){
			physData = dataIn;
		}

		@Override
		public double keyType(){
			return MiscOperators.posOrNeg(updateKey);
		}

		@Override
		public void resetAngle(){
			if(!worldObj.isRemote){
				angleQ[2] = (keyType() == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public void setQ(double QIn, boolean client){
			if(client){
				angleQ[2] = QIn;
			}else{
				angleQ[1] = QIn;
			}
		}

		@Override
		public double getAngle(){
			return angleQ[0];
		}

		@Override
		public void updateStates(){
			if(!getWorld().isRemote){
				SendStringToClient msg = new SendStringToClient("memb", type == null ? "" : type.name(), pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
				SendStringToClient msgOther = new SendStringToClient("side", side == null ? "" : side.name(), pos);
				ModPackets.network.sendToAllAround(msgOther, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			}

			physData[1] = type == null ? 0 : MiscOperators.betterRound(type.getDensity() * 4.5D, 2);
			physData[2] = physData[1] * 1.125; /*
												 * 1.125 because r*r/2 so
												 * 1.5*1.5/2
												 */
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[1] += energy;
			}else if(allowInvert){
				motionData[1] += energy * MiscOperators.posOrNeg(motionData[1]);
			}else if(absolute){
				int sign = (int) MiscOperators.posOrNeg(motionData[1]);
				motionData[1] += energy;
				if(sign != 0 && MiscOperators.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}else{
				int sign = (int) MiscOperators.posOrNeg(motionData[1]);
				motionData[1] += energy * ((double) sign);
				if(MiscOperators.posOrNeg(motionData[1]) != sign){
					motionData[1] = 0;
				}
			}
		}

		@Override
		public void setMember(GearTypes membIn){
			type = membIn;
		}

		@Override
		public GearTypes getMember(){
			return type;
		}
	}
}
