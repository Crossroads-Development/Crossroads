package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Random;

import com.Da_Technomancer.crossroads.CommonProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.rotary.IRotaryHandler;
import com.Da_Technomancer.crossroads.API.rotary.ITileMasterAxis;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

public class MasterAxisTileEntity extends TileEntity implements ITileMasterAxis, ITickable{

	private ArrayList<IRotaryHandler> rotaryMembers = new ArrayList<IRotaryHandler>();

	private boolean locked = false;

	private double sumEnergy = 0;

	private int ticksExisted = 0;

	private double lastQ = 0;

	private EnumFacing facing;

	private int key;

	public MasterAxisTileEntity(){
		this(EnumFacing.NORTH);
	}

	@Override
	public boolean isLocked(){
		return locked;
	}

	public MasterAxisTileEntity(EnumFacing facingIn){
		facing = facingIn;
	}

	@Override
	public void requestUpdate(){
		if(worldObj.isRemote){
			return;
		}
		ArrayList<IRotaryHandler> memberCopy = new ArrayList<IRotaryHandler>();
		memberCopy.addAll(rotaryMembers);
		rotaryMembers.clear();
		locked = false;
		Random rand = new Random();
		if(worldObj.getTileEntity(pos.offset(facing)) != null && worldObj.getTileEntity(pos.offset(facing)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite())){
			key = rand.nextInt(100) + 1;
			worldObj.getTileEntity(pos.offset(facing)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite()).propogate(key, this);
		}
		if(!memberCopy.containsAll(rotaryMembers) || !rotaryMembers.containsAll(memberCopy)){
			for(IRotaryHandler gear : rotaryMembers){
				gear.resetAngle();
			}
		}
	}

	@Override
	public double getTotalEnergy(){
		return sumEnergy;
	}

	private void runCalc(){
		double sumMass = 0;
		sumEnergy = 0;
		// The standard variable is V, not Q. I like Q because reasons.
		double Q = 0;
		// I should mention that I use I = mr*r/2 in the code because I don't
		// want to code calculus, also, no user wants to do calculus. 
		// This isn't perfect, but it's pretty close for a simple gear shape.
		
		// Also, IRL you wouldn't say a gear spinning a different direction has
		// negative energy, but it makes the code easier.

		for(IRotaryHandler gear : rotaryMembers){
			sumMass += gear.getPhysData()[1];
		}

		sumEnergy = runLoss(rotaryMembers, timer < 0 ? 1.5D : timer > 0 ? 1 : 1.001D);
		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}

		boolean QFound = false;
		for(IRotaryHandler gear : rotaryMembers){

			double newEnergy = 0;

			if(QFound){
				// set w
				gear.getMotionData()[0] = gear.keyType() * Q / gear.getPhysData()[0];
				// set energy
				newEnergy = MiscOp.posOrNeg(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[2] / 2D;
				gear.getMotionData()[1] = newEnergy;
				gear.setQ(lastQ);
			}else{
				// set energy
				newEnergy = gear.keyType() * sumEnergy * gear.getPhysData()[1] / sumMass;
				gear.getMotionData()[1] = newEnergy;
				// set w
				gear.getMotionData()[0] = MiscOp.posOrNeg(newEnergy) * Math.sqrt(Math.abs(newEnergy * 2D / gear.getPhysData()[2]));
				// set Q
				Q = gear.getMotionData()[0] * gear.getPhysData()[0];
				QFound = true;
				lastQ = Q;
				gear.setQ(lastQ);
			}
			// set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			// set lastE
			gear.getMotionData()[3] = newEnergy;
		}
	}

	/**The multiplier is badly named, the exact effect it has on loss is
	* actually exponential, but as a rule, higher multiplier means higher loss.
	* Multiplier should always be equal or greater than one. 1 means no loss. 
	*/
	private static double runLoss(ArrayList<IRotaryHandler> gears, double multiplier){
		double sumEnergy = 0;

		for(IRotaryHandler gear : gears){
			sumEnergy += gear.keyType() * gear.getMotionData()[1] * Math.pow(multiplier, -Math.abs(gear.getMotionData()[0]));
		}

		return sumEnergy;
	}

	@Override
	public void lock(){
		locked = true;
		for(IRotaryHandler gear : rotaryMembers){
			gear.getMotionData()[0] = 0;
			gear.getMotionData()[0] = 1;
			gear.getMotionData()[0] = 2;
			gear.getMotionData()[0] = 3;
		}
		lastQ = 0;
	}

	@Override
	public boolean addToList(IRotaryHandler handler){
		if(!locked){
			rotaryMembers.add(handler);
			return false;
		}else{
			return true;
		}
	}

	@Override
	public void trigger(int keyIn, ITileMasterAxis masterIn, EnumFacing side){
		if(!locked && side == facing && keyIn != key){
			masterIn.lock();
			requestUpdate();
		}

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("facing", this.facing.getIndex());
		nbt.setInteger("time", timer);

		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		this.facing = EnumFacing.getFront(nbt.getInteger("facing"));
		timer = nbt.getInteger("time");

	}

	private int lastKey = 0;

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		ticksExisted++;

		if(ticksExisted % 300 == 0 || CommonProxy.masterKey != lastKey){
			requestUpdate();

		}

		if(ticksExisted % 300 == 0){
			for(IRotaryHandler gear : rotaryMembers){
				gear.resetAngle();
			}
		}
		
		lastKey = CommonProxy.masterKey;

		if(!locked){
			runCalc();
		}
		
		if(timer < 0){
			++timer;
		}else if(timer > 0){
			--timer;
		}
	}

	private int timer;
	
	@Override
	public void addTimer(int ticks){
		timer += ticks;
	}
}
