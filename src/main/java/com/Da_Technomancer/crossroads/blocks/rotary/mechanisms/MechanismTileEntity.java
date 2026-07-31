package com.Da_Technomancer.crossroads.blocks.rotary.mechanisms;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.packets.CRPackets;
import com.Da_Technomancer.crossroads.api.rotary.*;
import com.Da_Technomancer.crossroads.api.templates.IInfoTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.crossroads.blocks.rotary.Mechanism;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import com.Da_Technomancer.essentials.api.packets.ILongReceiver;
import com.Da_Technomancer.essentials.api.packets.INBTReceiver;
import com.Da_Technomancer.essentials.api.packets.SendLongToTE;
import com.Da_Technomancer.essentials.api.packets.SendNBTToTE;
import com.Da_Technomancer.essentials.api.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
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

import javax.annotation.Nullable;
import java.util.ArrayList;

public class MechanismTileEntity extends BlockEntity implements ITickableTileEntity, ILongReceiver, INBTReceiver, IInfoTE, IAxleCapable, ICogCapable{

	public static final BlockEntityType<MechanismTileEntity> TYPE = CRTileEntity.createType(MechanismTileEntity::new, CRBlocks.mechanism);

	public static final ArrayList<IMechanism<?>> MECHANISMS = new ArrayList<>(10);//This is a list instead of an array to allow expansion by addons

	static{
		MECHANISMS.add(new MechanismSmallGear());//Index 0, small gear
		MECHANISMS.add(new MechanismAxle());//Index 1, axle
		MECHANISMS.add(new MechanismClutch(false));//Index 2, normal clutch
		MECHANISMS.add(new MechanismClutch(true));//Index 3, inverted clutch
		MECHANISMS.add(new MechanismToggleGear(false));//Index 4, normal toggle gear
		MECHANISMS.add(new MechanismToggleGear(true));//Index 5, inverted toggle gear
		MECHANISMS.add(new MechanismAxleMount());//Index 6, axle mount
		MECHANISMS.add(MechanismFacade.INSTANCE);//Index 7, facades
		MECHANISMS.add(MechanismLargeGearCore.INSTANCE);//Index 8, large gear core
		MECHANISMS.add(MechanismLargeGearEdge.INSTANCE);//Index 9, large gear edge
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

		members[part].addInfo(chat, player, mats[part], part == 6 ? null : Direction.from3DDataValue(part), getAxleAxis(), this, axleHandlers[part]);
//		RotaryUtil.addRotaryInfo(chat, axleHandlers[part], false, player);
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
	 * If there is an existing mechanism in that slot, it will be overridden and the new mechanism will inherit some of its properties - for a clean replacement, call this with a null mechanism first
	 * @param index The index, with 6 being the axle slot. Must be from 0 to 6, inclusive.
	 * @param mechanism The new mechanism. May be null.
	 * @param mat The new material. If mechanism is null, must be null. If mechanism is nonnull, must be nonnull.
	 * @param axis The new axle orientation, if index = 6. Should be null otherwise.
	 * @param newTE Whether this TE is newly created this tick
	 */
	public void setMechanism(int index, @Nullable IMechanism<?> mechanism, @Nullable IMechanismProperty mat, @Nullable Direction.Axis axis, boolean newTE){
		final IMechanism<?> prevMechanism = members[index];
		final IMechanismProperty prevMat = mats[index];
		final Direction.Axis prevAxis = getAxleAxis();

		members[index] = mechanism;
		mats[index] = mat;
		if(index == 6 && prevAxis != axis){
			axleAxis = axis;
			if(!newTE && !level.isClientSide){
				CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(14, axis == null ? -1 : axis.ordinal(), worldPosition));
			}
		}

		if(newTE){
			updateMembers = true;
		}else{
			axleHandlers[index].updateStates(true);
		}
		level.invalidateCapabilities(worldPosition);
		if(prevMechanism != null && mechanism == null){
			//Update pre-existing mechanism as 'removed' only if replaced by null
			prevMechanism.onRemoved(prevMat, index == 6 ? null : Direction.from3DDataValue(index), prevAxis, this);
		}
		setChanged();

		if(!newTE && mechanism == null && members[0] == null && members[1] == null && members[2] == null && members[3] == null && members[4] == null && members[5] == null && members[6] == null){
			level.destroyBlock(worldPosition, false);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);

		// members
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.putInt("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				CompoundTag matNBT = new CompoundTag();
				mats[i].write(matNBT);
				nbt.put("[" + i + "]mat", matNBT);
//				nbt.putString("[" + i + "]mat", mats[i].getSaveName());
			}
			nbt.putDouble("[" + i + ",1]mot", energy[i]);
		}

		if(members[6] != null && mats[6] != null && getAxleAxis() != null){
			nbt.putInt("axis", getAxleAxis().ordinal());
		}

		nbt.putInt("reds", redstoneIn);

	}

	@Override
	public void setBlockState(BlockState pBlockState){
		super.setBlockState(pBlockState);
		//This is not, strictly speaking, optimized
		//Pre MC1.21, default behavior for all TEs was that changing blockstate invalidated capability caches
		//Post MC1.21, this is no longer the case, which opens up some opportunities for optimization
		//But everything was written with the assumption of invalidation on state change,
		//So anything other than re-implementing the old default is going to introduce a lot of new bugs
		level.invalidateCapabilities(worldPosition);
	}

	@Override
	public CompoundTag getUpdateTag(HolderLookup.Provider pRegistries){
		CompoundTag nbt = super.getUpdateTag(pRegistries);
		for(int i = 0; i < 7; i++){
			if(members[i] != null && mats[i] != null){//Sanity check. mats[i] should never be null if members[i] isn't
				nbt.putInt("[" + i + "]memb", MECHANISMS.indexOf(members[i]));
				CompoundTag matNBT = new CompoundTag();
				mats[i].write(matNBT);
				nbt.put("[" + i + "]mat", matNBT);
//				nbt.putString("[" + i + "]mat", mats[i].getSaveName());

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
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);

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

				mats[i] = members[i].readProperty(nbt.getCompound("[" + i + "]mat"));
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
		}else if(identifier >= 7 && identifier < 14){
			if(message == -1){
				members[identifier - 7] = null;
				mats[identifier - 7] = null;
			}else{
				members[identifier - 7] = MECHANISMS.get((int) (message & 0xFFFFFFFFL));
				mats[identifier - 7] = members[identifier - 7].deserializeProperty((int) (message >>> 32L));
			}
			axleHandlers[identifier - 7].updateStates(false);
		}else */
		if(identifier == 14){
			axleAxis = message == -1 ? null : Direction.Axis.values()[(int) message];
			axleHandlers[6].updateStates(false);
		}else if(identifier == 15){
			redstoneIn = (int) message;
		}
	}

	@Override
	public void receiveNBT(CompoundTag nbt, @Nullable ServerPlayer serverPlayer){
		int side = nbt.getInt("side");
		if(nbt.getBoolean("empty")){
			members[side] = null;
			mats[side] = null;
		}else{
			members[side] = MECHANISMS.get(nbt.getInt("mech"));
			mats[side] = members[side].readProperty(nbt.getCompound("prop"));
		}
		axleHandlers[side].updateStates(false);
	}

	@Override
	public void serverTick(){
		ITickableTileEntity.super.serverTick();

		if(updateMembers){
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(14, getAxleAxis() == null ? -1 : getAxleAxis().ordinal(), worldPosition));
			for(int i = 0; i < 7; i++){
				axleHandlers[i].updateStates(true);
			}
			updateMembers = false;
		}

		if(members[6] != null && getAxleAxis() != null && members[6].shouldUpdateCircuitReaders(mats[6], getAxleAxis(), energy[6], axleHandlers[6].getSpeed(), this)){
			//Need to update comparators/circuit readers
			level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
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
			CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(15, (long) redstoneIn, worldPosition));
		}
	}

	public float getRedstone(){
		return members[6] != null && getAxleAxis() != null ? (float) members[6].getCircuitSignal(mats[6], getAxleAxis(), energy[6], axleHandlers[6].getSpeed(), this) : 0;
	}

	//Direct access to the axle handlers is needed
	protected final SidedAxleHandler[] axleHandlers = {new SidedAxleHandler(0), new SidedAxleHandler(1), new SidedAxleHandler(2), new SidedAxleHandler(3), new SidedAxleHandler(4), new SidedAxleHandler(5), new SidedAxleHandler(6)};

	@SuppressWarnings("unchecked")
	private final ICogHandler[] cogHandlers = new ICogHandler[] {new SidedCogHandler(0), new SidedCogHandler(1), new SidedCogHandler(2), new SidedCogHandler(3), new SidedCogHandler(4), new SidedCogHandler(5)};

	@Nullable
	@Override
	public ICogHandler getCogHandler(Direction dir){
		if(dir != null){
			if(members[dir.get3DDataValue()] != null && members[dir.get3DDataValue()].hasCap(CRCapabilities.COG_CAPABILITY, dir, mats[dir.get3DDataValue()], dir, getAxleAxis(), this)){
				return cogHandlers[dir.get3DDataValue()];
			}else{
				return null;
			}
		}
		return null;
	}

	@Nullable
	@Override
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir != null){
			if(members[dir.get3DDataValue()] == null && getAxleAxis() == dir.getAxis() && members[6] != null){
				//Connect to axle
				return members[6].hasCap(CRCapabilities.AXLE_CAPABILITY, dir, mats[6], null, getAxleAxis(), this) ? axleHandlers[6] : null;
			}else{
				//Connect to gear on that side
				return members[dir.get3DDataValue()] != null && members[dir.get3DDataValue()].hasCap(CRCapabilities.AXLE_CAPABILITY, dir, mats[dir.get3DDataValue()], dir, getAxleAxis(), this) ? axleHandlers[dir.get3DDataValue()] : null;
			}
		}
		return null;
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
			if(members[side] != null){
				members[side].connect(mats[side], side == 6 ? null : Direction.from3DDataValue(side), getAxleAxis(), MechanismTileEntity.this, axleHandlers[side], masterIn, key, rotationRatioIn, lastRadius, cogOrient, renderOffset);
			}
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

	protected class SidedAxleHandler implements IMechanismAxleHandler{

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
		public void disconnect(IAxisHandler disconnectingAxis){
			if(disconnectingAxis == null || axis == disconnectingAxis){
				axis = null;
			}
		}

		@Override
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
				CompoundTag updateNBT = new CompoundTag();
				updateNBT.putInt("side", side);
				if(members[side] == null){
					updateNBT.putBoolean("empty", true);
				}else{
					updateNBT.putInt("mech", MECHANISMS.indexOf(members[side]));
					CompoundTag propertyNBT = new CompoundTag();
					mats[side].write(propertyNBT);
					updateNBT.put("prop", propertyNBT);
				}
				CRPackets.sendPacketAround(level, worldPosition, new SendNBTToTE(updateNBT, worldPosition));
//				CRPackets.sendPacketAround(level, worldPosition, new SendLongToTE(side + 7, members[side] == null ? -1L : (MECHANISMS.indexOf(members[side]) & 0xFFFFFFFFL) | (long) (mats[side].serialize()) << 32L, worldPosition));
			}
		}

		@Override
		public double getRotationRatio(){
			return rotRatio;
		}

		@Override
		public byte getUpdateKey(){
			return updateKey;
		}

		@Override
		public void setUpdateKey(byte keyIn){
			this.updateKey = keyIn;
		}

		@Override
		public boolean renderOffset(){
			return renderOffset;
		}

		@Override
		public void setRenderOffset(boolean newOffset){
			renderOffset = newOffset;
		}

		@Override
		public void setRotRatio(double newRotRatio){
			rotRatio = newRotRatio;
		}
	}
}
