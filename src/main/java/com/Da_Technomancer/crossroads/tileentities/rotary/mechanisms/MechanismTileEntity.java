package com.Da_Technomancer.crossroads.tileentities.rotary.mechanisms;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.rotary.Mechanism;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import com.Da_Technomancer.essentials.packets.ILongReceiver;
import com.Da_Technomancer.essentials.packets.SendLongToClient;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

//@ObjectHolder(Crossroads.MODID)
public class MechanismTileEntity extends TileEntity implements ITickableTileEntity, ILongReceiver, IInfoTE{

	@ObjectHolder(Crossroads.MODID + ":mechanism")
	public static TileEntityType<MechanismTileEntity> type = null;

	public static final ArrayList<IMechanism<?>> MECHANISMS = new ArrayList<>(8);//This is a list instead of an array to allow expansion by addons

	static{
		MECHANISMS.add(new MechanismSmallGear());//Index 0, small gear
		MECHANISMS.add(new MechanismAxle());//Index 1, axle
		MECHANISMS.add(new MechanismClutch(false));//Index 2, normal clutch
		MECHANISMS.add(new MechanismClutch(true));//Index 3, inverted clutch
		MECHANISMS.add(new MechanismToggleGear(false));//Index 4, normal toggle gear
		MECHANISMS.add(new MechanismToggleGear(true));//Index 5, inverted toggle gear
		MECHANISMS.add(new MechanismAxleMount());//Index 6, axle mount
		MECHANISMS.add(new MechanismFacade());//Index 7, facades
	}

	public MechanismTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		int part = -1;
		Vector3d hitVec = hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ());//Subtract position, as the VoxelShapes are defined relative to position
		for(int i = 0; i < 7; i++){
			if(boundingBoxes[i] != null && Mechanism.voxelContains(boundingBoxes[i], hitVec)){
				part = i;
				break;
			}
		}

		if(part == -1){
			return;
		}

		RotaryUtil.addRotaryInfo(chat, motionData[part], inertia[part], axleHandlers[part].rotRatio, false);
	}

	// D-U-N-S-W-E-A

	//Public for read-only
	public final IMechanism<?>[] members = new IMechanism[7];
	//Public for read-only
	public final IMechanismProperty[] mats = new IMechanismProperty[7];
	// [0]=w, [1]=E, [2]=P, [3]=lastE
	private final double[][] motionData = new double[7][4];
	private final double[] inertia = new double[7];
//	private final float[] angle = new float[7];
//	private final float[] clientW = new float[7];
	//Public for read-only
	public final VoxelShape[] boundingBoxes = new VoxelShape[7];

	private boolean updateMembers = false;
	private Direction.Axis axleAxis;
	//Public for read-only
	public int redstoneIn = 0;

	/**
	 * Sets the mechanism in a slot
	 * @param index The index, with 6 being the axle slot. Must be from 0 to 6, inclusive.
	 * @param mechanism The new mechanism. May be null.
	 * @param mat The new material. If mechanism is null, must be null. If mechanism is nonnull, must be nonnull.
	 * @param axis The new axle orientation, if index = 6. Should be null otherwise.
	 * @param newTE Whether this TE is newly created this tick
	 */
	public void setMechanism(int index, @Nullable IMechanism<?> mechanism, @Nullable IMechanismProperty mat, @Nullable Direction.Axis axis, boolean newTE){
		members[index] = mechanism;
		mats[index] = mat;
		if(index == 6 && getAxleAxis() != axis){
			axleAxis = axis;
			if(!newTE && !world.isRemote){
				CRPackets.sendPacketAround(world, pos, new SendLongToClient(14, axis == null ? -1 : axis.ordinal(), pos));
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
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);

		// motionData
		for(int i = 0; i < 7; i++){
//			nbt.putFloat("[" + i + "]cl_w", clientW[i]);
//			nbt.putFloat("[" + i + "]ang", angle[i]);
			for(int j = 0; j < 4; j++){
				if(motionData[i][j] != 0){
					nbt.putDouble("[" + i + "," + j + "]mot", motionData[i][j]);
				}
			}
		}

		// members
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.putInt("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.putString("[" + i + "]mat", mats[i].getSaveName());
			}
		}

		if(members[6] != null && mats[6] != null && getAxleAxis() != null){
			nbt.putInt("axis", getAxleAxis().ordinal());
		}

		nbt.putInt("reds", redstoneIn);

		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.putInt("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.putString("[" + i + "]mat", mats[i].getSaveName());

//				nbt.putFloat("[" + i + "]cl_w", clientW[i]);
//				nbt.putFloat("[" + i + "]ang", angle[i]);
			}
		}

		if(members[6] != null && mats[6] != null && getAxleAxis() != null){
			nbt.putInt("axis", getAxleAxis().ordinal());
		}
		nbt.putInt("reds", redstoneIn);

		return nbt;
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);

		if(nbt.contains("[6]memb") && nbt.contains("[6]mat")){
			axleAxis = Direction.Axis.values()[nbt.getInt("axis")];
		}else{
			axleAxis = null;
		}

		for(int i = 0; i < 7; i++){
			if(nbt.contains("[" + i + "]memb") && nbt.contains("[" + i + "]mat")){
				// members
				members[i] = MECHANISMS.get(nbt.getInt("[" + i + "]memb"));
				if(members[i] == null){
					continue;//Sanity check in case a mechanism type gets removed in the future
				}

				mats[i] = members[i].loadProperty(nbt.getString("[" + i + "]mat"));

				// motionData
//				clientW[i] = nbt.getFloat("[" + i + "]cl_w");
//				angle[i] = nbt.getFloat("[" + i + "]ang");
				for(int j = 0; j < 4; j++){
					motionData[i][j] = nbt.getDouble("[" + i + "," + j + "]mot");
				}

				axleHandlers[i].updateStates(false);
			}
		}

		redstoneIn = nbt.getInt("reds");
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
				mats[identifier - 7] = members[identifier - 7].deserializeProperty((int) (message >>> 32L));
			}
			axleHandlers[identifier - 7].updateStates(false);
		}else if(identifier == 14){
			axleAxis = message == -1 ? null : Direction.Axis.values()[(int) message];
			axleHandlers[6].updateStates(false);
		}else if(identifier == 15){
			redstoneIn = (int) message;
		}
	}

	@Override
	public void tick(){
		//functionality moved to master-axis
//		if(world.isRemote){
//			for(int i = 0; i < 7; i++){
//				// it's 9 / PI instead of 180 / PI because 20 ticks/second
//				angle[i] += clientW[i] * 9D / Math.PI;
//			}
//		}

		if(updateMembers && !world.isRemote){
			CRPackets.sendPacketAround(world, pos, new SendLongToClient(14, getAxleAxis() == null ? -1 : getAxleAxis().ordinal(), pos));
			for(int i = 0; i < 7; i++){
				axleHandlers[i].updateStates(true);
			}
			updateMembers = false;
		}
	}

	public void updateRedstone(){
		int reds = RedstoneUtil.getRedstoneAtPos(world, pos);
		if(reds != redstoneIn){
			markDirty();
			for(int i = 0; i < 7; i++){
				if(members[i] != null){
					members[i].onRedstoneChange(redstoneIn, reds, mats[i], i == 6 ? null : Direction.byIndex(i), getAxleAxis(), motionData[i], this);
				}
			}
			redstoneIn = reds;
			CRPackets.sendPacketAround(world, pos, new SendLongToClient(15, (long) redstoneIn, pos));
		}
	}

	public float getRedstone(){
		return members[6] != null && getAxleAxis() != null ? (float) members[6].getCircuitSignal(mats[6], getAxleAxis(), motionData[6], this) : 0;
	}

	//Direct access to the axle handlers is needed
	protected final SidedAxleHandler[] axleHandlers = {new SidedAxleHandler(0), new SidedAxleHandler(1), new SidedAxleHandler(2), new SidedAxleHandler(3), new SidedAxleHandler(4), new SidedAxleHandler(5), new SidedAxleHandler(6)};

	@SuppressWarnings("unchecked")
	private final LazyOptional<IAxleHandler>[] axleOpts = new LazyOptional[] {LazyOptional.of(() -> axleHandlers[0]), LazyOptional.of(() -> axleHandlers[1]), LazyOptional.of(() -> axleHandlers[2]), LazyOptional.of(() -> axleHandlers[3]), LazyOptional.of(() -> axleHandlers[4]), LazyOptional.of(() -> axleHandlers[5]), LazyOptional.of(() -> axleHandlers[6])};
	@SuppressWarnings("unchecked")
	private final LazyOptional<ICogHandler>[] cogOpts = new LazyOptional[] {LazyOptional.of(() -> new SidedCogHandler(0)), LazyOptional.of(() -> new SidedCogHandler(1)), LazyOptional.of(() -> new SidedCogHandler(2)), LazyOptional.of(() -> new SidedCogHandler(3)), LazyOptional.of(() -> new SidedCogHandler(4)), LazyOptional.of(() -> new SidedCogHandler(5))};

	@Override
	public void remove(){
		super.remove();
		for(int i = 0; i < 6; i++){
			cogOpts[i].invalidate();
			axleOpts[i].invalidate();
		}
		axleOpts[6].invalidate();//cogOpts is length 6, axleOpts is length 7
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.COG_CAPABILITY && facing != null){
			if(members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, getAxleAxis(), this)){
				return (LazyOptional<T>) cogOpts[facing.getIndex()];
			}else{
				return LazyOptional.empty();
			}
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing != null){
			if(members[facing.getIndex()] == null && getAxleAxis() == facing.getAxis() && members[6] != null){
				//Connect to axle
				return members[6].hasCap(capability, facing, mats[6], null, getAxleAxis(), this) ? (LazyOptional<T>) axleOpts[6] : LazyOptional.empty();
			}else{
				//Connect to gear on that side
				return members[facing.getIndex()] != null && members[facing.getIndex()].hasCap(capability, facing, mats[facing.getIndex()], facing, getAxleAxis(), this) ? (LazyOptional<T>) axleOpts[facing.getIndex()] : LazyOptional.empty();
			}
		}

		return super.getCapability(capability, facing);
	}

	public Direction.Axis getAxleAxis(){
		return axleAxis;
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
			axleHandlers[side].propagate(masterIn, key, rotationRatioIn, lastRadius, !renderOffset);
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
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			if(members[side] != null){
				this.renderOffset = renderOffset;
				axis = masterIn;
				members[side].propagate(mats[side], side == 6 ? null : Direction.byIndex(side), getAxleAxis(), MechanismTileEntity.this, this, masterIn, key, rotRatioIn, lastRadius);
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
				inertia[side] = members[side].getInertia(mats[side], side == 6 ? null : Direction.byIndex(side), getAxleAxis());
				boundingBoxes[side] = members[side].getBoundingBox(side == 6 ? null : Direction.byIndex(side), getAxleAxis());
			}

			if(sendPacket && !world.isRemote){
				CRPackets.sendPacketAround(world, pos, new SendLongToClient(side + 7, members[side] == null ? -1L : (MECHANISMS.indexOf(members[side]) & 0xFFFFFFFFL) | (long) (mats[side].serialize()) << 32L, pos));
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
	}
}
