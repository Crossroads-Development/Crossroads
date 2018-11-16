package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.essentials.shared.IAxisHandler;
import com.Da_Technomancer.essentials.shared.IAxleHandler;
import com.Da_Technomancer.essentials.shared.ISlaveAxisHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class CrystalMasterAxisTileEntity extends TileEntity implements ITickable, IInfoTE{

	private static final Random RAND = new Random();

	public void disconnect(){
		for(IAxleHandler axle : rotaryMembers){
			//For 0-mass gears.
			axle.getMotionData()[0] = 0;
			axle.getMotionData()[2] = 0;
			axle.getMotionData()[3] = 0;
			axle.syncAngle();
			axle.disconnect();
		}
		CommonProxy.masterKey++;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Element: " + (currentElement == null ? "NONE" : currentElement.toString() + ", Time: " + time));
	}

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing facing;
	private byte key;

	public CrystalMasterAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	public EnumBeamAlignments getElement(){
		return currentElement;
	}

	public int getTime(){
		return time;
	}

	public CrystalMasterAxisTileEntity(EnumFacing facingIn){
		super();
		facing = facingIn;
	}

	private double lastSumEnergy;

	private void runCalc(){
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

		if(currentElement == EnumBeamAlignments.STABILITY){
			for(IAxleHandler gear : rotaryMembers){
				sumEnergy += Math.signum(gear.getRotationRatio()) * gear.getMotionData()[1];
			}
		}else{
			sumEnergy = RotaryUtil.getTotalEnergy(rotaryMembers);
			if(currentElement == EnumBeamAlignments.ENERGY){
				sumEnergy += ModConfig.getConfigDouble(ModConfig.crystalAxisMult, false) * (Math.signum(sumEnergy) == 0 ? 1 : Math.signum(sumEnergy));
			}else if(currentElement == EnumBeamAlignments.CHARGE){
				sumEnergy += ModConfig.getConfigDouble(ModConfig.crystalAxisMult, false);
			}else if(currentElement == EnumBeamAlignments.EQUALIBRIUM){
				sumEnergy = (sumEnergy + 3D * lastSumEnergy) / 4D;
			}
		}

		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}

		lastSumEnergy = sumEnergy;

		for(IAxleHandler gear : rotaryMembers){
			// set w
			gear.getMotionData()[0] = Math.signum(sumEnergy) * Math.signum(gear.getRotationRatio()) * Math.sqrt(Math.abs(sumEnergy) * 2D * Math.pow(gear.getRotationRatio(), 2) / sumIRot);
			// set energy
			double newEnergy = Math.signum(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getMoInertia() / 2D;
			gear.getMotionData()[1] = newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;

			gear.markChanged();
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("facing", facing.getIndex());
		nbt.setInteger("time", time);
		if(currentElement != null){
			nbt.setString("elem", currentElement.name());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		facing = EnumFacing.byIndex(nbt.getInteger("facing"));
		time = nbt.getInteger("time");
		currentElement = nbt.hasKey("elem") ? EnumBeamAlignments.valueOf(nbt.getString("elem")) : null;
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

		if(currentElement != null && time-- <= 0){
			currentElement = null;
			time = 0;
		}

		if(!locked && !rotaryMembers.isEmpty()){
			runCalc();
			runAngleCalc();
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

	private final HashSet<Pair<ISlaveAxisHandler, EnumFacing>> slaves = new HashSet<>();
	private final IBeamHandler magicHandler = new BeamHandler();
	private final IAxisHandler handler = new AxisHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != facing){
			return true;
		}
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != facing){
			return (T) magicHandler;
		}
		if(cap == Capabilities.AXIS_HANDLER_CAPABILITY && (side == null || side == facing)){
			return (T) handler;
		}

		return super.getCapability(cap, side);
	}

	private EnumBeamAlignments currentElement;
	private int time;

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(mag != null){
				EnumBeamAlignments newElem = EnumBeamAlignments.getAlignment(mag);
				if(newElem != currentElement){
					currentElement = newElem;
					time = mag.getPower() * BeamManager.BEAM_TIME;
				}else{
					time = Math.max(mag.getVoid() == 0 ? time + mag.getPower() * BeamManager.BEAM_TIME : time - mag.getPower() * BeamManager.BEAM_TIME, 0);
				}
			}
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
				axle.disconnect();
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
