package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MechanismTileEntity extends TileEntity implements ITickable, ILongReceiver, IInfoTE{

	public static final ArrayList<IMechanism> MECHANISMS = new ArrayList<>(4);//This is a list instead of an array to allow expansion by addons

	static{
		MECHANISMS.add(new MechanismSmallGear());//Index 0, small gear
		MECHANISMS.add(new MechanismAxle());//Index 1, axle
		MECHANISMS.add(new MechanismClutch(false));//Index 2, normal clutch
		MECHANISMS.add(new MechanismClutch(true));//Index 3, inverted clutch
		MECHANISMS.add(new MechanismToggleGear(false));//Index 4, normal toggle gear
		MECHANISMS.add(new MechanismToggleGear(true));//Index 5, inverted toggle gear
	}


	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		int hit = -1;
		for(int i = 0; i < 7; i++){
			if(boundingBoxes[i] != null && boundingBoxes[i].minX <= hitX && boundingBoxes[i].maxX >= hitX && boundingBoxes[i].minY <= hitY && boundingBoxes[i].maxY >= hitY && boundingBoxes[i].minZ <= hitZ && boundingBoxes[i].maxZ >= hitZ){
				hit = i;
				break;
			}
		}

		if(hit == -1){
			return;
		}

		chat.add("Speed: " + MiscUtil.betterRound(motionData[hit][0], 3));
		chat.add("Energy: " + MiscUtil.betterRound(motionData[hit][1], 3));
		chat.add("Power: " + MiscUtil.betterRound(motionData[hit][2], 3));
		chat.add("I: " + inertia[hit] + ", Rotation Ratio: " + axleHandlers[hit].rotRatio);
	}

	// D-U-N-S-W-E-A

	//Public for read-only
	public final IMechanism[] members = new IMechanism[7];
	//Public for read-only
	public final GearFactory.GearMaterial[] mats = new GearFactory.GearMaterial[7];
	// [0]=w, [1]=E, [2]=P, [3]=lastE
	private final double[][] motionData = new double[7][4];
	private final double[] inertia = new double[7];
	//Public for read-only
	private final float[] angle = new float[7];
	//Public for read-only
	private final float[] clientW = new float[7];
	//Public for read-only
	public final AxisAlignedBB[] boundingBoxes = new AxisAlignedBB[7];

	private boolean updateMembers = false;
	//Public for read-only, use setMechanism
	public EnumFacing.Axis axleAxis;
	//Public for read-only
	public double redstoneIn = 0;

	/**
	 * Sets the mechanism in a slot
	 * @param index The index, with 6 being the axle slot. Must be from 0 to 6, inclusive.
	 * @param mechanism The new mechanism. May be null.
	 * @param mat The new material. If mechanism is null, must be null. If mechanism is nonnull, must be nonnull.
	 * @param axis The new axle orientation, if index = 6. Should be null otherwise.
	 * @param newTE Whether this TE is newly created this tick
	 */
	public void setMechanism(int index, @Nullable IMechanism mechanism, @Nullable GearFactory.GearMaterial mat, @Nullable EnumFacing.Axis axis, boolean newTE){
		members[index] = mechanism;
		mats[index] = mat;
		CommonProxy.masterKey++;

		if(index == 6 && axleAxis != axis){
			axleAxis = axis;
			if(!newTE){
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) 14, axis == null ? -1 : axis.ordinal(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		if(newTE){
			updateMembers = true;
		}else{
			axleHandlers[index].updateStates(true);
		}

		markDirty();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		// motionData
		for(int i = 0; i < 7; i++){
			nbt.setFloat("[" + i + "]cl_w", clientW[i]);
			nbt.setFloat("[" + i + "]ang", angle[i]);
			for(int j = 0; j < 4; j++){
				if(motionData[i][j] != 0){
					nbt.setDouble("[" + i + "," + j + "]mot", motionData[i][j]);
				}
			}
		}

		// members
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.setInteger("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.setInteger("[" + i + "]mat", mats[i].getIndex());
			}
		}

		if(members[6] != null && mats[6] != null && axleAxis != null){
			nbt.setInteger("axis", axleAxis.ordinal());
		}

		nbt.setDouble("reds", redstoneIn);

		return nbt;
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.setInteger("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.setInteger("[" + i + "]mat", mats[i].getIndex());

				nbt.setFloat("[" + i + "]cl_w", clientW[i]);
				nbt.setFloat("[" + i + "]ang", angle[i]);
			}
		}

		if(members[6] != null && mats[6] != null && axleAxis != null){
			nbt.setInteger("axis", axleAxis.ordinal());
		}
		nbt.setDouble("reds", redstoneIn);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		if(nbt.hasKey("[6]memb") && nbt.hasKey("[6]mat")){
			axleAxis = EnumFacing.Axis.values()[nbt.getInteger("axis")];
		}

		for(int i = 0; i < 7; i++){
			if(nbt.hasKey("[" + i + "]memb") && nbt.hasKey("[" + i + "]mat")){
				// members
				members[i] = MECHANISMS.get(nbt.getInteger("[" + i + "]memb"));
				if(members[i] == null){
					continue;//Sanity check in case a mechanism type gets removed in the future
				}

				mats[i] = GearFactory.gearMats.get(nbt.getInteger("[" + i + "]mat"));

				// motionData
				clientW[i] = nbt.getFloat("[" + i + "]cl_w");
				angle[i] = nbt.getFloat("[" + i + "]ang");
				for(int j = 0; j < 4; j++){
					motionData[i][j] = nbt.getDouble("[" + i + "," + j + "]mot");
				}

				axleHandlers[i].updateStates(false);
			}
		}

		redstoneIn = nbt.getDouble("reds");
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable EntityPlayerMP sendingPlayer){
		if(identifier >= 0 && identifier < 7){
			float angleIn = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angle[identifier] = Math.abs(angleIn - angle[identifier]) > 15F ? angleIn : angle[identifier];
			clientW[identifier] = Float.intBitsToFloat((int) (message >>> 32L));
		}else if(identifier >= 7 && identifier < 14){
			if(message == -1){
				members[identifier - 7] = null;
				mats[identifier - 7] = null;
			}else{
				members[identifier - 7] = MECHANISMS.get((int) (message & 0xFFFFFFFFL));
				mats[identifier - 7] = GearFactory.gearMats.get((int) (message >>> 32L));
			}
			axleHandlers[identifier - 7].updateStates(false);
		}else if(identifier == 14){
			axleAxis = message == -1 ? null : EnumFacing.Axis.values()[(int) message];
			axleHandlers[6].updateStates(false);
		}else if(identifier == 15){
			redstoneIn = Double.longBitsToDouble(message);
		}
	}

	@Override
	public void update(){
		if(world.isRemote){
			for(int i = 0; i < 7; i++){
				// it's 9 / PI instead of 180 / PI because 20 ticks/second
				angle[i] += clientW[i] * 9D / Math.PI;
			}
		}

		if(updateMembers && !world.isRemote){
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 14, axleAxis == null ? -1 : axleAxis.ordinal(), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			for(int i = 0; i < 7; i++){
				axleHandlers[i].updateStates(true);
			}
			updateMembers = false;
		}
	}

	public void updateRedstone(){
		double reds = RedstoneUtil.getPowerAtPos(world, pos);
		if(reds != redstoneIn){
			markDirty();
			for(int i = 0; i < 7; i++){
				if(members[i] != null){
					members[i].onRedstoneChange(redstoneIn, reds, mats[i], i == 6 ? null : EnumFacing.byIndex(i), axleAxis, motionData[i], this);
				}
			}
			redstoneIn = reds;
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) 15, Double.doubleToLongBits(redstoneIn), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	public double getRedstone(){
		return members[6] != null && axleAxis != null ? members[6].getRatiatorSignal(mats[6], axleAxis, motionData[6], this) : 0;
	}

	protected final SidedAxleHandler[] axleHandlers = {new SidedAxleHandler(0), new SidedAxleHandler(1), new SidedAxleHandler(2), new SidedAxleHandler(3), new SidedAxleHandler(4), new SidedAxleHandler(5), new SidedAxleHandler(6)};
	private final ICogHandler[] cogHandlers = {new SidedCogHandler(0), new SidedCogHandler(1), new SidedCogHandler(2), new SidedCogHandler(3), new SidedCogHandler(4), new SidedCogHandler(5)};
	private final IAdvancedRedstoneHandler redsHandler = new AdvRedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && facing != null){
			return members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this);
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing != null){
			return members[facing.getIndex()] == null && axleAxis == facing.getAxis() ? members[6].hasCap(capability, facing, mats[6], null, axleAxis, this) : members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this);
		}

		return capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.COG_HANDLER_CAPABILITY && facing != null){
			return members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this) ? (T) cogHandlers[facing.getIndex()] : null;
		}
		if(capability == Capabilities.AXLE_HANDLER_CAPABILITY && facing != null){
			return members[facing.getIndex()] == null && axleAxis == facing.getAxis() ? members[6].hasCap(capability, facing, mats[6], null, axleAxis, this) ? (T) axleHandlers[6] : null : members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this) ? (T) axleHandlers[facing.getIndex()] : null;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redsHandler;
		}

		return super.getCapability(capability, facing);
	}

	private class AdvRedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean measure){
			return measure ? getRedstone() : 0;
		}
	}

	private class SidedCogHandler implements ICogHandler{

		private final int side;

		/**
		 * @param sideIn Must be between 0 and 5, inclusive
		 */
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

	protected class SidedAxleHandler implements IAxleHandler{

		private final int side;
		protected byte updateKey;
		protected double rotRatio;

		/**
		 * @param sideIn Must be between 0 and 6, inclusive
		 */
		private SidedAxleHandler(int sideIn){
			this.side = sideIn;
		}

		@Override
		public double[] getMotionData(){
			return motionData[side];
		}

		@Override
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius){
			if(members[side] != null){
				members[side].propogate(mats[side], side == 6 ? null : EnumFacing.byIndex(side), axleAxis, MechanismTileEntity.this, this, masterIn, key, rotRatioIn, lastRadius);
			}
		}

		public double getMoInertia(){
			return inertia[side];
		}

		@Override
		public void resetAngle(){
			if(!world.isRemote){
				clientW[side] = 0;
				angle[side] = Math.signum(rotRatio) == -1 ? 22.5F : 0F;
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) side, (Float.floatToIntBits(angle[side]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(clientW[side]) << 32L), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public float getAngle(){
			return angle[side];
		}

		private void updateStates(boolean sendPacket){
			if(members[side] == null || mats[side] == null){
				inertia[side] = 0;
				motionData[side][0] = 0;
				motionData[side][1] = 0;
				motionData[side][2] = 0;
				motionData[side][3] = 0;
				boundingBoxes[side] = null;
			}else{
				inertia[side] = members[side].getInertia(mats[side], side == 6 ? null : EnumFacing.byIndex(side), axleAxis);
				boundingBoxes[side] = members[side].getBoundingBox(side == 6 ? null : EnumFacing.byIndex(side), axleAxis);
			}

			if(sendPacket && !world.isRemote){
				ModPackets.network.sendToAllAround(new SendLongToClient((byte) (side + 7), members[side] == null ? -1L : (MECHANISMS.indexOf(members[side]) & 0xFFFFFFFFL) | (long) (mats[side].getIndex()) << 32L, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public void markChanged(){
			markDirty();
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
			ModPackets.network.sendToAllAround(new SendLongToClient((byte) side, (Float.floatToIntBits(angle[side]) & 0xFFFFFFFFL) | ((long) Float.floatToIntBits(clientW[side]) << 32L), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
}
