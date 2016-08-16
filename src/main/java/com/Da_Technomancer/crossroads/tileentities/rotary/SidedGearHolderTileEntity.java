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

import net.minecraft.nbt.NBTTagCompound;
//import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class SidedGearHolderTileEntity extends TileEntity implements ITickable, IStringReceiver,
		IDoubleReceiver{

	// D-U-N-S-W-E is the order of the sides
	private int[] updateKey = new int[6];

	private double[] clientQ = new double[6];

	private double[] angle = new double[6];

	// D-U-N-S-W-E
	// [0]=w, [1]=E, [2]=P, [3]=lastE
	private double[][] motionData = new double[6][4];

	// D-U-N-S-W-E
	// [0]=r, [1]=m, [2]=I
	private double[][] physData = new double[6][3];

	// This is used for storing what type of gear is on each side. It really
	// exists to store what color to render the gears as and what item to return
	// when broken.
	// In order, down up north south west east.
	private GearTypes[] members = new GearTypes[6];

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
	public void receiveDouble(String context, double message){
		if(context.contains("Q")){
			switch(context){
				case "Q0":{
					sideHandlers[0].setQ(message, true);
					break;
				}
				case "Q1":{
					sideHandlers[1].setQ(message, true);
					break;
				}
				case "Q2":{
					sideHandlers[2].setQ(message, true);
					break;
				}
				case "Q3":{
					sideHandlers[3].setQ(message, true);
					break;
				}
				case "Q4":{
					sideHandlers[4].setQ(message, true);
					break;
				}
				case "Q5":{
					sideHandlers[5].setQ(message, true);
					break;
				}
			}
		}
	}

	private int ticksExisted = 0;

	private final int tiers = ModConfig.getConfigInt(MasterAxis.speedTiers);

	private double[] Q = new double[6];

	private void sendQPacket(){
		boolean flag = false;
		for(int i = 0; i < 6; i++){
			if(clientQ[i] == Double.POSITIVE_INFINITY || clientQ[i] == Double.NEGATIVE_INFINITY){
				flag = true;
			}else if(MiscOperators.centerCeil(Q[i], tiers) * sideHandlers[i].keyType() != clientQ[i]){
				flag = true;
				clientQ[i] = MiscOperators.centerCeil(Q[i], tiers) * sideHandlers[i].keyType();
			}
		}

		if(flag){
			for(int i = 0; i < 6; ++i){
				SendDoubleToClient msg = new SendDoubleToClient("Q" + i, clientQ[i], this.getPos());
				ModPackets.network.sendToAllAround(msg, new TargetPoint(this.getWorld().provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			}

			for(int i = 0; i < 6; i++){
				if(clientQ[i] == Double.POSITIVE_INFINITY || clientQ[i] == Double.NEGATIVE_INFINITY){
					clientQ[i] = 0;
				}
			}
		}
	}

	@Override
	public void receiveString(String context, String message){
		if(context.contains("memb")){
			switch(context){
				case "memb0":{
					members[0] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[0].updateStates();
					break;
				}
				case "memb1":{
					members[1] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[1].updateStates();
					break;
				}
				case "memb2":{
					members[2] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[2].updateStates();
					break;
				}
				case "memb3":{
					members[3] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[3].updateStates();
					break;
				}
				case "memb4":{
					members[4] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[4].updateStates();
					break;
				}
				case "memb5":{
					members[5] = message.equals("") ? null : GearTypes.valueOf(message);
					sideHandlers[5].updateStates();
					break;
				}
			}
		}
	}

	@Override
	public void update(){
		ticksExisted++;

		if(getWorld().isRemote){
			for(int i = 0; i < 6; i++){
				if(clientQ[i] == Double.POSITIVE_INFINITY){
					angle[i] = 0;
				}else if(clientQ[i] == Double.NEGATIVE_INFINITY){
					angle[i] = 22.5;
				}else{
					// it's 18 / PI instead of 180 / PI because 20 ticks /
					// second, so 9 / PI, then * 2 because this is Q not w (Q =
					// r * w, r = .5).
					angle[i] += clientQ[i] * 18 / Math.PI;
				}
			}
		}

		if(!getWorld().isRemote){
			sendQPacket();
		}

		if(ticksExisted % 200 == 1){
			for(IRotaryHandler handler : sideHandlers){
				handler.updateStates();
			}
		}
	}

	private final IRotaryHandler[] sideHandlers = {new SidedRotaryHandler(EnumFacing.DOWN), new SidedRotaryHandler(EnumFacing.UP), new SidedRotaryHandler(EnumFacing.NORTH), new SidedRotaryHandler(EnumFacing.SOUTH), new SidedRotaryHandler(EnumFacing.WEST), new SidedRotaryHandler(EnumFacing.EAST)};

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY && facing != null && members[facing.getIndex()] != null){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.ROTARY_HANDLER_CAPABILITY){
			if(facing == null){
				return null;
			}

			return (T) sideHandlers[facing.getIndex()];
		}
		return super.getCapability(capability, facing);
	}

	private class SidedRotaryHandler implements IRotaryHandler{

		private final int side;

		private SidedRotaryHandler(EnumFacing side){
			this.side = side.getIndex();
		}

		@Override
		public double[] getMotionData(){
			return motionData[side];
		}

		@Override
		public void propogate(int key, ITileMasterAxis masterIn){

			if(members[side] == null || ticksExisted == 0){
				return;
			}

			if(key * -1 == updateKey[side]){
				// If true, then there is a direction conflict.
				masterIn.lock();
				return;
			}else if(key == updateKey[side]){
				// If true, this has already been checked, and should do nothing
				return;
			}
			if(masterIn.addToList(this)){
				return;
			}

			if(updateKey[side] == 0){
				updateKey[side] = key;
				resetAngle();
			}else{
				updateKey[side] = key;
			}

			if(worldObj.getTileEntity(pos.offset(EnumFacing.getFront(side))) instanceof ITileMasterAxis){
				((ITileMasterAxis) worldObj.getTileEntity(pos.offset(EnumFacing.getFront(side)))).trigger(key, masterIn, EnumFacing.getFront(side).getOpposite());
			}

			for(int i = 0; i < 6; i++){
				if(i != side && i != EnumFacing.getFront(side).getOpposite().getIndex() && members[i] != null){
					sideHandlers[i].propogate(key * -1, masterIn);
				}
			}

			for(int i = 0; i < 6; ++i){
				if(i != side && i != EnumFacing.getFront(side).getOpposite().getIndex()){
					EnumFacing facing = EnumFacing.getFront(i);
					// Adjacent gears
					if(getWorld().getTileEntity(pos.offset(facing)) != null && getWorld().getTileEntity(pos.offset(facing)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(side))){
						getWorld().getTileEntity(pos.offset(facing)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, EnumFacing.getFront(side)).propogate(key * -1, masterIn);
					}

					// Diagonal gears
					if(!getWorld().getBlockState(pos.offset(facing)).getBlock().isNormalCube(getWorld().getBlockState(pos.offset(facing)), getWorld(), pos.offset(facing)) && getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.getFront(side))) != null && getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.getFront(side))).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite())){
						getWorld().getTileEntity(pos.offset(facing).offset(EnumFacing.getFront(side))).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite()).propogate(key * -1, masterIn);
					}
				}
			}
		}

		@Override
		public void setMotionData(double[] dataIn){
			motionData[side] = dataIn;
		}

		@Override
		public double[] getPhysData(){
			return physData[side];
		}

		@Override
		public void setPhysData(double[] dataIn){
			physData[side] = dataIn;
		}

		@Override
		public double keyType(){
			return MiscOperators.posOrNeg(updateKey[side]);
		}

		@Override
		public void resetAngle(){
			if(!worldObj.isRemote){
				clientQ[side] = (keyType() == -1 ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY);
			}
		}

		@Override
		public void setQ(double QIn, boolean client){
			if(client){
				clientQ[side] = QIn;
			}else{
				Q[side] = QIn;
			}
		}

		@Override
		public double getAngle(){
			return angle[side];
		}

		@Override
		public void updateStates(){
			// assume each gear is 1/8 of a cubic meter and has a radius of 1/2
			// meter.
			// mass is rounded to make things nicer for everyone

			if(members[side] == null){
				physData[side][0] = 0;
				physData[side][1] = 0;
				physData[side][2] = 0;
				motionData[side][0] = 0;
				motionData[side][1] = 0;
				motionData[side][2] = 0;
				motionData[side][3] = 0;
			}else{
				physData[side][1] = MiscOperators.betterRound(members[side].getDensity() / 8, 1);
				physData[side][0] = .5;
				physData[side][2] = physData[side][1] * .125; /*
																 * .125 because
																 * r*r/2 so
																 * .5*.5/2
																 */
			}

			if(!getWorld().isRemote){
				SendStringToClient msg = new SendStringToClient("memb" + side, members[side] == null ? "" : members[side].name(), pos);
				ModPackets.network.sendToAllAround(msg, new TargetPoint(worldObj.provider.getDimension(), getPos().getX(), getPos().getY(), getPos().getZ(), 512));
			}
		}

		@Override
		public void addEnergy(double energy, boolean allowInvert, boolean absolute){

			if(allowInvert && absolute){
				motionData[side][1] += energy;
			}else if(allowInvert){
				motionData[side][1] += energy * MiscOperators.posOrNeg(motionData[side][1]);
			}else if(absolute){
				int sign = (int) MiscOperators.posOrNeg(motionData[side][1]);
				motionData[side][1] += energy;
				if(sign != 0 && MiscOperators.posOrNeg(motionData[side][1]) != sign){
					motionData[side][1] = 0;
				}
			}else{
				int sign = (int) MiscOperators.posOrNeg(motionData[side][1]);
				motionData[side][1] += energy * ((double) sign);
				if(MiscOperators.posOrNeg(motionData[side][1]) != sign){
					motionData[side][1] = 0;
				}
			}
		}

		@Override
		public void setMember(GearTypes membIn){
			members[side] = membIn;
		}

		@Override
		public GearTypes getMember(){
			return members[side];
		}

	}
}
