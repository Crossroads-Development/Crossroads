package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.API.technomancy.FieldWorldSavedData;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.common.capabilities.Capability;

public class FluxReaderAxisTileEntity extends TileEntity implements ITickable{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing facing;
	private byte key;

	public FluxReaderAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	public FluxReaderAxisTileEntity(EnumFacing facingIn){
		super();
		facing = facingIn;
	}
	
	private void runCalc(){
		FieldWorldSavedData data = FieldWorldSavedData.get(world);
		double baseSpeed = data.fieldNodes.containsKey(MiscOp.getLongFromChunkPos(new ChunkPos(pos))) ? EnergyConverters.SPEED_PER_FLUX * data.fieldNodes.get(MiscOp.getLongFromChunkPos(new ChunkPos(pos)))[0][MiscOp.getChunkRelativeCoord(pos.getX()) / 2][MiscOp.getChunkRelativeCoord(pos.getZ()) / 2] : 0;
		
		double sumIRot = 0;
		sumEnergy = 0;
		
		double cost = 0;
		
		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getPhysData()[1] * Math.pow(gear.getRotationRatio(), 2);
			sumEnergy += MiscOp.posOrNeg(gear.getRotationRatio()) * gear.getMotionData()[1];
			cost += Math.abs(gear.getMotionData()[1] * Math.pow(1.001D, -Math.abs(gear.getMotionData()[0])));
		}
		
		cost += sumIRot * Math.pow(baseSpeed, 2) / 2D;
		
		double availableEnergy = Math.abs(sumEnergy) + Math.abs(world.getTileEntity(pos.offset(facing.getOpposite())) != null && world.getTileEntity(pos.offset(facing.getOpposite())).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) ? world.getTileEntity(pos.offset(facing.getOpposite())).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] : 0);
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
		
		if(world.getTileEntity(pos.offset(facing.getOpposite())) != null && world.getTileEntity(pos.offset(facing.getOpposite())).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing)){
			world.getTileEntity(pos.offset(facing.getOpposite())).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1] = availableEnergy * MiscOp.posOrNeg(world.getTileEntity(pos.offset(facing.getOpposite())).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[1], 1);
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
	private static final int updateTime = ModConfig.gearResetTime.getInt();
	
	@Override
	public void update(){
		if(world.isRemote){
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

		if(!locked && !rotaryMembers.isEmpty()){
			runCalc();
			triggerSlaves();
		}
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
			if(world.getTileEntity(pos.offset(facing)) != null && world.getTileEntity(pos.offset(facing)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
				byte keyNew;
				do {
					keyNew = (byte) (rand.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;
				
				world.getTileEntity(pos.offset(facing)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).propogate(this, key, 0, 0);
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
			slaves.add(Pair.of(handler, side));
		}
		
		@Override
		public double getTotalEnergy(){
			return sumEnergy;
		}
	}
}
