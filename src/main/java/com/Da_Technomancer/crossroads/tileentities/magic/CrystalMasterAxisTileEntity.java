package com.Da_Technomancer.crossroads.tileentities.magic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Pair;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.effects.goggles.IGoggleInfoTE;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.IAxisHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ISlaveAxisHandler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class CrystalMasterAxisTileEntity extends TileEntity implements ITickable, IGoggleInfoTE{

	@Override
	public void addInfo(ArrayList<String> chat, GoggleLenses lens, EntityPlayer player, @Nullable EnumFacing side){
		if(lens == GoggleLenses.QUARTZ){
			chat.add("Element: " + (currentElement == null ? "NONE" : currentElement.toString() + (voi ? " (VOID), " : ", ") + "Time: " + time));
		}
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

	public MagicElements getElement(){
		return currentElement;
	}

	public boolean isVoid(){
		return voi;
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
			sumIRot += gear.getPhysData()[1] * Math.pow(gear.getRotationRatio(), 2);
		}

		if(sumIRot == 0 || sumIRot != sumIRot){
			return;
		}

		sumEnergy = runLoss(rotaryMembers, currentElement == MagicElements.STABILITY ? (voi ? 1.5D : 1D) : 1.001D);
		sumEnergy += Math.signum(sumEnergy) * (currentElement == MagicElements.ENERGY ? (voi ? -10 : 10) : 0);
		sumEnergy += currentElement == MagicElements.CHARGE ? (voi ? -10 : 10) : 0;
		sumEnergy = currentElement == MagicElements.EQUALIBRIUM ? (voi ? ((7D * sumEnergy) - (3D * lastSumEnergy)) / 4D : (sumEnergy + (3D * lastSumEnergy)) / 4D) : sumEnergy;

		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}

		lastSumEnergy = sumEnergy;

		for(IAxleHandler gear : rotaryMembers){
			double newEnergy = 0;

			// set w
			gear.getMotionData()[0] = Math.signum(sumEnergy) * Math.signum(gear.getRotationRatio()) * Math.sqrt(Math.abs(sumEnergy) * 2D * Math.pow(gear.getRotationRatio(), 2) / sumIRot);
			// set energy
			newEnergy = Math.signum(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[1] / 2D;
			gear.getMotionData()[1] = newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;

			gear.markChanged();
		}
	}

	/**
	 * base should always be equal or greater than one. 1 means no loss. 
	 */
	private static double runLoss(ArrayList<IAxleHandler> gears, double base){
		double sumEnergy = 0;

		for(IAxleHandler gear : gears){
			sumEnergy += Math.signum(gear.getRotationRatio()) * gear.getMotionData()[1] * Math.pow(base, -Math.abs(gear.getMotionData()[0]));
		}
		return sumEnergy;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("facing", this.facing.getIndex());
		nbt.setInteger("time", time);
		nbt.setBoolean("voi", voi);
		if(currentElement != null){
			nbt.setString("elem", currentElement.name());
		}
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		facing = EnumFacing.getFront(nbt.getInteger("facing"));
		time = nbt.getInteger("time");
		voi = nbt.getBoolean("voi");
		currentElement = nbt.hasKey("elem") ? MagicElements.valueOf(nbt.getString("elem")) : null;
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
			voi = false;
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

	private final HashSet<Pair<ISlaveAxisHandler, EnumFacing>> slaves = new HashSet<Pair<ISlaveAxisHandler, EnumFacing>>();
	private final IMagicHandler magicHandler = new MagicHandler();
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

	private MagicElements currentElement;
	private boolean voi;
	private int time;

	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			if(mag != null){
				MagicElements newElem = MagicElements.getElement(mag);
				if(newElem != currentElement || voi != (mag.getVoid() != 0)){
					currentElement = newElem;
					voi = mag.getVoid() != 0;
					time = mag.getPower() * IMagicHandler.BEAM_TIME;
				}else{
					time += mag.getPower() * IMagicHandler.BEAM_TIME;
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
			slaves.add(Pair.of(handler, side));
		}

		@Override
		public double getTotalEnergy(){
			return sumEnergy;
		}
	}
}
