package com.Da_Technomancer.crossroads.tileentities.rotary;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.CrossroadsConfig;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class StirlingEngineTileEntity extends ModuleTE{

	@Override
	protected boolean useRotary(){
		return true;
	}

	@Override
	protected boolean useHeat(){
		return false;//We intentionally do NOT use the ModuleTE heat template due to having two separate internal heat devices
	}

	@Override
	protected double getMoInertia(){
		return 200;
	}

	private double tempSide;
	private double tempBottom;
	private boolean init = false;

	@Override
	public void addInfo(ArrayList<String> chat, PlayerEntity player, @Nullable Direction side, BlockRayTraceResult hit){
		super.addInfo(chat, player, side, hitX, hitY, hitZ);

		chat.add("Side Temp: " + MiscUtil.betterRound(sideHeatHandler.getTemp(), 3) + "°C");
		chat.add("Bottom Temp: " + MiscUtil.betterRound(bottomHeatHandler.getTemp(), 3) + "°C");
		chat.add("Biome Temp: " + HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}
		init();

		int level = (int) ((tempSide - tempBottom) / 100D);
		tempSide -= 5D * level;
		tempBottom += 5D * level;

		if(axleHandler.axis != null && Math.signum(level) * motData[0] < ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.stirlingSpeedLimit).get()){
			motData[1] += ((ForgeConfigSpec.DoubleValue) CrossroadsConfig.stirlingMultiplier).get() * 5D * level * Math.abs(level);//5*stirlingMult*level^2 with sign of level
		}

		markDirty();
	}

	@Override
	public void readFromNBT(CompoundNBT nbt){
		super.readFromNBT(nbt);

		nbt.setBoolean("initHeat", init);
		nbt.setDouble("temp_side", tempSide);
		nbt.setDouble("temp_bottom", tempBottom);
	}

	@Override
	public CompoundNBT writeToNBT(CompoundNBT nbt){
		super.writeToNBT(nbt);

		init = nbt.getBoolean("initHeat");
		tempSide = nbt.getDouble("temp_side");
		tempBottom = nbt.getDouble("temp_bottom");

		return nbt;
	}

	private final SideHeatHandler sideHeatHandler = new SideHeatHandler();
	private final BottomHeatHandler bottomHeatHandler = new BottomHeatHandler();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing){
		if(capability == Capabilities.AXLE_CAPABILITY && (facing == null || facing == Direction.UP)){
			return (T) axleHandler;
		}
		if(capability == Capabilities.HEAT_CAPABILITY && facing != Direction.UP){
			return facing == Direction.DOWN ? (T) bottomHeatHandler : (T) sideHeatHandler;
		}

		return super.getCapability(capability, facing);
	}

	private void init(){
		if(!init){
			tempSide = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			tempBottom = tempSide;
			init = true;
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
			tempSide = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			tempSide += heat;
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
			tempBottom = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			tempBottom += heat;
		}
	}
}
