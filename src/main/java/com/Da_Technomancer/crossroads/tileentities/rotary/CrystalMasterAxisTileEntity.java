package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Random;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class CrystalMasterAxisTileEntity extends TileEntity implements ITileMasterAxis, ITickable{

	private ArrayList<IAxleHandler> rotaryMembers = new ArrayList<IAxleHandler>();

	private boolean locked = false;
	private double sumEnergy = 0;
	private int ticksExisted = 0;
	private EnumFacing facing;
	private byte key;

	public CrystalMasterAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	@Override
	public boolean isLocked(){
		return locked;
	}

	public CrystalMasterAxisTileEntity(EnumFacing facingIn){
		facing = facingIn;
	}

	@Override
	public void requestUpdate(){
		if(worldObj.isRemote){
			return;
		}
		ArrayList<IAxleHandler> memberCopy = new ArrayList<IAxleHandler>();
		memberCopy.addAll(rotaryMembers);
		rotaryMembers.clear();
		locked = false;
		Random rand = new Random();
		if(worldObj.getTileEntity(pos.offset(facing)) != null && worldObj.getTileEntity(pos.offset(facing)).hasCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite())){
			byte keyNew;
			do {
				keyNew = (byte) (rand.nextInt(100) + 1);
			}while(key == keyNew);
			key = keyNew;
			
			worldObj.getTileEntity(pos.offset(facing)).getCapability(Capabilities.AXLE_HANDLER_CAPABILITY, facing.getOpposite()).propogate(this, key, 1, 0);
		}
		if(!memberCopy.containsAll(rotaryMembers) || !rotaryMembers.containsAll(memberCopy)){
			for(IAxleHandler gear : rotaryMembers){
				gear.resetAngle();
			}
		}
	}

	@Override
	public double getTotalEnergy(){
		return sumEnergy;
	}

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
		
		sumEnergy = runLoss(rotaryMembers, currentElement == MagicElements.EQUALIBRIUM ? (voi ? 1.5D : 1D) : 1.001D);
		sumEnergy += MiscOp.posOrNeg(sumEnergy) * (currentElement == MagicElements.ENERGY ? (voi ? -10 : 10) : 0);
		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}
		
		for(IAxleHandler gear : rotaryMembers){
			double newEnergy = 0;

			// set w
			gear.getMotionData()[0] = MiscOp.posOrNeg(sumEnergy) * MiscOp.posOrNeg(gear.getRotationRatio()) * Math.sqrt(Math.abs(sumEnergy) * 2D * Math.pow(gear.getRotationRatio(), 2) / sumIRot);
			// set energy
			newEnergy = MiscOp.posOrNeg(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[1] / 2D;
			gear.getMotionData()[1] = newEnergy;
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;
		}
	}

	/**
	 * base should always be equal or greater than one. 1 means no loss. 
	*/
	private static double runLoss(ArrayList<IAxleHandler> gears, double base){
		double sumEnergy = 0;

		for(IAxleHandler gear : gears){
			sumEnergy += MiscOp.posOrNeg(gear.getRotationRatio()) * gear.getMotionData()[1] * Math.pow(base, -Math.abs(gear.getMotionData()[0]));
		}

		return sumEnergy;
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
	public boolean addToList(IAxleHandler handler){
		if(!locked){
			rotaryMembers.add(handler);
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void trigger(byte keyIn, ITileMasterAxis masterIn, EnumFacing side){
		if(!locked && side == facing && keyIn != key){
			masterIn.lock();
			requestUpdate();
		}

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

	private int lastKey = 0;
	private boolean forceUpdate;
	
	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		ticksExisted++;
		
		if(ticksExisted % 300 == 20 || forceUpdate){
			requestUpdate();
		}
		
		forceUpdate = CommonProxy.masterKey != lastKey;

		if(ticksExisted % 300 == 20){
			for(IAxleHandler gear : rotaryMembers){
				gear.resetAngle();
			}
		}
		
		lastKey = CommonProxy.masterKey;

		if(currentElement != null && time-- <= 0){
			currentElement = null;
			time = 0;
			voi = false;
		}
		
		if(!locked && !rotaryMembers.isEmpty()){
			runCalc();
		}
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != facing){
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
}
