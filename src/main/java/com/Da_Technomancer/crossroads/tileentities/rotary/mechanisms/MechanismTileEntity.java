package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.packets.ILongReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendLongToClient;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ICogHandler;
import com.Da_Technomancer.crossroads.items.itemSets.GearFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MechanismTileEntity extends TileEntity implements ITickableTileEntity, ILongReceiver, IInfoTE{

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
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
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
//	private final float[] angle = new float[7];
//	private final float[] clientW = new float[7];
	//Public for read-only
	public final AxisAlignedBB[] boundingBoxes = new AxisAlignedBB[7];

	private boolean updateMembers = false;
	//Public for read-only, use setMechanism
	public Direction.Axis axleAxis;
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
	public void setMechanism(int index, @Nullable IMechanism mechanism, @Nullable GearFactory.GearMaterial mat, @Nullable Direction.Axis axis, boolean newTE){
		members[index] = mechanism;
		mats[index] = mat;

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
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);

		// motionData
		for(int i = 0; i < 7; i++){
//			nbt.setFloat("[" + i + "]cl_w", clientW[i]);
//			nbt.setFloat("[" + i + "]ang", angle[i]);
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
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.setInteger("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.setInteger("[" + i + "]mat", mats[i].getIndex());

//				nbt.setFloat("[" + i + "]cl_w", clientW[i]);
//				nbt.setFloat("[" + i + "]ang", angle[i]);
			}
		}

		if(members[6] != null && mats[6] != null && axleAxis != null){
			nbt.setInteger("axis", axleAxis.ordinal());
		}
		nbt.setDouble("reds", redstoneIn);

		return nbt;
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);

		if(nbt.hasKey("[6]memb") && nbt.hasKey("[6]mat")){
			axleAxis = Direction.Axis.values()[nbt.getInteger("axis")];
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
//				clientW[i] = nbt.getFloat("[" + i + "]cl_w");
//				angle[i] = nbt.getFloat("[" + i + "]ang");
				for(int j = 0; j < 4; j++){
					motionData[i][j] = nbt.getDouble("[" + i + "," + j + "]mot");
				}

				axleHandlers[i].updateStates(false);
			}
		}

		redstoneIn = nbt.getDouble("reds");
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayerEntity sendingPlayer){
		/*if(identifier >= 0 && identifier < 7){
			float angleIn = Float.intBitsToFloat((int) (message & 0xFFFFFFFFL));
			angle[identifier] = Math.abs(angleIn - angle[identifier]) > 15F ? angleIn : angle[identifier];
			clientW[identifier] = Float.intBitsToFloat((int) (message >>> 32L));
		}else */if(identifier >= 7 && identifier < 14){
			if(message == -1){
				members[identifier - 7] = null;
				mats[identifier - 7] = null;
			}else{
				members[identifier - 7] = MECHANISMS.get((int) (message & 0xFFFFFFFFL));
				mats[identifier - 7] = GearFactory.gearMats.get((int) (message >>> 32L));
			}
			axleHandlers[identifier - 7].updateStates(false);
		}else if(identifier == 14){
			axleAxis = message == -1 ? null : Direction.Axis.values()[(int) message];
			axleHandlers[6].updateStates(false);
		}else if(identifier == 15){
			redstoneIn = Double.longBitsToDouble(message);
		}
	}

	@Override
	public void update(){
//		if(world.isRemote){
//			for(int i = 0; i < 7; i++){
//				// it's 9 / PI instead of 180 / PI because 20 ticks/second
//				angle[i] += clientW[i] * 9D / Math.PI;
//			}
//		}

		//TODO see if it's possible to make this not a ticking tile entity

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
					members[i].onRedstoneChange(redstoneIn, reds, mats[i], i == 6 ? null : Direction.byIndex(i), axleAxis, motionData[i], this);
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
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing){
		if(capability == Capabilities.COG_CAPABILITY && facing != null){
			return members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this);
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing != null){
			return members[facing.getIndex()] == null && axleAxis == facing.getAxis() ? members[6].hasCap(capability, facing, mats[6], null, axleAxis, this) : members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this);
		}

		return capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY || super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.COG_CAPABILITY && facing != null){
			return members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this) ? (T) cogHandlers[facing.getIndex()] : null;
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing != null){
			return members[facing.getIndex()] == null && axleAxis == facing.getAxis() ? members[6].hasCap(capability, facing, mats[6], null, axleAxis, this) ? (T) axleHandlers[6] : null : members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, axleAxis, this) ? (T) axleHandlers[facing.getIndex()] : null;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
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
		public void connect(IAxisHandler masterIn, byte key, double rotationRatioIn, double lastRadius, Direction cogOrient, boolean renderOffset){
			axleHandlers[side].propogate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);
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
		protected boolean renderOffset;
		protected IAxisHandler axis;

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
		public void propogate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			if(members[side] != null){
				this.renderOffset = renderOffset;
				axis = masterIn;
				members[side].propogate(mats[side], side == 6 ? null : Direction.byIndex(side), axleAxis, MechanismTileEntity.this, this, masterIn, key, rotRatioIn, lastRadius);
			}
		}

		@Override
		public void disconnect(){
			axis = null;
		}

		public double getMoInertia(){
			return inertia[side];
		}

		@Override
		public float getAngle(float partialTicks){
			return axis == null ? 0 : axis.getAngle(rotRatio, partialTicks, renderOffset, 22.5F);
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
				inertia[side] = members[side].getInertia(mats[side], side == 6 ? null : Direction.byIndex(side), axleAxis);
				boundingBoxes[side] = members[side].getBoundingBox(side == 6 ? null : Direction.byIndex(side), axleAxis);
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
	}
}
