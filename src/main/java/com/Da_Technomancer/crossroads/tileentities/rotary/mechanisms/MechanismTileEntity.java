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
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import javax.annotation.Nullable;
import java.util.ArrayList;

//@ObjectHolder(Crossroads.MODID)
public class MechanismTileEntity extends BlockEntity implements ITickableTileEntity, ILongReceiver, IInfoTE{

	@ObjectHolder(Crossroads.MODID + ":mechanism")
	public static BlockEntityType<MechanismTileEntity> TYPE = null;

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

	public MechanismTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		int part = -1;
		Vec3 hitVec = hit.getLocation().subtract(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());//Subtract position, as the VoxelShapes are defined relative to position
		for(int i = 0; i < 7; i++){
			if(boundingBoxes[i] != null && Mechanism.voxelContains(boundingBoxes[i], hitVec)){
				part = i;
				break;
			}
		}

		if(part == -1){
			return;
		}

		RotaryUtil.addRotaryInfo(chat, axleHandlers[part], false);
	}

	// D-U-N-S-W-E-A

	//Public for read-only
	public final IMechanism<?>[] members = new IMechanism[7];
	//Public for read-only
	public final IMechanismProperty[] mats = new IMechanismProperty[7];
	private final double[] energy = new double[7];
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
			if(!newTE && !level.isClientSide){
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(14, axis == null ? -1 : axis.ordinal(), worldPosition));
			}
		}

		if(newTE){
			updateMembers = true;
		}else{
			axleHandlers[index].updateStates(true);
		}

		setChanged();
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);

		// members
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.putInt("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				nbt.putString("[" + i + "]mat", mats[i].getSaveName());
			}
			nbt.putDouble("[" + i + ",1]mot", energy[i]);
		}

		if(members[6] != null && mats[6] != null && getAxleAxis() != null){
			nbt.putInt("axis", getAxleAxis().ordinal());
		}

		nbt.putInt("reds", redstoneIn);

	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
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
	public void load(CompoundTag nbt){
		super.load(nbt);

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
				energy[i] = nbt.getDouble("[" + i + ",1]mot");

//				clientW[i] = nbt.getFloat("[" + i + "]cl_w");
//				angle[i] = nbt.getFloat("[" + i + "]ang");

				axleHandlers[i].updateStates(false);
			}
		}

		redstoneIn = nbt.getInt("reds");
	}

	@Override
	public void receiveLong(byte identifier, long message, @Nullable ServerPlayer sendingPlayer){
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
	public void serverTick(){
		ITickableTileEntity.super.serverTick();

		if(updateMembers){
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(14, getAxleAxis() == null ? -1 : getAxleAxis().ordinal(), worldPosition));
			for(int i = 0; i < 7; i++){
				axleHandlers[i].updateStates(true);
			}
			updateMembers = false;
		}
	}

	public void updateRedstone(){
		int reds = RedstoneUtil.getRedstoneAtPos(level, worldPosition);
		if(reds != redstoneIn){
			setChanged();
			for(int i = 0; i < 7; i++){
				if(members[i] != null){
					members[i].onRedstoneChange(redstoneIn, reds, mats[i], i == 6 ? null : Direction.from3DDataValue(i), getAxleAxis(), energy[i], axleHandlers[i].getSpeed(), this);
				}
			}
			redstoneIn = reds;
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(15, (long) redstoneIn, worldPosition));
		}
	}

	public float getRedstone(){
		return members[6] != null && getAxleAxis() != null ? (float) members[6].getCircuitSignal(mats[6], getAxleAxis(), energy[6], axleHandlers[6].getSpeed(), this) : 0;
	}

	//Direct access to the axle handlers is needed
	protected final SidedAxleHandler[] axleHandlers = {new SidedAxleHandler(0), new SidedAxleHandler(1), new SidedAxleHandler(2), new SidedAxleHandler(3), new SidedAxleHandler(4), new SidedAxleHandler(5), new SidedAxleHandler(6)};

	@SuppressWarnings("unchecked")
	private final LazyOptional<IAxleHandler>[] axleOpts = new LazyOptional[] {LazyOptional.of(() -> axleHandlers[0]), LazyOptional.of(() -> axleHandlers[1]), LazyOptional.of(() -> axleHandlers[2]), LazyOptional.of(() -> axleHandlers[3]), LazyOptional.of(() -> axleHandlers[4]), LazyOptional.of(() -> axleHandlers[5]), LazyOptional.of(() -> axleHandlers[6])};
	@SuppressWarnings("unchecked")
	private final LazyOptional<ICogHandler>[] cogOpts = new LazyOptional[] {LazyOptional.of(() -> new SidedCogHandler(0)), LazyOptional.of(() -> new SidedCogHandler(1)), LazyOptional.of(() -> new SidedCogHandler(2)), LazyOptional.of(() -> new SidedCogHandler(3)), LazyOptional.of(() -> new SidedCogHandler(4)), LazyOptional.of(() -> new SidedCogHandler(5))};

	@Override
	public void setRemoved(){
		super.setRemoved();
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
			if(members[facing.get3DDataValue()] != null && members[facing.get3DDataValue()].hasCap(capability, facing, mats[facing.get3DDataValue()], facing, getAxleAxis(), this)){
				return (LazyOptional<T>) cogOpts[facing.get3DDataValue()];
			}else{
				return LazyOptional.empty();
			}
		}
		if(capability == Capabilities.AXLE_CAPABILITY && facing != null){
			if(members[facing.get3DDataValue()] == null && getAxleAxis() == facing.getAxis() && members[6] != null){
				//Connect to axle
				return members[6].hasCap(capability, facing, mats[6], null, getAxleAxis(), this) ? (LazyOptional<T>) axleOpts[6] : LazyOptional.empty();
			}else{
				//Connect to gear on that side
				return members[facing.get3DDataValue()] != null && members[facing.get3DDataValue()].hasCap(capability, facing, mats[facing.get3DDataValue()], facing, getAxleAxis(), this) ? (LazyOptional<T>) axleOpts[facing.get3DDataValue()] : LazyOptional.empty();
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

	/**
	 * Optimization: Marks the block dirty without updating the blockstate cache
	 * Careful when using this- any situation where the blockstate might change makes this unacceptable
	 */
	private void markDirtyLight(){
		level.blockEntityChanged(worldPosition);
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
		public double getSpeed(){
			return axis == null ? 0 : axis.getBaseSpeed() * rotRatio;
		}

		@Override
		public double getEnergy(){
			return energy[side];
		}

		@Override
		public void setEnergy(double newEnergy){
			energy[side] = newEnergy;
//			markDirty();
			markDirtyLight();
		}

		@Override
		public void propagate(IAxisHandler masterIn, byte key, double rotRatioIn, double lastRadius, boolean renderOffset){
			if(members[side] != null){
				this.renderOffset = renderOffset;
				axis = masterIn;
				members[side].propagate(mats[side], side == 6 ? null : Direction.from3DDataValue(side), getAxleAxis(), MechanismTileEntity.this, this, masterIn, key, rotRatioIn, lastRadius);
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
				energy[side] = 0;
				boundingBoxes[side] = null;
			}else{
				inertia[side] = members[side].getInertia(mats[side], side == 6 ? null : Direction.from3DDataValue(side), getAxleAxis());
				boundingBoxes[side] = members[side].getBoundingBox(side == 6 ? null : Direction.from3DDataValue(side), getAxleAxis());
			}

			if(sendPacket && !level.isClientSide){
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToClient(side + 7, members[side] == null ? -1L : (MECHANISMS.indexOf(members[side]) & 0xFFFFFFFFL) | (long) (mats[side].serialize()) << 32L, worldPosition));
			}
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}
	}
}
