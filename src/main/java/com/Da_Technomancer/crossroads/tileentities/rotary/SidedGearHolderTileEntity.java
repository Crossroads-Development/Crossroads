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

public class SidedGearHolderTileEntity extends TileEntity implements ITickable, IStringReceiver, IDoubleReceiver{
	
	private final double[] clientW = new double[6];
	private final double[] angle = new double[6];

	// D-U-N-S-W-E
	// [0]=w, [1]=E, [2]=P, [3]=lastE
	private final double[][] motionData = new double[6][4];

	// D-U-N-S-W-E
	// [0]=m, [1]=I
	private final double[][] physData = new double[6][2];
	private final GearTypes[] members = new GearTypes[6];

	public GearTypes[] getMembers(){
		return members;
	}
	
	public void setMembers(GearTypes type, int side){
		members[side] = type;
		axleHandlers[side].updateStates();
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound){
		super.writeToNBT(compound);

		// motionData
		NBTTagCompound motionTags = new NBTTagCompound();
		for(int i = 0; i < 6; i++){
			for(int j = 0; j < 3; j++){
				if(motionData[i][j] != 0)
					motionTags.setDouble(i + "," + j + "motion", motionData[i][j]);
			}
		}
		compound.setTag("motionData", motionTags);

		// members
		NBTTagCompound membTags = new NBTTagCompound();
		for(int i = 0; i < 6; i++){
			if(members[i] != null){
				membTags.setString(i + "memb", members[i].name());
			}
		}
		compound.setTag("members", membTags);

		return compound;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound){
		super.readFromNBT(compound);

		// motionData
		NBTTagCompound innerMot = compound.getCompoundTag("motionData");
		for(int i = 0; i < 6; i++){
			for(int j = 0; j < 4; j++){
				this.motionData[i][j] = (innerMot.hasKey(i + "," + j + "motion")) ? innerMot.getDouble(i + "," + j + "motion") : 0;
			}
		}

		// members
		NBTTagCompound innerMemb = compound.getCompoundTag("members");
		for(int i = 0; i < 6; i++){
			this.members[i] = innerMemb.hasKey(i + "memb") ? GearTypes.valueOf(innerMemb.getString(i + "memb")) : null;
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
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
	public void receiveDouble(String context, double message){
		if(context.contains("w")){
			switch(context){
				case "w0":{
					clientW[0] = message;
					break;
				}
				case "w1":{
					clientW[1] = message;
					break;
				}
				case "w2":{
					clientW[2] = message;
					break;
				}
				case "w3":{
					clientW[3] = message;
					break;
				}
				case "w4":{
					clientW[4] = message;
					break;
				}
				case "w5":{
					clientW[5] = message;
					break;
				}
			}
		}
	}

	private int ticksExisted = 0;

	private static final int tiers = ModConfig.speedTiers.getInt();

	private void sendWPacket(){
		boolean flag = false;
		for(int i = 0; i < 6; i++){
			if(clientW[i] == Double.POSITIVE_INFINITY || clientW[i] == Double.NEGATIVE_INFINITY){
				flag = true;
			}else if(MiscOp.tiersRound(motionData[i][0], tiers) != clientW[i]){
				flag = true;
				clientW[i] = MiscOp.tiersRound(motionData[i][0], tiers);
			}
		}

		if(flag){
			for(int i = 0; i < 6; ++i){
				SendDoubleToClient msg = new SendDoubleToClient("w" + i, clientW[i], pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}

			for(int i = 0; i < 6; i++){
				if(clientW[i] == Double.POSITIVE_INFINITY || clientW[i] == Double.NEGATIVE_INFINITY){
					clientW[i] = 0;
				}
			}
		}
	}

	@Override
	public void receiveString(String context, String message, EntityPlayerMP player){
		if(context.contains("memb")){
			switch(context){
				case "memb0":{
					members[0] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[0].updateStates();
					break;
				}
				case "memb1":{
					members[1] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[1].updateStates();
					break;
				}
				case "memb2":{
					members[2] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[2].updateStates();
					break;
				}
				case "memb3":{
					members[3] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[3].updateStates();
					break;
				}
				case "memb4":{
					members[4] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[4].updateStates();
					break;
				}
				case "memb5":{
					members[5] = message.equals("") ? null : GearTypes.valueOf(message);
					axleHandlers[5].updateStates();
					break;
				}
			}
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			for(int i = 0; i < 6; i++){
				if(clientW[i] == Double.POSITIVE_INFINITY){
					angle[i] = 0;
				}else if(clientW[i] == Double.NEGATIVE_INFINITY){
					angle[i] = 22.5;
				}else{
					// it's 9 / PI instead of 180 / PI because 20 ticks/second
					angle[i] += clientW[i] * 9D / Math.PI;
				}
			}
		}

		if(!world.isRemote){
			sendWPacket();
		}

		if(++ticksExisted % 200 == 1){
			for(SidedAxleHandler handler : axleHandlers){
				handler.updateStates();
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
			if(members[side] == null || ticksExisted == 0){
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
					if(!world.getBlockState(pos.offset(facing)).isNormalCube() && diagTE != null && diagTE.hasCapability(Capabilities.COG_HANDLER_CAPABILITY, facing.getOpposite())){
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
				clientW[side] = (MiscOp.posOrNeg(rotRatio) == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public double getAngle(){
			return angle[side];
		}

		public void updateStates(){
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

			if(!world.isRemote){
				SendStringToClient msg = new SendStringToClient("memb" + side, members[side] == null ? "" : members[side].name(), pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 256));
			}
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){
			if(allowInvert && absolute){
				motionData[side][1] += energy;
			}else if(allowInvert){
				motionData[side][1] += energy * MiscOp.posOrNeg(motionData[side][1]);
			}else if(absolute){
				int sign = (int) MiscOp.posOrNeg(motionData[side][1]);
				motionData[side][1] += energy;
				if(sign != 0 && MiscOp.posOrNeg(motionData[side][1]) != sign){
					motionData[side][1] = 0;
				}
			}else{
				int sign = (int) MiscOp.posOrNeg(motionData[side][1]);
				motionData[side][1] += energy * ((double) sign);
				if(MiscOp.posOrNeg(motionData[side][1]) != sign){
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
		public double getNextAngle(){
			return Double.isFinite(clientW[side]) ? angle[side] + (clientW[side] * 9D / Math.PI) : angle[side];
		}
	}
}
