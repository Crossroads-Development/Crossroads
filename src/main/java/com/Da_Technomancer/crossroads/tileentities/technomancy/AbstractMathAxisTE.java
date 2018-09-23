package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public abstract class AbstractMathAxisTE extends TileEntity implements ITickable{

	//TODO completely broken


	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return newState.getBlock() != oldState.getBlock();
	}

	public AbstractMathAxisTE(){
		super();
	}

	protected ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
			//For 0-mass gears.
			axle.getMotionData()[0] = 0;
			axle.syncAngle();
		}
		rotaryMembers.clear();
		cleanDirCache();
		CommonProxy.masterKey++;
	}

	protected boolean locked = false;
	protected double sumEnergy = 0;
	protected int ticksExisted = 0;
	protected byte key;

	protected static final float CLIENT_SPEED_MARGIN = (float) ModConfig.speedPrecision.getDouble();


	protected void runCalc(){
		EnumFacing side1 = getInOne();
		TileEntity te1 = world.getTileEntity(pos.offset(side1));
		double in1 = te1 != null && te1.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()) ? te1.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side1.getOpposite()).getMotionData()[0] : 0;
		double in2;
		EnumFacing side2 = getInTwo();
		if(side2 == null){
			in2 = 0;
		}else{
			TileEntity te2 = world.getTileEntity(pos.offset(side2));
			in2 = te2 != null && te2.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()) ? te2.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, side2.getOpposite()).getMotionData()[0] : 0;
		}

		double baseSpeed = getOutSpeed(in1, in2);

		EnumFacing sideOut = getOut();

		double sumIRot = 0;
		sumEnergy = 0;

		double cost = 0;

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
			sumEnergy += Math.signum(gear.getRotationRatio()) * gear.getMotionData()[1] * Math.pow(1.001D, -Math.abs(gear.getMotionData()[0]));
		}

		cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;

		TileEntity downTE = world.getTileEntity(pos.offset(getBattery()));

		double availableEnergy = Math.abs(sumEnergy) + Math.abs(downTE != null && downTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP) ? downTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1] : 0);
		if(availableEnergy - cost < 0){
			baseSpeed = 0;
			cost = 0;
		}
		availableEnergy -= cost;

		for(IAxleHandler gear : rotaryMembers){
			double newEnergy = 0;

			// set w
			gear.getMotionData()[0] = gear.getRotationRatio() * baseSpeed;
			// set energy
			newEnergy = Math.signum(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getMoInertia() / 2D;
			gear.getMotionData()[1] = newEnergy;
			sumEnergy += newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;

			gear.markChanged();
		}

		if(downTE != null && downTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			downTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1] = availableEnergy * MiscOp.posOrNeg(downTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1], 1);
		}

		runAngleCalc();
	}

	protected abstract double getOutSpeed(double speed1, double speed2);

	protected abstract EnumFacing getInOne();

	@Nullable
	protected abstract EnumFacing getInTwo();

	protected abstract EnumFacing getOut();

	protected abstract EnumFacing getBattery();

	protected abstract void cleanDirCache();

	protected void runAngleCalc(){
		boolean syncSpin = false;
		boolean work = false;
		for(IAxleHandler axle : rotaryMembers){
			if(axle.shouldManageAngle()){
				syncSpin = Math.abs(axle.getMotionData()[0] - axle.getClientW()) >= CLIENT_SPEED_MARGIN * axle.getRotationRatio();
				work = true;
				break;
			}
		}
		if(!work){
			return;
		}

		for(IAxleHandler axle : rotaryMembers){
			if(axle.shouldManageAngle()){
				float axleSpeed = ((float) axle.getMotionData()[0]);
				axle.setAngle(axle.getAngle() + (axleSpeed * 9F / (float) Math.PI));
				if(syncSpin){
					axle.syncAngle();
				}
			}
		}
	}

	protected int lastKey = 0;
	protected boolean forceUpdate;
	protected static final int UPDATE_TIME = ModConfig.gearResetTime.getInt();

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		ticksExisted++;

		if(ticksExisted % UPDATE_TIME == 20 || forceUpdate){
			handler.requestUpdate();
		}

		forceUpdate = CommonProxy.masterKey != lastKey;

		lastKey = CommonProxy.masterKey;
	}

	protected void triggerSlaves(){
		HashSet<Pair<ISlaveAxisHandler, EnumFacing>> toRemove = new HashSet<Pair<ISlaveAxisHandler, EnumFacing>>();
		for(Pair<ISlaveAxisHandler, EnumFacing> slave : slaves){
			if(slave.getLeft().isInvalid()){
				toRemove.add(slave);
				continue;
			}
			slave.getLeft().trigger(slave.getRight());
		}
		slaves.removeAll(toRemove);
	}

	protected final HashSet<Pair<ISlaveAxisHandler, EnumFacing>> slaves = new HashSet<Pair<ISlaveAxisHandler, EnumFacing>>();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == getOut())){
			return true;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == getInOne() || getInTwo() != null && side == getInTwo())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	protected final IAxisHandler handler = new AxisHandler();
	protected final ISlaveAxisHandler slaveHandler = new SlaveAxisHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == getOut())){
			return (T) handler;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == getInOne() || getInTwo() != null && side == getInTwo())){
			return (T) slaveHandler;
		}

		return super.getCapability(cap, side);
	}

	private boolean oneTrig = false;
	private boolean twoTrig = false;

	protected class SlaveAxisHandler implements ISlaveAxisHandler{
		@Override
		public void trigger(EnumFacing side){
			if(getInTwo() == null){
				if(side == getInOne()){
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}
			}else{
				if(side == getInOne()){
					if(twoTrig){
						twoTrig = false;
						oneTrig = false;
						if(!locked && !rotaryMembers.isEmpty()){
							runCalc();
							triggerSlaves();
						}
					}else{
						oneTrig = true;
					}
				}else if(side == getInTwo()){
					if(oneTrig){
						oneTrig = false;
						twoTrig = false;
						if(!locked && !rotaryMembers.isEmpty()){
							runCalc();
							triggerSlaves();
						}
					}else{
						twoTrig = true;
					}
				}
			}
		}

		@Override
		public HashSet<ISlaveAxisHandler> getContainedAxes(){
			HashSet<ISlaveAxisHandler> out = new HashSet<ISlaveAxisHandler>();
			for(Pair<ISlaveAxisHandler, EnumFacing> slave : slaves){
				out.add(slave.getLeft());
			}
			return out;
		}

		@Override
		public boolean isInvalid(){
			return tileEntityInvalid;
		}
	}

	protected class AxisHandler implements IAxisHandler{

		@Override
		public void trigger(IAxisHandler masterIn, byte keyIn){
			if(keyIn != key){
				locked = true;
			}
		}

		protected final Random RAND = new Random();

		@Override
		public void requestUpdate(){
			if(world.isRemote || ModConfig.disableSlaves.getBoolean()){
				return;
			}
			ArrayList<IAxleHandler> memberCopy = new ArrayList<IAxleHandler>(rotaryMembers);
			rotaryMembers.clear();
			locked = false;
			TileEntity te = world.getTileEntity(pos.offset(getOut()));
			if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, getOut().getOpposite())){
				byte keyNew;
				do{
					keyNew = (byte) (RAND.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;

				te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, getOut().getOpposite()).propogate(this, key, 0, 0);
			}

			if(!memberCopy.containsAll(rotaryMembers)){
				for(IAxleHandler axle : rotaryMembers){
					axle.resetAngle();
				}
			}

			memberCopy.removeAll(rotaryMembers);
			for(IAxleHandler axle : memberCopy){
				//For 0-mass gears.
				axle.getMotionData()[0] = 0;
				axle.syncAngle();
			}
		}

		@Override
		public void lock(){
			locked = true;
			for(IAxleHandler gear : rotaryMembers){
				gear.getMotionData()[0] = 0;
				gear.getMotionData()[1] = 0;
				gear.getMotionData()[2] = 0;
				gear.getMotionData()[3] = 0;
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
		public void addAxisToList(ISlaveAxisHandler handler, EnumFacing side){
			if(RotaryUtil.contains(slaveHandler, handler)){
				world.destroyBlock(pos, true);
				return;
			}
			slaves.add(Pair.of(handler, side));
		}

		@Override
		public double getTotalEnergy(){
			return sumEnergy;
		}
	}
}
