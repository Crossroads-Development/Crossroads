package com.Da_Technomancer.crossroads.tileentities.technomancy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.packets.ISpinReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendSpinToClient;
import com.Da_Technomancer.crossroads.API.rotary.DefaultAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;
import com.Da_Technomancer.crossroads.blocks.ModBlocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class MultiplicationAxisTileEntity extends TileEntity implements ITickable, ISpinReceiver{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing facing;
	private byte key;

	public MultiplicationAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	public MultiplicationAxisTileEntity(EnumFacing facingIn){
		super();
		facing = facingIn;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return newState.getBlock() != oldState.getBlock();
	}
	
	@Override
	public void receiveSpin(int identifier, float clientW, float angle){
		//No point in syncing angle. 
		
		if(identifier == 0){
			lastInOne = clientW;
		}else if(identifier == 1){
			lastInTwo = clientW;
		}
	}
	
	//On the server side these serve as a record of what was sent to the client, but on the client this is the received data for rendering. 
	public float lastInOne;
	public float lastInTwo;
	public double angleOne;
	public double angleTwo;
	public double angleThree;
	public double angleTwoPos;
	
	private void runCalc(){
		TileEntity backTE = world.getTileEntity(pos.offset(facing.getOpposite()));
		double inOne = backTE != null && backTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing) ? backTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing).getMotionData()[0] : 0;
		TileEntity topTE = world.getTileEntity(pos.offset(EnumFacing.UP));
		double inTwo = topTE != null && topTE.hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN) ? topTE.getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, EnumFacing.DOWN).getMotionData()[0] : 0;
		if(facing.getAxisDirection() == AxisDirection.POSITIVE && world.getBlockState(pos.offset(facing.getOpposite())).getBlock() != ModBlocks.axle){
			inOne *= -1D;
		}
		double baseSpeed = inTwo == 0 ? 0 : world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL) ? -inOne / inTwo : -inOne * inTwo;
		
		double sumIRot = 0;
		sumEnergy = 0;

		for(IAxleHandler gear : rotaryMembers){
			sumIRot += gear.getPhysData()[1] * Math.pow(gear.getRotationRatio(), 2);
			sumEnergy += Math.signum(gear.getRotationRatio()) * gear.getMotionData()[1] * Math.pow(1.001D, -Math.abs(gear.getMotionData()[0]));
		}

		double cost = sumIRot * Math.pow(baseSpeed, 2) / 2D;

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
			newEnergy = Math.signum(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[1] / 2D;
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
		
		if(facing.getAxisDirection() == AxisDirection.NEGATIVE){
			inOne *= -1D;
		}
		if(Math.abs(inOne - lastInOne) >= CLIENT_SPEED_MARGIN){
			lastInOne = (float) inOne;
			ModPackets.network.sendToAllAround(new SendSpinToClient(1, lastInOne, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		if(Math.abs(inTwo - lastInTwo) >= CLIENT_SPEED_MARGIN){
			lastInTwo = (float) inTwo;
			ModPackets.network.sendToAllAround(new SendSpinToClient(1, lastInTwo, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
		
		runAngleCalc();
	}
	
	private static final float CLIENT_SPEED_MARGIN = (float) ModConfig.speedPrecision.getDouble();

	private void runAngleCalc(){
		for(IAxleHandler axle : rotaryMembers){
			if(axle.shouldManageAngle()){
				float axleSpeed = ((float) axle.getMotionData()[0]);
				axle.setAngle(axle.getAngle() + (axleSpeed * 9F / (float) Math.PI));
				if(Math.abs(axleSpeed - axle.getClientW()) >= CLIENT_SPEED_MARGIN){
					axle.syncAngle();
				}
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("facing", facing.getIndex());
		nbt.setFloat("lastOne", lastInOne);
		nbt.setFloat("lastTwo", lastInTwo);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		facing = EnumFacing.getFront(nbt.getInteger("facing"));
		lastInOne = nbt.getFloat("lastOne");
		lastInTwo = nbt.getFloat("lastTwo");
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setFloat("lastOne", lastInOne);
		nbt.setFloat("lastTwo", lastInTwo);
		return nbt;
	}

	private int lastKey = 0;
	private boolean forceUpdate;
	private static final int UPDATE_TIME = ModConfig.gearResetTime.getInt();
	
	@Override
	public void update(){
		if(world.isRemote){
			boolean divide = world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL);
			angleOne += Math.toDegrees((divide ? (lastInTwo == 0 ? 0 : divide ? -lastInOne / lastInTwo : -lastInOne * lastInTwo) : lastInOne) / 20D);
			angleTwo += Math.toDegrees(lastInTwo / 20D);
			angleTwoPos += Math.toDegrees(Math.abs(lastInTwo) / 20D);
			angleThree += Math.toDegrees((divide ? lastInOne : (lastInTwo == 0 ? 0 : divide ? -lastInOne / lastInTwo : -lastInOne * lastInTwo)) / 20D);
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
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return true;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == facing.getOpposite())){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	private final IAxisHandler handler = new AxisHandler();
	private final ISlaveAxisHandler slaveHandler = new SlaveAxisHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return (T) handler;
		}
		if(cap == Capabilities.SLAVE_AXIS_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == facing.getOpposite())){
			return (T) slaveHandler;
		}

		return super.getCapability(cap, side);
	}

	private boolean up = false;
	private boolean back = false;

	private class SlaveAxisHandler implements ISlaveAxisHandler{
		@Override
		public void trigger(EnumFacing side){
			if(side == EnumFacing.UP){
				if(back){
					back = false;
					up = false;
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}else{
					up = true;
				}
			}else if(side == facing.getOpposite()){
				if(up){
					back = false;
					up = false;
					if(!locked && !rotaryMembers.isEmpty()){
						runCalc();
						triggerSlaves();
					}
				}else{
					back = true;
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
			if(world.getTileEntity(pos.offset(facing)) != null && world.getTileEntity(pos.offset(facing)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
				byte keyNew;
				do {
					keyNew = (byte) (rand.nextInt(100) + 1);
				}while(key == keyNew);
				key = keyNew;

				world.getTileEntity(pos.offset(facing)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).propogate(this, key, 0, 0);
			}
			if(!memberCopy.containsAll(rotaryMembers) || !rotaryMembers.containsAll(memberCopy)){
				for(IAxleHandler gear : memberCopy){
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
