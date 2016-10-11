package com.Da_Technomancer.crossroads.tileentities.heat;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class HeatCableTileEntity extends TileEntity implements ITickable{

	private HeatConductors conductor;
	private HeatInsulators insulator;

	private boolean init = false;
	// Temp as in temperature, not as in temporary
	private double temp = 0;
	private int ticksExisted;

	public HeatCableTileEntity(){
		this(HeatConductors.COPPER, HeatInsulators.WOOL);
	}

	public HeatCableTileEntity(HeatConductors conductor, HeatInsulators insulator){
		this.conductor = conductor;
		this.insulator = insulator;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}

		ticksExisted++;

		if(!init){
			if(insulator == HeatInsulators.ICE){
				temp = -10;
			}else{
				temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
			}
			init = true;
		}

		if(ticksExisted % 10 == 0){
			transHeat(conductor.getRate());
			runLoss(insulator.getRate());
			markDirty();
		}

		if(temp > insulator.getLimit()){
			if(ModConfig.overheatEffects.getBoolean()){
				insulator.getEffect().onOverheat(worldObj, pos);
			}else{
				worldObj.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
			}
		}
	}

	public void transHeat(double rate){

		double reservePool = temp * rate;
		temp -= reservePool;
		int members = 1;

		for(EnumFacing side : EnumFacing.values()){
			if(worldObj.getTileEntity(pos.offset(side)) != null && worldObj.getTileEntity(pos.offset(side)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				IHeatHandler handler = worldObj.getTileEntity(pos.offset(side)).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite());
				reservePool += handler.getTemp() * rate;
				handler.addHeat(-(handler.getTemp() * rate));
				members++;
			}
		}

		reservePool /= members;

		for(EnumFacing side : EnumFacing.values()){
			if(worldObj.getTileEntity(pos.offset(side)) != null && worldObj.getTileEntity(pos.offset(side)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				worldObj.getTileEntity(pos.offset(side)).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite()).addHeat(reservePool);
			}
		}
		temp += reservePool;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		conductor = nbt.hasKey("cond") ? HeatConductors.valueOf(nbt.getString("cond")) : null;
		insulator = nbt.hasKey("insul") ? HeatInsulators.valueOf(nbt.getString("insul")) : null;

	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		if(!worldObj.isRemote){
			nbt.setString("cond", conductor.name());
			nbt.setString("insul", insulator.name());
		}
		return nbt;
	}

	public HeatConductors getConductor(){
		return conductor;
	}

	public HeatInsulators getInsulator(){
		return insulator;
	}

	private void runLoss(double rate){
		if(rate == 0){
			return;
		}

		double newTemp = temp + (rate * (EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos())));
		newTemp /= (rate + 1);
		temp = newTemp;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private final IHeatHandler heatHandler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				if(insulator == HeatInsulators.ICE){
					temp = -10;
				}else{
					temp = EnergyConverters.BIOME_TEMP_MULT * worldObj.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
				}
			}
		}

		@Override
		public double getTemp(){
			init();
			return temp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}

	}
}
