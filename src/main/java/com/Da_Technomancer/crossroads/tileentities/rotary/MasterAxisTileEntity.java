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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.TickableBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;
import java.util.Random;

@ObjectHolder(Crossroads.MODID)
public class MasterAxisTileEntity extends BlockEntity implements TickableBlockEntity, ITaylorReceiver{

	@ObjectHolder("master_axis")
	private static BlockEntityType<MasterAxisTileEntity> type = null;

	protected static final Random RAND = new Random();

	//Network building
	protected boolean forceUpdate;
	protected byte key;
	protected int lastKey = 0;
	protected boolean locked = false;

	//Network independent axis data
	protected Direction facing;
	protected long ticksExisted = 0;

	//Motion data
	protected double sumEnergy = 0;
	protected double baseSpeed = 0;
	protected double energyChange = 0;
	protected double energyLossChange = 0;//Note that this is the wrong sign

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

	protected MasterAxisTileEntity(BlockEntityType<? extends MasterAxisTileEntity> typeIn){
		super(typeIn);
	}

	protected Direction getFacing(){
		if(facing == null){
			BlockState state = getBlockState();
			if(!state.hasProperty(ESProperties.FACING)){
				setRemoved();
				return Direction.DOWN;
			}
			facing = state.getValue(ESProperties.FACING);
		}

		return facing;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		//It is important that disconnect is called when this TE is destroyed/removed/invalidated on both the server and client to both prevent memory leaks, and clear up minor rendering abnormalities
		disconnect();
		axisOpt.invalidate();
//		axisOpt = LazyOptional.of(() -> handler);
	}

	@Override
	public void clearCache(){
		super.clearCache();
		disconnect();
		axisOpt.invalidate();
		axisOpt = LazyOptional.of(() -> handler);
	}

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
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
		double prevSumEnergy = sumEnergy;

		double[] systemEnergyResult = RotaryUtil.getTotalEnergy(rotaryMembers, true);
		sumEnergy = systemEnergyResult[0];
		energyLossChange = systemEnergyResult[1];
		baseSpeed = systemEnergyResult[2];
		//For very low total system energy, we drain the remainder of the energy as 'loss'
		if(sumEnergy < 1 && sumEnergy > -1 || Double.isNaN(sumEnergy)){
			energyLossChange += sumEnergy;
			sumEnergy = 0;
			baseSpeed = 0;
		}
		energyChange = sumEnergy - prevSumEnergy;

		for(IAxleHandler gear : rotaryMembers){
			// set energy
			double gearSpeed = baseSpeed * gear.getRotationRatio();
			gear.setEnergy(Math.signum(gearSpeed) * Math.pow(gearSpeed, 2) * gear.getMoInertia() / 2D);
		}
	}

	protected void runAngleCalc(){
		boolean timeReset = false;
		if(ticksExisted > 86400){
			//For very high ticksExisted, floating point errors become visually noticeable with 'choppy' angles
			ticksExisted = 0;
			timeReset = true;
		}

		if(rotaryMembers.isEmpty()){
			//Clear all angle data
			for(int i = 0; i < 4; i++){
				prevAngles[i] = 0;
				coeff[i] = 0;
				if(i != 3){
					wCoeff[i] = 0;
				}
			}
		}else if(!level.isClientSide){//Server side, has members
			float trueSpeed = (float) baseSpeed / 20F;//Speed in rad/t
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
//			final float RESET_MARGIN = ADJUST_MARGIN * 2F;

			float speedPred = runWSeries(ticksExisted);
			float diff = Math.abs(speedPred - trueSpeed);
			if(Float.isNaN(diff)){
				diff = Float.MAX_VALUE;
			}
			boolean signChanged = Math.signum(speedPred) != Math.signum(trueSpeed);
			if(diff >= ADJUST_MARGIN || signChanged || timeReset){
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
				CRPackets.sendPacketAround(level, worldPosition, new SendTaylorToClient(ticksExisted, coeff, worldPosition));
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
		float partTicks = Minecraft.getInstance().getFrameTime();
		float prevAngle = runSeries(ticksExisted, partTicks);
		regrTimestamp = timestamp;
		coeff = series;
		//Fine tune the linear term to match up with the currently displayed angle- preventing a jerking motion
		coeff[3] += prevAngle - runSeries(ticksExisted, partTicks);
	}

	@Override
	public void tick(){
		ticksExisted++;
		setChanged();

		if(ticksExisted % UPDATE_TIME == 20 || forceUpdate || rotaryMembers.isEmpty()){
			handler.requestUpdate();
		}

		forceUpdate = RotaryUtil.getMasterKey() != lastKey;

		lastKey = RotaryUtil.getMasterKey();

		if(!locked && !rotaryMembers.isEmpty()){
			if(!level.isClientSide){
				runCalc();
			}
			runAngleCalc();
		}
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
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

		sumEnergy = nbt.getDouble("sum_energy");
		baseSpeed = nbt.getDouble("base_speed");
		energyChange = nbt.getDouble("energy_change");
		energyLossChange = nbt.getDouble("energy_change_loss");

		regrTimestamp = nbt.getLong("timestamp");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putLong("life", ticksExisted);
		for(int i = 0; i < 4; i++){
			nbt.putFloat("prev_" + i, prevAngles[i]);
			nbt.putFloat("coeff_" + i, coeff[i]);
			if(i != 3){
				nbt.putFloat("w_coeff_" + i, wCoeff[i]);
			}
		}
		nbt.putLong("timestamp", regrTimestamp);

		nbt.putDouble("sum_energy", sumEnergy);
		nbt.putDouble("base_speed", baseSpeed);
		nbt.putDouble("energy_change", energyChange);
		nbt.putDouble("energy_change_loss", energyLossChange);
		return nbt;
	}

	@Override
	public CompoundTag getUpdateTag(){
		CompoundTag nbt = super.getUpdateTag();
		return save(nbt);
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
			BlockEntity te = level.getBlockEntity(worldPosition.relative(dir));
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
				gear.setEnergy(0);
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
//				if(rotaryMembers.contains(handler)){
//					Crossroads.logger.error("Handler attempted to add itself repeatedly to axis; Instance: " + handler.toString());
//					return false;
//				}
				rotaryMembers.add(handler);
				return false;
			}else{
				return true;
			}
		}

		@Override
		public double getTotalEnergy(){
			return locked ? 0 : sumEnergy;
		}

		@Override
		public double getEnergyChange(){
			return locked ? 0 : energyChange;
		}

		@Override
		public double getEnergyLost(){
			return locked ? 0 : -energyLossChange;
		}

		@Override
		public double getBaseSpeed(){
			return locked ? 0 : baseSpeed;
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
