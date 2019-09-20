package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.ITaylorReceiver;
import com.Da_Technomancer.crossroads.API.packets.CrossroadsPackets;
import com.Da_Technomancer.crossroads.API.packets.SendTaylorToClient;
import com.Da_Technomancer.crossroads.API.rotary.*;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.essentials.blocks.EssentialsProperties;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ITickableTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class MasterAxisTileEntity extends TileEntity implements ITickableTileEntity, ITaylorReceiver{

	protected boolean locked = false;
	protected double sumEnergy = 0;
	protected long ticksExisted = 0;
	protected byte key;
	protected int lastKey = 0;
	protected boolean forceUpdate;
	protected Direction facing;

	protected static final Random RAND = new Random();

	protected ArrayList<IAxleHandler> rotaryMembers = new ArrayList<>();
	protected final HashSet<Pair<ISlaveAxisHandler, Direction>> slaves = new HashSet<>();

	/**
	 * Now I know what you're thinking: "What the heck is this for?". Well it's quite simple- no it isn't that's a lie
	 * The Master Axis is responsible for keeping the rendering angles of all members synced centrally.
	 * A Taylor Series (if you don't know what that is, I suggest you google "Taylor Series", "Power Series", "Derivatives calculus", and "Painless suicide methods" in that order)
	 * is used to estimate and extrapolate future and intermediate gear angles to reduce the number of angle information packets that have to be sent.
	 * The Taylor Series is only regenerated and resynced when its predication begins to significantly diverge from actual values, by more than ANGLE_MARGIN
	 */
	private long seriesTimestamp;
	private float[] taylorSeries = new float[4];
	/**
	 * Stores the previous 4 angle values as a reference to calculate and verify the Taylor series.
	 * The first value is the oldest
	 */
	private float[] prevAngles = new float[4];

	private static final float ANGLE_MARGIN = (float) CrossroadsConfig.speedPrecision.getDouble();
	protected static final int UPDATE_TIME = CrossroadsConfig.gearResetTime.getInt();

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, BlockState oldState, BlockState newState){
		return newState.getBlock() != oldState.getBlock();
	}

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!state.getPropertyKeys().contains(EssentialsProperties.FACING)){
				invalidate();
				return Direction.DOWN;
			}
			facing = state.get(EssentialsProperties.FACING);
		}

		return facing;
	}

	@Override
	public void invalidate(){
		super.invalidate();
		//It is important that disconnect is called when this TE is destroyed/removed/invalidated on both the server and client to both prevent memory leaks, and clear up minor rendering abnormalities
		disconnect();
	}

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
			//For 0-mass gears.
			axle.getMotionData()[0] = 0;
			axle.getMotionData()[2] = 0;
			axle.getMotionData()[3] = 0;
			axle.disconnect();
		}
		for(int i = 0; i < 4; i++){
			prevAngles[i] = 0;
			taylorSeries[i] = 0;
		}
		rotaryMembers.clear();
		RotaryUtil.increaseMasterKey(false);
		facing = null;
	}

	protected void runCalc(){
		double sumIRot = 0;
		sumEnergy = 0;
		// IRL you wouldn't say a gear spinning a different direction has
		// negative energy, but it makes the code easier.

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
		}

		if(sumIRot == 0 || sumIRot != sumIRot){
			return;
		}

		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);
		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}

		for(IAxleHandler gear : rotaryMembers){
			// set w
			double newSpeed = Math.signum(sumEnergy * gear.getRotationRatio()) * Math.sqrt(Math.abs(sumEnergy) * 2D * Math.pow(gear.getRotationRatio(), 2) / sumIRot);
			gear.getMotionData()[0] = newSpeed;
			// set energy
			double newEnergy = Math.signum(newSpeed) * Math.pow(newSpeed, 2) * gear.getMoInertia() / 2D;
			gear.getMotionData()[1] = newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20D;
			// set lastE
			gear.getMotionData()[3] = newEnergy;

			gear.markChanged();
		}
	}

	protected void runAngleCalc(){
		if(rotaryMembers.isEmpty()){
			//Clear all angle data
			for(int i = 0; i < 4; i++){
				prevAngles[i] = 0;
				taylorSeries[i] = 0;
			}
		}else if(!world.isRemote){//Server side, has members
			//Add the current angle value to the prevAngles record, and shift the array
			System.arraycopy(prevAngles, 1, prevAngles, 0, 3);
			prevAngles[3] = prevAngles[2] + (float) (rotaryMembers.get(0).getMotionData()[0] / rotaryMembers.get(0).getRotationRatio()) / 20F;


			if(Math.abs(runSeries(ticksExisted, 0, true) - prevAngles[3]) >= ANGLE_MARGIN){
				//Take the current simulated angle as the new "true" angle value, to prevent a "jerking" re-alignment of gear angles on the client side
				float delta = runSeries(ticksExisted, 0, false) - prevAngles[3];
				for(int i = 0; i < 4; i++){
					prevAngles[i] += delta;
				}

				//Generate a new series
				taylorSeries[0] = 0;
				taylorSeries[1] = prevAngles[3] - prevAngles[2];
				taylorSeries[2] = taylorSeries[1] - (prevAngles[2] - prevAngles[1]);
				taylorSeries[3] = taylorSeries[2] - ((prevAngles[2] - prevAngles[1]) - (prevAngles[1] - prevAngles[0]));

				//Build in the factorial quotients
				taylorSeries[1] /= 1F;//1!
				taylorSeries[2] /= 2F;//2!
				taylorSeries[3] /= 6F;//3!
				seriesTimestamp = ticksExisted;

				//Set the first term of the Taylor series such that calling runSeries with the current time gets the current angle
				float offset = runSeries(ticksExisted, 0, false);
				taylorSeries[0] = prevAngles[3] - offset;

				//Sync the series to the client
				CrossroadsPackets.network.sendToAllAround(new SendTaylorToClient(seriesTimestamp, taylorSeries, pos), new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
			}
		}
	}

	private float runSeries(long time, float partialTicks, boolean simulate){
		float relTime = time - seriesTimestamp;
		relTime += partialTicks;

		double result = taylorSeries[0] + (relTime - 0.5F) * taylorSeries[1];
		if(simulate || relTime >= 2F){
			result += Math.pow(relTime - 1F, 2F) * taylorSeries[2];
			if(simulate || relTime >= 3F){
				result += Math.pow(relTime - 1.5F, 3F) * taylorSeries[3];
			}
		}
		return (float) result;
	}

	@Override
	public void receiveSeries(long timestamp, float[] series){
		seriesTimestamp = timestamp;
		taylorSeries = series;
	}

	@Override
	public void update(){
		ticksExisted++;
		markDirty();

		if(ticksExisted % UPDATE_TIME == 20 || forceUpdate || rotaryMembers.isEmpty()){
			handler.requestUpdate();
		}

		forceUpdate = RotaryUtil.getMasterKey() != lastKey;

		lastKey = RotaryUtil.getMasterKey();

		if(!locked && !rotaryMembers.isEmpty()){
			if(!world.isRemote){
				runCalc();
			}
			runAngleCalc();
			triggerSlaves();
		}
	}

	protected void triggerSlaves(){
		HashSet<Pair<ISlaveAxisHandler, Direction>> toRemove = new HashSet<>();
		for(Pair<ISlaveAxisHandler, Direction> slave : slaves){
			if(slave.getLeft().isInvalid()){
				toRemove.add(slave);
				continue;
			}
			slave.getLeft().trigger(slave.getRight());
		}
		slaves.removeAll(toRemove);
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		ticksExisted = nbt.getLong("life");
		for(int i = 0; i < 4; i++){
			prevAngles[i] = nbt.getFloat("prev_" + i);
			taylorSeries[i] = nbt.getFloat("taylor_" + i);
		}
		seriesTimestamp = nbt.getLong("timestamp");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putLong("life", ticksExisted);
		for(int i = 0; i < 4; i++){
			nbt.putFloat("prev_" + i, prevAngles[i]);
			nbt.putFloat("taylor_" + i, taylorSeries[i]);
		}
		nbt.putLong("timestamp", seriesTimestamp);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		return writeToNBT(nbt);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	protected AxisTypes getType(){
		return AxisTypes.NORMAL;
	}

	protected final IAxisHandler handler = new AxisHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	protected class AxisHandler implements IAxisHandler{

		@Override
		public void trigger(IAxisHandler masterIn, byte keyIn){
			if(keyIn != key){
				locked = true;
			}
		}

		private ArrayList<IAxleHandler> memberCopy;

		@Override
		public void requestUpdate(){
			if(CrossroadsConfig.disableSlaves.getBoolean()){
				return;
			}
			memberCopy = new ArrayList<>(rotaryMembers);
			rotaryMembers.clear();
			locked = false;
			Direction dir = getFacing();
			TileEntity te = world.getTileEntity(pos.offset(dir));
			IAxleHandler axleHandler;
			if(te != null && (axleHandler = te.getCapability(Capabilities.AXLE_CAPABILITY, dir.getOpposite())) != null){
				byte keyNew;
				do {
					keyNew = (byte) (RAND.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;
				axleHandler.propogate(this, key, 1, 0, false);
			}

			memberCopy.removeAll(rotaryMembers);
			for(IAxleHandler axle : memberCopy){
				//For 0-mass gears.
				axle.getMotionData()[0] = 0;
				axle.disconnect();
			}
			memberCopy = null;
		}

		@Override
		public void lock(){
			locked = true;
			if(memberCopy != null){
				rotaryMembers.addAll(memberCopy);
			}
			for(IAxleHandler gear : rotaryMembers){
				gear.getMotionData()[0] = 0;
				gear.getMotionData()[1] = 0;
				gear.getMotionData()[2] = 0;
				gear.getMotionData()[3] = 0;
				gear.markChanged();
			}
			rotaryMembers.clear();
			memberCopy.clear();
		}

		@Override
		public boolean isLocked(){
			return locked;
		}

		@Override
		public boolean addToList(IAxleHandler handler){
			if(!locked){
				rotaryMembers.add(handler);
				return false;
			}else{
				return true;
			}
		}

		@Override
		public void addAxisToList(ISlaveAxisHandler handler, Direction side){
			slaves.add(Pair.of(handler, side));
		}

		@Override
		public double getTotalEnergy(){
			return sumEnergy;
		}

		@Override
		public float getAngle(double rotRatio, float partialTicks, boolean shouldOffset, float angleOffset){
			float angle = runSeries(ticksExisted, partialTicks, false);
			angle *= rotRatio;
			angle = (float) Math.toDegrees(angle);
			if(shouldOffset){
				angle += angleOffset;
			}
			return angle;
		}

		@Override
		public AxisTypes getType(){
			return MasterAxisTileEntity.this.getType();
		}
	}
}
