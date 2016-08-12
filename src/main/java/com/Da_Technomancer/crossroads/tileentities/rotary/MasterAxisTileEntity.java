package com.Da_Technomancer.crossroads.tileentities.rotary;

import java.util.ArrayList;
import java.util.Random;

import com.Da_Technomancer.crossroads.ServerProxy;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscOperators;
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
		if(getWorld().isRemote){
			return;
		}
		rotaryMembers.clear();
		locked = false;
		Random rand = new Random();
		if(getWorld().getTileEntity(getPos().offset(facing)) != null && getWorld().getTileEntity(getPos().offset(facing)).hasCapability(Capabilities.ROTARY_HANDLER_CAPABILITY,  facing.getOpposite())){
			key = rand.nextInt(100) + 1;
			getWorld().getTileEntity(getPos().offset(facing)).getCapability(Capabilities.ROTARY_HANDLER_CAPABILITY, facing.getOpposite()).propogate(key, this);
		}
	}
	
	//For debugging mainly
	public double getTotalEnergy(){
		return sumEnergy;
	}
	
	private void runCalc(){
		double sumMass = 0;
		sumEnergy = 0;
		//The standard variable is V, not Q. I like Q because reasons.
		double Q = 0;
		//I should mention that I use I = mr*r/2 in the code because I don't want to code
		//calculus, also, no user wants to do calculus. This isn't perfect, but it's pretty close for a simple gear shape, FYI.
		
		//Also, IRL you wouldn't say a gear spinning a different direction has negative energy, but it makes the code easier.
		
		for(IRotaryHandler gear: rotaryMembers){
			sumMass += gear.getPhysData()[1];
		}
		
		sumEnergy = runLoss(rotaryMembers, 1D);
		if(sumEnergy < 1 && sumEnergy > -1){
			sumEnergy = 0;
		}
		
		boolean QFound = false;
		for(IRotaryHandler gear: rotaryMembers){
			
			double newEnergy = 0;
			
			if(QFound){
				//set w
				gear.getMotionData()[0] = gear.keyType() * Q / gear.getPhysData()[0];
				//set energy
				newEnergy = MiscOperators.posOrNeg(gear.getMotionData()[0]) * Math.pow(gear.getMotionData()[0], 2) * gear.getPhysData()[2] / 2D;
				gear.getMotionData()[1] = newEnergy;
				gear.setQ(lastQ, false);
			}else{
				//set energy
				newEnergy = gear.keyType() * sumEnergy * gear.getPhysData()[1] / sumMass;
				gear.getMotionData()[1] = newEnergy;
				//set w
				gear.getMotionData()[0] = MiscOperators.posOrNeg(newEnergy) * Math.sqrt(Math.abs(newEnergy * 2D / gear.getPhysData()[2]));
				//set Q
				Q = gear.getMotionData()[0] * gear.getPhysData()[0];
				QFound = true;
				lastQ = Q;
				gear.setQ(lastQ, false);
			}
			//set power
			gear.getMotionData()[2] = (newEnergy - gear.getMotionData()[3]) * 20;
			//set lastE
			gear.getMotionData()[3] = newEnergy;
		}
	}
	
	//The multiplier is badly named, the exact effect it has on loss is actually convoluted, but as a rule, higher multiplier means lower loss
	//A multiplier of 0 means no power loss
	private static double runLoss(ArrayList<IRotaryHandler> gears, double multiplier){
		double sumEnergy = 0;
		
		for(IRotaryHandler gear: gears){
			if(multiplier == 0){
				sumEnergy += gear.keyType() * gear.getMotionData()[1];
			}else{
				sumEnergy += gear.keyType() * gear.getMotionData()[1] * (gear.getPhysData()[2] * multiplier/ (Math.abs(gear.getMotionData()[0]) + (multiplier * gear.getPhysData()[2])));
			}
		}
		
		return sumEnergy;
	}
	
	@Override
	public void lock(){
		locked = true;
		for(IRotaryHandler gear: rotaryMembers){
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
	public void trigger(int keyIn, ITileMasterAxis masterIn, EnumFacing side) {
		if(!locked && side == facing && keyIn != key){
			masterIn.lock();
			requestUpdate();
		}
		
	}
	
	@Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("facing", this.facing.getIndex());

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        this.facing = EnumFacing.getFront(compound.getInteger("facing"));

    }
	
	
	private int lastKey = 0;
	
	@Override
	public void update() {
		if(getWorld().isRemote){
			return;
		}
		
		ticksExisted++;
		
		if(ticksExisted % 100 == 0 || ServerProxy.masterKey != lastKey){	
			requestUpdate();
			
		}
		
		if(ticksExisted % 300 == 0 || ServerProxy.masterKey != lastKey){
			for(IRotaryHandler gear: rotaryMembers){
				gear.resetAngle();
			}
		}
		
		lastKey = ServerProxy.masterKey;
		
		if(!locked){
			runCalc();
		}
	}
}	
	