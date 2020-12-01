package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.packets.CRPackets;
import com.Da_Technomancer.crossroads.API.packets.ITaylorReceiver;
import com.Da_Technomancer.crossroads.API.packets.SendTaylorToClient;
import com.Da_Technomancer.crossroads.API.rotary.AxisTypes;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.Random;

@ObjectHolder(Crossroads.MODID)
public class MasterAxisTileEntity extends TileEntity implements ITickableTileEntity, ITaylorReceiver{

	@ObjectHolder("master_axis")
	private static TileEntityType<MasterAxisTileEntity> type = null;
	protected static final Random RAND = new Random();

	protected boolean locked = false;
	protected double sumEnergy = 0;
	protected long ticksExisted = 0;
	protected byte key;
	protected int lastKey = 0;
	protected boolean forceUpdate;
	protected Direction facing;

	protected ArrayList<IAxleHandler> rotaryMembers = new ArrayList<>();

	/**
	 * We model and predict the speeds and angles with a regression of past values
	 * The used regression is an area of active development;
	 * Currently: Linear regression for θ(t), constant ω(t)
	 * Attempted:
	 * -3rd order Taylor polynomial for θ(t); Failed due to handling asymptote behaviour poorly
	 * -A/(t + B) + C for ω(t); Failed due to only being a good approximation in limited circumstances
	 *
	 * Timestamp of when the regression was generated and the time it is defined relative to
	 */
	private long regrTimestamp = 0;
	/**
	 * Stores the coefficients on the regression for θ(t). A, B, C, D
	 */
	private float[] coeff = new float[4];
	/**
	 * Stores the coefficients on the regression for ω(t). A, B, C
	 * Used on the server side to track when to invalidate the angle regression
	 */
	private float[] wCoeff = new float[3];
	/**
	 * Stores the previous 4 angle values as a reference to calculate and verify the regression.
	 * The first value is the oldest
	 */
	private float[] prevAngles = new float[4];

//	private static final float ANGLE_MARGIN = CRConfig.speedPrecision.get().floatValue();
	protected static final int UPDATE_TIME = CRConfig.gearResetTime.get();

	public MasterAxisTileEntity(){
		this(type);
	}

	protected MasterAxisTileEntity(TileEntityType<? extends MasterAxisTileEntity> typeIn){
		super(typeIn);
	}

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = world.getBlockState(pos);
			if(!state.hasProperty(ESProperties.FACING)){
				remove();
				return Direction.DOWN;
			}
			facing = state.get(ESProperties.FACING);
		}

		return facing;
	}

	@Override
	public void remove(){
		super.remove();
		//It is important that disconnect is called when this TE is destroyed/removed/invalidated on both the server and client to both prevent memory leaks, and clear up minor rendering abnormalities
		disconnect();
		axisOpt.invalidate();
		axisOpt = LazyOptional.of(() -> handler);
	}

	@Override
	public void updateContainingBlockInfo(){
		super.updateContainingBlockInfo();
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
			coeff[i] = 0;
			if(i != 3){
				wCoeff[i] = 0;
			}
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

		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers, true);
		if(sumEnergy < 1 && sumEnergy > -1 || Double.isNaN(sumEnergy)){
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
				coeff[i] = 0;
				if(i != 3){
					wCoeff[i] = 0;
				}
			}
		}else if(!world.isRemote){//Server side, has members
			//Speed in rad/t
			float trueSpeed = (float) (rotaryMembers.get(0).getMotionData()[0] / rotaryMembers.get(0).getRotationRatio()) / 20F;
			if(Float.isNaN(trueSpeed)){
				trueSpeed = 0;
			}
			//Add the current angle value to the prevAngles record, and shift the array
			System.arraycopy(prevAngles, 1, prevAngles, 0, 3);
			prevAngles[3] = prevAngles[2] + trueSpeed;
			if(Float.isNaN(prevAngles[3])){
				prevAngles[3] = 0;
			}


			final float ADJUST_MARGIN = CRConfig.speedPrecision.get().floatValue() / 20F;
			final float RESET_MARGIN = ADJUST_MARGIN * 2F;

			float speedPred = runWSeries(ticksExisted);
			float diff = Math.abs(speedPred - trueSpeed);
			if(Float.isNaN(diff)){
				diff = Float.MAX_VALUE;
			}
			boolean signChanged = Math.signum(speedPred) != Math.signum(trueSpeed);
			if(diff >= ADJUST_MARGIN || signChanged){
				//Take the current simulated angle as the new "true" angle value, to prevent a "jerking" re-alignment of gear angles on the client side
				float delta = runSeries(ticksExisted, 0) - prevAngles[3];
				if(Float.isNaN(delta)){
					delta = 0;
				}
				for(int i = 0; i < 4; i++){
					prevAngles[i] += delta;
				}

//				//Whether we believe this to be a jump discontinuity, or just the regression diverging
//				boolean jump = diff >= RESET_MARGIN || signChanged;

				//Currently this code has more boilerplate than we use. For more advanced regressions, this boilerplate is used
				//Generate a new regression
//				float[] prevSpeed = new float[3];
//				prevSpeed[2] = trueSpeed;
//
//				if(jump){
//					//We can't trust any previous values- they were all before the discontinuity. Therefore, we take the higher order derivatives as 0
//					prevSpeed[1] = trueSpeed;
//					prevSpeed[0] = trueSpeed;
//				}else{
//					prevSpeed[1] = prevAngles[2] - prevAngles[1];
//					prevSpeed[0] = prevAngles[1] - prevAngles[0];
//				}

				//linear regression
				wCoeff[0] = trueSpeed;
				wCoeff[1] = 0;
				wCoeff[2] = 0;

				//Generate angle series
				//A*time + B
				coeff[0] = trueSpeed;
				coeff[1] = 0;
				coeff[2] = 0;
				coeff[3] = 0;
				regrTimestamp = ticksExisted;

				//Set the constant term of the regression such that calling runSeries with the current time gets the current angle
				float offset = runSeries(ticksExisted, 0);
				coeff[3] = prevAngles[3] - offset;

				//Sync the series to the client
				CRPackets.sendPacketAround(world, pos, new SendTaylorToClient(ticksExisted, coeff, pos));
			}
		}
	}

	private float runSeries(long time, float partialTicks){
		float relTime = time + partialTicks - regrTimestamp;
		return coeff[0] * relTime + coeff[3];
//		float relTime = time - seriesTimestamp;
//		relTime += partialTicks;
//
//		//The time offsets are due to the higher order derivatives being found as a difference of derivatives- making them defined relative to a different time
//		double result = taylorSeries[0] + (relTime - 0.5F) * taylorSeries[1];
//		result += Math.pow(relTime - 1F, 2F) * taylorSeries[2];
//		result += Math.pow(relTime - 1.5F, 3F) * taylorSeries[3];
//		return (float) result;
	}

	private float runWSeries(long time){
		return wCoeff[0];
	}

	@Override
	public void receiveSeries(long timestamp, float[] series){
		float partTicks = Minecraft.getInstance().getRenderPartialTicks();
		float prevAngle = runSeries(ticksExisted, partTicks);
		regrTimestamp = timestamp;
		coeff = series;
		//Fine tune the linear term to match up with the currently displayed angle- preventing a jerking motion
		coeff[3] += prevAngle - runSeries(ticksExisted, partTicks);
	}

	@Override
	public void tick(){
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
		}
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt){
		super.read(state, nbt);
		ticksExisted = nbt.getLong("life");
		for(int i = 0; i < 4; i++){
			prevAngles[i] = nbt.getFloat("prev_" + i);
			coeff[i] = nbt.getFloat("coeff_" + i);
			if(i != 3){
				wCoeff[i] = nbt.getFloat("w_coeff_" + i);
			}
			if(Float.isNaN(prevAngles[i])){
				prevAngles[i] = 0;//Unlikely to occur, but one NaN value can corrupt the entire angle regression
			}
		}

		regrTimestamp = nbt.getLong("timestamp");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putLong("life", ticksExisted);
		for(int i = 0; i < 4; i++){
			nbt.putFloat("prev_" + i, prevAngles[i]);
			nbt.putFloat("coeff_" + i, coeff[i]);
			if(i != 3){
				nbt.putFloat("w_coeff_" + i, wCoeff[i]);
			}
		}
		nbt.putLong("timestamp", regrTimestamp);
		return nbt;
	}

	@Override
	public CompoundNBT getUpdateTag(){
		CompoundNBT nbt = super.getUpdateTag();
		return write(nbt);
	}

	/**
	 * Describes the behaviour of this master axis into broad categories.
	 * Currently unused
	 * @return The type of this axis
	 */
	protected AxisTypes getAxisType(){
		return AxisTypes.NORMAL;
	}

	protected final IAxisHandler handler = new AxisHandler();
	protected LazyOptional<IAxisHandler> axisOpt = LazyOptional.of(() -> handler);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return (LazyOptional<T>) axisOpt;
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
			memberCopy = new ArrayList<>(rotaryMembers);
			rotaryMembers.clear();
			locked = false;
			Direction dir = getFacing();
			TileEntity te = world.getTileEntity(pos.offset(dir));
			LazyOptional<IAxleHandler> axleOpt;
			if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, dir.getOpposite())).isPresent()){
				byte keyNew;
				do {
					keyNew = (byte) (RAND.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;
				axleOpt.orElseThrow(NullPointerException::new).propagate(this, key, 1, 0, false);
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
			for(int i = 0; i < 4; i++){
				prevAngles[i] = 0;
				coeff[i] = 0;
				if(i != 3){
					wCoeff[i] = 0;
				}
			}
			rotaryMembers.clear();
			if(memberCopy != null){
				memberCopy.clear();
			}
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
		public double getTotalEnergy(){
			return sumEnergy;
		}

		@Override
		public float getAngle(double rotRatio, float partialTicks, boolean shouldOffset, float angleOffset){
			float angle = runSeries(ticksExisted, partialTicks);
			angle *= rotRatio;
			angle = (float) Math.toDegrees(angle);
			if(shouldOffset){
				angle += angleOffset;
			}
			return angle;
		}

		@Override
		public AxisTypes getType(){
			return MasterAxisTileEntity.this.getAxisType();
		}
	}
}
