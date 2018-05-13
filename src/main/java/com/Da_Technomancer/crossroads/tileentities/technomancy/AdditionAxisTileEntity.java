package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class AdditionAxisTileEntity extends TileEntity implements ITickable, ISpinReceiver{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
			//For 0-mass gears.
			axle.getMotionData()[0] = 0;
			axle.syncAngle();
		}
		CommonProxy.masterKey++;
	}
	
	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing.Axis axis;
	private byte key;

	public AdditionAxisTileEntity(){
		this(EnumFacing.Axis.X);
	}

	public AdditionAxisTileEntity(EnumFacing.Axis axisIn){
		super();
		axis = axisIn;
	}

	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		//No point synchronizing angle. 

		if(identifier == 0){
			lastInPos = clientW;
		}else if(identifier == 1){
			lastInNeg = clientW;
		}
	}

	//On the server side these serve as a record of what was sent to the client, but on the client this is the received data for rendering. 
	public float lastInPos;
	public float lastInNeg;
	public double angleOne;
	public double angleTwo;
	public double angleThree;

	private void runCalc(){
		TileEntity posTE = world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)));
		double inPos = posTE != null && posTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)) ? posTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)).getMotionData()[0] : 0;
		TileEntity negTE = world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)));
		double inNeg = negTE != null && negTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)) ? negTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)).getMotionData()[0] : 0;
		if(world.getBlockState(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))).getBlock() == ModBlocks.axle){
			inNeg *= -1D;
		}
		double baseSpeed = inPos + inNeg;

		double sumIRot = 0;
		sumEnergy = 0;

		double cost = 0;

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getMoInertia() * Math.pow(gear.getRotationRatio(), 2);
			sumEnergy += Math.signum(gear.getRotationRatio()) * gear.getMotionData()[1];
			cost += Math.abs(gear.getMotionData()[1] * (1D - Math.pow(1.001D, -Math.abs(gear.getMotionData()[0]))));
		}

		cost += sumIRot * Math.pow(baseSpeed, 2) / 2D;

		TileEntity downTE = world.getTileEntity(pos.offset(EnumFacing.DOWN));

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

		inPos *= -1D;

		if(Math.abs(lastInPos - inPos) >= CLIENT_SPEED_MARGIN){
			lastInPos = (float) inPos;
			ModPackets.network.sendToAllAround(new SendSpinToClient(0, lastInPos, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		if(Math.abs(inNeg - lastInNeg) >= CLIENT_SPEED_MARGIN){
			lastInNeg = (float) inNeg;
			ModPackets.network.sendToAllAround(new SendSpinToClient(1, lastInNeg, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}

		runAngleCalc();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("axis", axis == Axis.Z);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		axis = nbt.getBoolean("axis") ? Axis.Z : Axis.X;
	}

	private int lastKey = 0;
	private boolean forceUpdate;
	private static final int UPDATE_TIME = ModConfig.gearResetTime.getInt();

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
	public void update(){
		if(world.isRemote){
			angleOne += Math.toDegrees(lastInPos / 20D);
			angleTwo += Math.toDegrees(lastInNeg / 20D);
			angleThree += Math.toDegrees((lastInNeg - lastInPos) / 20D);
			return;
		}

		ticksExisted++;

		if(ticksExisted % UPDATE_TIME == 20 || forceUpdate){
			handler.requestUpdate();
		}

		forceUpdate = CommonProxy.masterKey != lastKey;

		lastKey = CommonProxy.masterKey;
	}

	private void triggerSlaves(){
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

	private final HashSet<Pair<ISlaveAxisHandler, EnumFacing>> slaves = new HashSet<Pair<ISlaveAxisHandler, EnumFacing>>();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == EnumFacing.UP)){
			return true;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == null || side.getAxis() == axis)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	private final IAxisHandler handler = new AxisHandler();
	private final ISlaveAxisHandler slaveHandler = new SlaveAxisHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == EnumFacing.UP)){
			return (T) handler;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == null || side.getAxis() == axis)){
			return (T) slaveHandler;
		}

		return super.getCapability(cap, side);
	}

	private boolean posTrig = false;
	private boolean negTrig = false;

	private class SlaveAxisHandler implements ISlaveAxisHandler{

		@Override
		public void trigger(EnumFacing side){
			if(side == EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)){
				if(negTrig){
					negTrig = false;
					posTrig = false;
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}else{
					posTrig = true;
				}
			}else if(side == EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)){
				if(posTrig){
					negTrig = false;
					posTrig = false;
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}else{
					negTrig = true;
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
			TileEntity te = world.getTileEntity(pos.offset(EnumFacing.UP));
			if(te != null && te.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				byte keyNew;
				do {
					keyNew = (byte) (RAND.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;

				te.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).propogate(this, key, 0, 0);
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
			if(DefaultAxisHandler.contains(slaveHandler, handler)){
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
