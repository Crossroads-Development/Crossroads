package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class CrystalMasterAxisTileEntity extends MasterAxisTileEntity implements IInfoTE{

	@ObjectHolder("crystal_master_axis")
	private static TileEntityType<CrystalMasterAxisTileEntity> type = null;

	private double lastSumEnergy;
	private EnumBeamAlignments currentElement;
	private int time;

	public CrystalMasterAxisTileEntity(){
		super(type);
	}

	@Override
	public void addInfo(ArrayList<ITextComponent> chat, PlayerEntity player, BlockRayTraceResult hit){
		//Known issue: the localization for alignment name happens on the server side
		chat.add(new TranslationTextComponent("tt.crossroads.crystal_master_axis.info", currentElement == null ? MiscUtil.localize("alignment.none") : currentElement.getLocalName(time < 0), Math.abs(time)));
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
				sumEnergy += CRConfig.crystalAxisMult.get() * (Math.signum(sumEnergy) == 0 ? 1 : Math.signum(sumEnergy));
			}else if(currentElement == EnumBeamAlignments.CHARGE){
				sumEnergy += CRConfig.crystalAxisMult.get();
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
	public void tick(){
		super.tick();

		if(!world.isRemote && currentElement != null && time-- <= 0){
			currentElement = null;
			time = 0;
		}
	}

	@Override
	public void remove(){
		super.remove();
		magicOpt.invalidate();
	}

	private final LazyOptional<IBeamHandler> magicOpt = LazyOptional.of(BeamHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.BEAM_CAPABILITY && side != getFacing()){
			return (LazyOptional<T>) magicOpt;
		}

		return super.getCapability(cap, side);
	}

	private class BeamHandler implements IBeamHandler{

		@Override
		public void setMagic(BeamUnit mag){
			if(!mag.isEmpty()){
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
		return time;
	}
}
