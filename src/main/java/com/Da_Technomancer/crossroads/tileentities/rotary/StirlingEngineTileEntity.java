package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.crossroads.blocks.rotary.StirlingEngine;
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

import javax.annotation.Nullable;
import java.util.ArrayList;

@ObjectHolder(Crossroads.MODID)
public class StirlingEngineTileEntity extends ModuleTE{

	@ObjectHolder("stirling_engine")
	public static BlockEntityType<StirlingEngineTileEntity> TYPE = null;

	public static final double INERTIA = 200;
	public static final double HEAT_INTERVAL = 20;
	public static final double MAX_TEMPERATURE_DIFFERANCE = 2000;
	public static final double EFFICIENCY_MULTIPLIER = 0.8D;
	public static final double HEAT_VENTING_RATIO = 0.5D;

	private double tempSide;
	private double tempBottom;

	//For readout
	private double lastPower = 0;
	private double lastHeatIn = 0;
	private double lastHeatOut = 0;

	public StirlingEngineTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected boolean useHeat(){
		//We intentionally do NOT use the ModuleTE heat template due to having two separate internal heat devices
		//This method is overriden to return the default as a reminder of that fact
		return false;
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void addInfo(ArrayList<Component> chat, Player player, BlockHitResult hit){
		chat.add(new TranslatableComponent("tt.crossroads.stirling_engine.temp", CRConfig.formatVal(tempSide), CRConfig.formatVal(HeatUtil.toKelvin(tempSide)), CRConfig.formatVal(tempBottom), CRConfig.formatVal(HeatUtil.toKelvin(tempBottom))));
		chat.add(new TranslatableComponent("tt.crossroads.stirling_engine.status", CRConfig.formatVal(lastHeatIn), CRConfig.formatVal(lastHeatOut), CRConfig.formatVal(lastPower)));
		if(lastHeatIn > 0 && lastHeatOut > 0){
			chat.add(new TranslatableComponent("tt.crossroads.stirling_engine.efficiency", CRConfig.formatVal(lastPower / lastHeatIn), CRConfig.formatVal(lastPower / lastHeatOut)));
		}else{
			chat.add(new TranslatableComponent("tt.crossroads.stirling_engine.efficiency", CRConfig.formatVal(0), CRConfig.formatVal(0)));
		}
		super.addInfo(chat, player, hit);
	}

	private void updateWorldState(){
		//Updates the (purely for rending) blockstate in the world to match the gear speed
		BlockState worldState = getBlockState();
		final double slowMin = 0.5D;
		final double fastMin = 1.5D;
		double speedMagnitude = Math.abs(axleHandler.getSpeed());
		int target = 0;
		if(speedMagnitude > slowMin){
			if(speedMagnitude > fastMin){
				target = 2;
			}else{
				target = 1;
			}
			if(axleHandler.getSpeed() < 0){
				target = -target;
			}
		}
		target += 2;//Change from [-2, 2] to [0, 4]
		if(worldState.getBlock() instanceof StirlingEngine && worldState.getValue(CRProperties.RATE_SIGNED) != target){
			level.setBlock(worldPosition, worldState.setValue(CRProperties.RATE_SIGNED, target), 2);//Doesn't create block updates
		}
	}

	@Override
	public void serverTick(){
		super.serverTick();

		init();

		//Ok, to summarize what is going on here:
		//This is being modelled as a non-ideal thermodynamic heat engine
		//ΔT is being rounded DOWN to the next HEAT_INTERVAL
		//Let Efficiency η = EFFICIENCY_MULTIPLIER * ΔT / MAX_TEMPERATURE_DIFFERANCE, capped at EFFICIENCY_MULTIPLIER
		//And where not all ejected heat is coming out the cold side- some fraction HEAT_VENTING_RATIO of the heat that should be ejected is being lost (unaccounted for)
		//If this seems like an excessive amount of detail, know that I planned this in a spreadsheet to try to carefully control gameplay balance
		//You can be the judge of whether that worked
		lastHeatIn = (int) Math.abs((tempSide - tempBottom) / HEAT_INTERVAL);
		double deltaT = Math.min(MAX_TEMPERATURE_DIFFERANCE, lastHeatIn * HEAT_INTERVAL);
		boolean isSideHot = tempSide > tempBottom;
		double efficiency = EFFICIENCY_MULTIPLIER * deltaT / MAX_TEMPERATURE_DIFFERANCE;
		lastHeatOut = lastHeatIn * HEAT_VENTING_RATIO * (1 - efficiency);
		lastPower = CRConfig.stirlingConversion.get() * lastHeatIn * efficiency;

		if(lastHeatIn != 0){
			if(isSideHot){
				tempSide -= lastHeatIn;
				tempBottom += lastHeatOut;
			}else{
				tempSide += lastHeatOut;
				tempBottom -= lastHeatIn;
			}

			if(axleHandler.axis != null && (isSideHot ? axleHandler.getSpeed() : -axleHandler.getSpeed()) < CRConfig.stirlingSpeedLimit.get()){
				energy += (isSideHot ? 1D : -1D) * lastPower;
			}

			setChanged();
		}
		updateWorldState();
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);

		tempSide = nbt.getDouble("temp_side");
		tempBottom = nbt.getDouble("temp_bottom");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);

		nbt.putDouble("temp_side", tempSide);
		nbt.putDouble("temp_bottom", tempBottom);

	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		sideHeatOpt.invalidate();
		bottomHeatOpt.invalidate();
	}

	private final LazyOptional<IHeatHandler> sideHeatOpt = LazyOptional.of(SideHeatHandler::new);
	private final LazyOptional<IHeatHandler> bottomHeatOpt = LazyOptional.of(BottomHeatHandler::new);
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == Direction.UP)){
			return (LazyOptional<T>) axleOpt;
		}
		if(capability == Capabilities.HEAT_CAPABILITY && facing != Direction.UP){
			return facing == Direction.DOWN ? (LazyOptional<T>) bottomHeatOpt : (LazyOptional<T>) sideHeatOpt;
		}

		return super.getCapability(capability, facing);
	}

	private void init(){
		if(!initHeat){
			tempSide = HeatUtil.convertBiomeTemp(level, worldPosition);
			tempBottom = tempSide;
			initHeat = true;
		}
	}

	private class SideHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return tempSide;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			tempSide = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			setChanged();
		}

		@Override
		public void addHeat(double heat){
			init();
			tempSide = Math.max(HeatUtil.ABSOLUTE_ZERO, tempSide + heat);
			setChanged();
		}
	}

	private class BottomHeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			init();
			return tempBottom;
		}

		@Override
		public void setTemp(double tempIn){
			init();
			tempBottom = Math.max(HeatUtil.ABSOLUTE_ZERO, tempBottom);
			setChanged();
		}

		@Override
		public void addHeat(double heat){
			init();
			tempBottom = Math.max(HeatUtil.ABSOLUTE_ZERO, tempBottom + heat);
			setChanged();
		}
	}
}
