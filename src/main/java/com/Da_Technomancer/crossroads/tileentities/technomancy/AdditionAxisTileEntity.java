package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.packets.IDoubleReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendDoubleToClient;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class AdditionAxisTileEntity extends TileEntity implements ITickable, IDoubleReceiver{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

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
	public void receiveDouble(String context, double message){
		if(context.equals("one")){
			lastInPos = message;
		}else if(context.equals("two")){
			lastInNeg = message;
		}
	}
	
	//On the server side these serve as a record of what was sent to the client, but on the client this is the received data for rendering. 
	public double lastInPos;
	public double lastInNeg;
	public double angleOne;
	public double angleTwo;
	public double angleThree;
	
	private void runCalc(){
		double inPos = world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis))) != null && world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis))).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)) ? world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis))).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis)).getMotionData()[0] : 0;
		double inNeg = world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))) != null && world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)) ? world.getTileEntity(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.getFacingFromAxis(AxisDirection.POSITIVE, axis)).getMotionData()[0] : 0;
		if(world.getBlockState(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))) != null && world.getBlockState(pos.offset(EnumFacing.getFacingFromAxis(AxisDirection.NEGATIVE, axis))).getBlock() == ModBlocks.axle){
			inNeg *= -1D;
		}
		double baseSpeed = inPos + inNeg;
		
		double sumIRot = 0;
		sumEnergy = 0;

		double cost = 0;

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getPhysData()[1] * Math.pow(gear.getRotationRatio(), 2);
			sumEnergy += MiscOp.posOrNeg(gear.getRotationRatio()) * gear.getMotionData()[1];
			cost += Math.abs(gear.getMotionData()[1] * (1D - Math.pow(1.001D, -Math.abs(gear.getMotionData()[0]))));
		}

		cost += sumIRot * Math.pow(baseSpeed, 2) / 2D;

		double availableEnergy = Math.abs(sumEnergy) + Math.abs(world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null && world.getTileEntity(pos.offset(EnumFacing.DOWN)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP) ? world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1] : 0);
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
			newEnergy = MiscOp.posOrNeg(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[1] / 2D;
			gear.getMotionData()[1] = newEnergy;
			sumEnergy += newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;
			
			gear.markChanged();
		}

		if(world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null && world.getTileEntity(pos.offset(EnumFacing.DOWN)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP)){
			world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1] = availableEnergy * MiscOp.posOrNeg(world.getTileEntity(pos.offset(EnumFacing.DOWN)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.UP).getMotionData()[1], 1);
		}
		
		inPos *= -1D;
		
		if(MiscOp.tiersRound(inPos, ModConfig.speedTiers.getInt()) != lastInPos){
			lastInPos = MiscOp.tiersRound(inPos, ModConfig.speedTiers.getInt());
			ModPackets.network.sendToAllAround(new SendDoubleToClient("one", lastInPos, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		if(MiscOp.tiersRound(inNeg, ModConfig.speedTiers.getInt()) != lastInNeg){
			lastInNeg = MiscOp.tiersRound(inNeg, ModConfig.speedTiers.getInt());
			ModPackets.network.sendToAllAround(new SendDoubleToClient("two", lastInNeg, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
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
	private static final int updateTime = ModConfig.gearResetTime.getInt();
	
	@Override
	public void update(){
		if(world.isRemote){
			angleOne += Math.toDegrees(lastInPos / 20D);
			angleTwo += Math.toDegrees(lastInNeg / 20D);
			angleThree += Math.toDegrees((lastInNeg - lastInPos) / 20D);
			return;
		}

		ticksExisted++;

		if(ticksExisted % updateTime == 20 || forceUpdate){
			handler.requestUpdate();
		}

		forceUpdate = CommonProxy.masterKey != lastKey;

		if(ticksExisted % updateTime == 20){
			for(IAxleHandler gear : rotaryMembers){
				gear.resetAngle();
			}
		}

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

		@Override
		public void requestUpdate(){
			if(world.isRemote || ModConfig.disableSlaves.getBoolean()){
				return;
			}
			ArrayList<IAxleHandler> memberCopy = new ArrayList<IAxleHandler>();
			memberCopy.addAll(rotaryMembers);
			for(IAxleHandler axle : memberCopy){
				//For 0-mass gears.
				axle.getMotionData()[0] = 0;
			}
			rotaryMembers.clear();
			locked = false;
			Random rand = new Random();
			if(world.getTileEntity(pos.offset(EnumFacing.UP)) != null && world.getTileEntity(pos.offset(EnumFacing.UP)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN)){
				byte keyNew;
				do {
					keyNew = (byte) (rand.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;

				world.getTileEntity(pos.offset(EnumFacing.UP)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).propogate(this, key, 0, 0);
			}
			if(!memberCopy.containsAll(rotaryMembers) || !rotaryMembers.containsAll(memberCopy)){
				for(IAxleHandler gear : rotaryMembers){
					gear.resetAngle();
				}
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
