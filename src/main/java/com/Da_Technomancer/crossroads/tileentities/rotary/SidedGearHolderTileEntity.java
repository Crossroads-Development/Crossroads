package com.Da_Technomancer.crossroads.tileentities.rotary;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.IStringReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.API.packets.SendStringToClient;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.GearTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SidedGearHolderTileEntity extends TileEntity implements ITickable, IStringReceiver, ISpinReceiver{

	private final float[] clientW = new float[6];
	private final float[] angle = new float[6];

	// D-U-N-S-W-E
	// [0]=w, [1]=E, [2]=P, [3]=lastE
	private final double[][] motionData = new double[6][4];

	// D-U-N-S-W-E
	// [0]=m, [1]=I
	private final double[][] physData = new double[6][2];
	private final GearTypes[] members = new GearTypes[6];

	private boolean updateMembers = false;

	public GearTypes[] getMembers(){
		return members;
	}

	public void setMembers(GearTypes type, int side, boolean newTE){
		members[side] = type;
		if(newTE){
			updateMembers = true;
		}else{
			axleHandlers[side].updateStates(true);
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int i = 0; i < 6; i++){
			nbt.setFloat(i + "_clientW", clientW[i]);
			nbt.setFloat(i + "_angle", angle[i]);
			for(int j = 0; j < 4; j++){
				if(motionData[i][j] != 0){
					motionTags.setDouble(i + "," + j + "motion", motionData[i][j]);
				}
			}
		}
		nbt.setTag("motionData", motionTags);

		// members
		NBTTagCompound membTags = new NBTTagCompound();
		for(int i = 0; i < 6; i++){
			if(members[i] != null){
				membTags.setString(i + "memb", members[i].name());
			}
		}
		nbt.setTag("members", membTags);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		// motionData
		NBTTagCompound innerMot = nbt.getCompoundTag("motionData");
		for(int i = 0; i < 6; i++){
			clientW[i] = nbt.getFloat(i + "_clientW");
			angle[i] = nbt.getFloat(i + "_angle");
			for(int j = 0; j < 4; j++){
				motionData[i][j] = innerMot.getDouble(i + "," + j + "motion");
			}
		}

		// members
		NBTTagCompound innerMemb = nbt.getCompoundTag("members");
		for(int i = 0; i < 6; i++){
			members[i] = innerMemb.hasKey(i + "memb") ? GearTypes.valueOf(innerMemb.getString(i + "memb")) : null;
			axleHandlers[i].updateStates(false);
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		NBTTagCompound membTags = new NBTTagCompound();
		for(int i = 0; i < 6; i++){
			nbt.setFloat(i + "_clientW", clientW[i]);
			nbt.setFloat(i + "_angle", angle[i]);
			if(members[i] != null){
				membTags.setString(i + "memb", members[i].name());
			}
		}
		nbt.setTag("members", membTags);
		return nbt;
	}

	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		if(identifier > -1 && identifier < 6){
			this.angle[identifier] = Math.abs(angle - this.angle[identifier]) > 15F ? angle : this.angle[identifier];
			this.clientW[identifier] = clientW;
		}
	}

	@Override
	public void receiveString(String context, String message, @Nullable EntityPlayerMP player){
		if(context.contains("memb")){
			switch(context){
				case "memb0":{
					members[0] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[0].updateStates(false);
					break;
				}
				case "memb1":{
					members[1] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[1].updateStates(false);
					break;
				}
				case "memb2":{
					members[2] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[2].updateStates(false);
					break;
				}
				case "memb3":{
					members[3] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[3].updateStates(false);
					break;
				}
				case "memb4":{
					members[4] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[4].updateStates(false);
					break;
				}
				case "memb5":{
					members[5] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[5].updateStates(false);
					break;
				}
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			for(int i = 0; i < 6; i++){
				// it's 9 / PI instead of 180 / PI because 20 ticks/second
				angle[i] += clientW[i] * 9D / Math.PI;
			}
		}

		if(updateMembers && !world.isRemote){
			for(SidedAxleHandler handler : axleHandlers){
					handler.updateStates(true);
					updateMembers = false;
			}
		}
	}

	private final SidedAxleHandler[] axleHandlers = {new SidedAxleHandler(0), new SidedAxleHandler(1), new SidedAxleHandler(2), new SidedAxleHandler(3), new SidedAxleHandler(4), new SidedAxleHandler(5)};
	private final ICogHandler[] cogHandlers = {new SidedCogHandler(0), new SidedCogHandler(1), new SidedCogHandler(2), new SidedCogHandler(3), new SidedCogHandler(4), new SidedCogHandler(5)};

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if((capability == Capabilities.AXLE_HANDLER_CAPABILITY || capability == Capabilities.COG_HANDLER_CAPABILITY) && facing != null && members[facing.getIndex()] != null){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY){
			if(facing == null || members[facing.getIndex()] == null){
				return null;
			}

			return (T) cogHandlers[facing.getIndex()];
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY){
			if(facing == null || members[facing.getIndex()] == null){
				return null;
			}

			return (T) axleHandlers[facing.getIndex()];
		}
		return super.getCapability(capability, facing);
	}

	private class SidedCogHandler implements ICogHandler{

		private final int side;

		private SidedCogHandler(int sideIn){
			side = sideIn;
		}

		@Override
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius){
			axleHandlers[side].propogate(masterIn, key, rotationRatioIn, lastRadius);
		}

		@Override
		public IAxleHandler getAxle(){
			return axleHandlers[side];
		}
	}

	private class SidedAxleHandler implements IAxleHandler{

		private final int side;
		private byte updateKey;
		private double rotRatio;

		private SidedAxleHandler(int sideIn){
			this.side = sideIn;
		}

		@Override
		public double[] getMotionData(){
			return motionData[side];
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			if(members[side] == null){
				return;
			}

			if(lastRadius != 0){
				rotRatioIn *= -lastRadius / .5D;
			}else if(rotRatioIn == 0){
				rotRatioIn = 1D;
			}else if(EnumFacing.getFront(side).getAxisDirection() == AxisDirection.POSITIVE){
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

			TileEntity sideTE = world.getTileEntity(pos.offset(EnumFacing.getFront(side)));
			if(sideTE != null){
				if(sideTE.hasCapability(Capabilities.AXIS_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite())){
					sideTE.getCapability(Capabilities.AXIS_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite()).trigger(masterIn, key);
				}
				if(sideTE.hasCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite())){
					masterIn.addAxisToList(sideTE.getCapability(Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite()), EnumFacing.getFront(side).getOpposite());
				}
				if(sideTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite())){
					sideTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFront(side).getOpposite()).propogate(masterIn, key, EnumFacing.getFront(side).getAxisDirection() == AxisDirection.POSITIVE ? -rotRatio : rotRatio, 0);
				}
			}

			for(int i = 0; i < 6; i++){
				if(i != side && i != EnumFacing.getFront(side).getOpposite().getIndex() && members[i] != null){
					axleHandlers[i].propogate(masterIn, key, rotRatio, .5D);
				}
			}

			for(int i = 0; i < 6; ++i){
				if(i != side && i != EnumFacing.getFront(side).getOpposite().getIndex()){
					EnumFacing facing = EnumFacing.getFront(i);
					// Adjacent gears
					TileEntity adjTE = world.getTileEntity(pos.offset(facing));
					if(adjTE != null && adjTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, EnumFacing.getFront(side))){
						adjTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, EnumFacing.getFront(side)).connect(masterIn, key, rotRatio, .5D);
					}

					// Diagonal gears
					TileEntity diagTE = world.getTileEntity(pos.offset(facing).offset(EnumFacing.getFront(side)));
					if(diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()) && DefaultAxleHandler.canConnectThrough(world, pos.offset(facing), facing.getOpposite(), EnumFacing.getFront(side))){
						diagTE.getCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite()).connect(masterIn, key, rotRatio, .5D);
					}
				}
			}
		}

		@Override
		public double[] getPhysData(){
			return physData[side];
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				clientW[side] = 0;
				angle[side] = Math.signum(rotRatio) == -1 ? 22.5F : 0F;
				SendSpinToClient msg = new SendSpinToClient(side, clientW[side], angle[side], pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public float getAngle(){
			return angle[side];
		}

		private void updateStates(boolean sendPacket){
			// assume each gear is 1/8 of a cubic meter and has a radius of 1/2
			// meter.
			// mass is rounded to make things nicer for everyone
			if(members[side] == null){
				physData[side][0] = 0;
				physData[side][1] = 0;
				motionData[side][0] = 0;
				motionData[side][1] = 0;
				motionData[side][2] = 0;
				motionData[side][3] = 0;
			}else{
				physData[side][0] = MiscOp.betterRound(members[side].getDensity() / 8, 1);
				// .125 because r*r/2 so .5*.5/2
				physData[side][1] = physData[side][0] * .125D;
			}

			if(sendPacket && !world.isRemote){
				SendStringToClient msg = new SendStringToClient("memb" + side, members[side] == null ? "" : members[side].name(), pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[side][1] += energy;
			}else if(allowInvert){
				motionData[side][1] += energy * Math.signum(motionData[side][1]);
			}else if(absolute){
				int sign = (int) Math.signum(motionData[side][1]);
				motionData[side][1] += energy;
				if(sign != 0 && Math.signum(motionData[side][1]) != sign){
					motionData[side][1] = 0;
				}
			}else{
				int sign = (int) Math.signum(motionData[side][1]);
				motionData[side][1] += energy * ((double) sign);
				if(Math.signum(motionData[side][1]) != sign){
					motionData[side][1] = 0;
				}
			}
			markDirty();
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
		}

		@SideOnly(Side.CLIENT)
		@Override
		public float getNextAngle(){
			return angle[side] + (clientW[side] * 9F / (float) Math.PI);
		}

		@Override
		public boolean shouldManageAngle(){
			return true;
		}

		@Override
		public void setAngle(float angleIn){
			angle[side] = angleIn;
		}

		@Override
		public float getClientW(){
			return clientW[side];
		}

		@Override
		public void syncAngle(){
			clientW[side] = (float) motionData[side][0];
			SendSpinToClient msg = new SendSpinToClient(side, clientW[side], angle[side], pos);
			ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
}
