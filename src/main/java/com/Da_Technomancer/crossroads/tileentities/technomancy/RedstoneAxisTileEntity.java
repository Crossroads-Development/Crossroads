package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.redstone.RedstoneUtil;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class RedstoneAxisTileEntity extends TileEntity implements ITickable{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<>();

	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing facing;
	private byte key;

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
			//For 0-mass gears.
			axle.getMotionData()[0] = 0;
			axle.syncAngle();
		}
		CommonProxy.masterKey++;
	}
	
	public RedstoneAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	public RedstoneAxisTileEntity(EnumFacing facingIn){
		super();
		facing = facingIn;
	}

	private void runCalc(){
		double baseSpeed = RedstoneUtil.getPowerAtPos(world, pos);

		double sumIRot = 0;
		sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;
		TileEntity backTE = world.getTileEntity(pos.offset(facing.getOpposite()));
		double availableEnergy = Math.abs(sumEnergy) + Math.abs(backTE != null && backTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) ? backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] : 0);
		if(availableEnergy - cost < 0){
			baseSpeed = 0;
			cost = 0;
		}
		availableEnergy -= cost;

		for(IAxleHandler gear : rotaryMembers){
			double newEnergy;

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

		if(backTE != null && backTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
			backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] = availableEnergy * RotaryUtil.posOrNeg(backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1], 1);
		}

		runAngleCalc();
	}

	private static final float CLIENT_SPEED_MARGIN = (float) ModConfig.speedPrecision.getDouble();

	private void runAngleCalc(){
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

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("facing", this.facing.getIndex());

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		facing = EnumFacing.getFront(nbt.getInteger("facing"));
	}

	private int lastKey = 0;
	private boolean forceUpdate;
	private static final int UPDATE_TIME = ModConfig.gearResetTime.getInt();

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

		if(!locked && !rotaryMembers.isEmpty()){
			runCalc();
			triggerSlaves();
		}
	}

	private void triggerSlaves(){
		for(Pair<ISlaveAxisHandler, EnumFacing> slave : slaves){
			slave.getLeft().trigger(slave.getRight());
		}
	}

	private final HashSet<Pair<ISlaveAxisHandler, EnumFacing>> slaves = new HashSet<Pair<ISlaveAxisHandler, EnumFacing>>();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	private final IAxisHandler handler = new AxisHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	private class AxisHandler implements IAxisHandler{

		@Override
		public void trigger(IAxisHandler masterIn, byte keyIn){
			if(keyIn != key){
				locked = true;
			}
		}

		private final Random RAND = new Random();

		@Override
		public void requestUpdate(){
			if(world.isRemote || ModConfig.disableSlaves.getBoolean()){
				return;
			}
			ArrayList<IAxleHandler> memberCopy = new ArrayList<IAxleHandler>(rotaryMembers);
			rotaryMembers.clear();
			locked = false;
			TileEntity te = world.getTileEntity(pos.offset(facing));
			if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
				byte keyNew;
				do {
					keyNew = (byte) (RAND.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;

				te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).propogate(this, key, 0, 0);
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
				gear.markChanged();
				if(gear.shouldManageAngle()){
					gear.syncAngle();
				}
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
			slaves.add(Pair.of(handler, side));
		}

		@Override
		public double getTotalEnergy(){
			return sumEnergy;
		}
	}
}
