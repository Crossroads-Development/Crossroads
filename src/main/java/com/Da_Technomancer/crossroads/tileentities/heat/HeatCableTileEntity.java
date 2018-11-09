package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.ModConfig;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class HeatCableTileEntity extends ModuleTE{

	public HeatCableTileEntity(){
		this(HeatInsulators.WOOL);
	}

	public HeatCableTileEntity(HeatInsulators insulator){
		super();
		this.insulator = insulator;
	}

	protected HeatInsulators insulator;

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new CableHeatHandler();
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote){
			return;
		}

		double prevTemp = temp;

		//Heat transfer
		int members = 1;
		for(EnumFacing side : EnumFacing.values()){
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				IHeatHandler handler = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite());
				temp += handler.getTemp();
				handler.addHeat(-handler.getTemp());
				members++;
			}
		}

		temp /= members;

		for(EnumFacing side : EnumFacing.values()){
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite()).addHeat(temp);
			}
		}

		//Energy loss
		double biomeTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		temp += Math.min(insulator.getRate(), Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);

		if(temp != prevTemp){
			markDirty();
		}

		if(temp > insulator.getLimit()){
			if(ModConfig.heatEffects.getBoolean()){
				insulator.getEffect().doEffect(world, pos, 1);
			}else{
				world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		insulator = nbt.hasKey("insul") ? HeatInsulators.valueOf(nbt.getString("insul")) : HeatInsulators.WOOL;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setString("insul", insulator.name());
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY){
			return (T) heatHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class CableHeatHandler extends HeatHandler{

		@Override
		public void init(){
			if(!initHeat){
				if(insulator == HeatInsulators.ICE){
					temp = -10;
				}else{
					temp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
				}
				initHeat = true;
				markDirty();
			}
		}
	}
}
