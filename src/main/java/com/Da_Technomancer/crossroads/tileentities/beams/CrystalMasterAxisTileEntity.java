package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.redstone.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CrystalMasterAxisTileEntity extends MasterAxisTileEntity implements IInfoTE{

	private double lastSumEnergy;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		chat.add("Element: " + (currentElement == null ? "NONE" : currentElement.getLocalName(time < 0) + ", Time: " + time));
	}

	public EnumBeamAlignments getElement(){
		return currentElement;
	}

	public int getTime(){
		return time;
	}

	@Override
	protected void runCalc(){
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
				sumEnergy += ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.crystalAxisMult).get() * (Math.signum(sumEnergy) == 0 ? 1 : Math.signum(sumEnergy));
			}else if(currentElement == EnumBeamAlignments.CHARGE){
				sumEnergy += ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.crystalAxisMult).get();
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
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putInt("time", time);
		if(currentElement != null){
			nbt.putString("elem", currentElement.name());
		}
		return nbt;
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		time = nbt.getInt("time");
		currentElement = nbt.contains("elem") ? EnumBeamAlignments.valueOf(nbt.getString("elem")) : null;
	}

	@Override
	public void update(){
		super.update();

		if(!world.isRemote && currentElement != null && time-- <= 0){
			currentElement = null;
			time = 0;
		}
	}

	private final IBeamHandler magicHandler = new BeamHandler();
	private final RedstoneHandler redsHandler = new RedstoneHandler();

	@Override
	public boolean hasCapability(Capability<?> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && side != getFacing()){
			return true;
		}
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return true;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return true;
		}

		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && side != getFacing()){
			return (T) magicHandler;
		}
		if(cap == Capabilities.AXIS_CAPABILITY && (side == null || side == getFacing())){
			return (T) handler;
		}
		if(cap == Capabilities.ADVANCED_REDSTONE_CAPABILITY){
			return (T) redsHandler;
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
					if(mag.getVoid() == 0){
						time = mag.getPower() * BeamManager.BEAM_TIME;
					}
				}else{
					time = Math.max(mag.getVoid() == 0 ? time + mag.getPower() * BeamManager.BEAM_TIME : time - mag.getPower() * BeamManager.BEAM_TIME, 0);
				}
			}
		}
	}

	public int getRedstone(){
		return (int) Math.min(15, time);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			return read ? time : 0;
		}
	}
}
