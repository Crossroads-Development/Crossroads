package com.Da_Technomancer.crossroads.tileentities.beams;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.beams.BeamUtil;
import com.Da_Technomancer.crossroads.API.beams.EnumBeamAlignments;
import com.Da_Technomancer.crossroads.API.beams.IBeamHandler;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.tileentities.rotary.MasterAxisTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class CrystalMasterAxisTileEntity extends MasterAxisTileEntity implements IInfoTE{

	@ObjectHolder("crystal_master_axis")
	public static BlockEntityType<CrystalMasterAxisTileEntity> TYPE = null;

	private EnumBeamAlignments currentElement;
	private int time;

	public CrystalMasterAxisTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		//Known issue: the localization for alignment name happens on the server side
		chat.add(new TranslatableComponent("tt.crossroads.crystal_master_axis.info", currentElement == null ? MiscUtil.localize("alignment.none") : currentElement.getLocalName(time < 0), Math.abs(time)));
	}

	public int getTime(){
		return time;
	}

	@Override
	protected void runCalc(){
		double prevSumEnergy = sumEnergy;

		double[] systemEnergyResult = RotaryUtil.getTotalEnergy(rotaryMembers, currentElement != EnumBeamAlignments.STABILITY);
		sumEnergy = systemEnergyResult[0];
		energyLossChange = systemEnergyResult[1];
		baseSpeed = systemEnergyResult[2];
		double sumIRot = systemEnergyResult[3];

		if(currentElement == EnumBeamAlignments.ENERGY){
			if(sumIRot > 0){
				sumEnergy += CRConfig.crystalAxisMult.get() * (Math.signum(sumEnergy) == 0 ? 1 : Math.signum(sumEnergy));
				baseSpeed = Math.signum(sumEnergy) * Math.sqrt(Math.abs(sumEnergy) * 2D / sumIRot);
			}
		}else if(currentElement == EnumBeamAlignments.CHARGE){
			if(sumIRot > 0){
				sumEnergy += CRConfig.crystalAxisMult.get();
				baseSpeed = Math.signum(sumEnergy) * Math.sqrt(Math.abs(sumEnergy) * 2D / sumIRot);
			}
		}else if(currentElement == EnumBeamAlignments.EQUILIBRIUM){
			if(sumIRot > 0){
				sumEnergy = (sumEnergy + 9D * prevSumEnergy) / 10D;
				baseSpeed = Math.signum(sumEnergy) * Math.sqrt(Math.abs(sumEnergy) * 2D / sumIRot);
			}
		}

		//For very low total system energy, we drain the remainder of the energy as 'loss'
		if(sumEnergy < 1 && sumEnergy > -1 || Double.isNaN(sumEnergy)){
			energyLossChange += sumEnergy;
			sumEnergy = 0;
			baseSpeed = 0;
		}
		energyChange = sumEnergy - prevSumEnergy;

		for(IAxleHandler gear : rotaryMembers){
			// set energy
			double gearSpeed = baseSpeed * gear.getRotationRatio();
			gear.setEnergy(Math.signum(gearSpeed) * Math.pow(gearSpeed, 2) * gear.getMoInertia() / 2D);
		}
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putInt("time", time);
		if(currentElement != null){
			nbt.putString("elem", currentElement.name());
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		time = nbt.getInt("time");
		currentElement = nbt.contains("elem") ? EnumBeamAlignments.valueOf(nbt.getString("elem")) : null;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		if(currentElement != null && time-- <= 0){
			currentElement = null;
			time = 0;
		}
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
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
		public void setBeam(BeamUnit mag){
			if(!mag.isEmpty()){
				EnumBeamAlignments newElem = EnumBeamAlignments.getAlignment(mag);
				if(newElem != currentElement){
					currentElement = newElem;
					if(mag.getVoid() == 0){
						time = mag.getPower() * BeamUtil.BEAM_TIME;
					}
				}else{
					time = Math.max(mag.getVoid() == 0 ? time + mag.getPower() * BeamUtil.BEAM_TIME : time - mag.getPower() * BeamUtil.BEAM_TIME, 0);
				}
			}
		}
	}

	public int getRedstone(){
		return time;
	}
}
