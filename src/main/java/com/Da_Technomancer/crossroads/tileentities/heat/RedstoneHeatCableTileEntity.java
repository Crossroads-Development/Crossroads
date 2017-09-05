package com.Da_Technomancer.crossroads.tileentities.heat;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.GoggleLenses;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.items.OmniMeter;
import com.Da_Technomancer.crossroads.items.Thermometer;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class RedstoneHeatCableTileEntity extends TileEntity implements ITickable, IInfoTE{

	private HeatInsulators insulator;

	private boolean init = false;
	// Temp as in temperature, not as in temporary
	private double temp = 0;

	public RedstoneHeatCableTileEntity(){
		this(HeatInsulators.WOOL);
	}

	public RedstoneHeatCableTileEntity(HeatInsulators insulator){
		super();
		this.insulator = insulator;
	}

	@Override
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, EnumFacing side){
		if(device instanceof OmniMeter || device == GoggleLenses.RUBY || device instanceof Thermometer){
			chat.add("Temp: " + heatHandler.getTemp() + "°C");
			if(!(device instanceof Thermometer)){
				chat.add("Biome Temp: " + EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos) + "°C");
			}
		}
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			if(insulator == HeatInsulators.ICE){
				temp = -10;
			}else{
				temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
			}
			init = true;
		}

		if(!world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return;
		}
		
		double prevTemp = temp;
		transHeat();
		runLoss(insulator.getRate());
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

	public void transHeat(){
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
	}
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		insulator = nbt.hasKey("insul") ? HeatInsulators.valueOf(nbt.getString("insul")) : null;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		nbt.setString("insul", insulator.name());
		return nbt;
	}

	public HeatInsulators getInsulator(){
		return insulator;
	}

	private void runLoss(double rate){
		double biomeTemp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
		temp += Math.min(rate, Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY){
			return world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL);
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private final HeatHandler heatHandler = new HeatHandler();
	private final RedstoneHandler redstoneHandler = new RedstoneHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return (T) heatHandler;
		}
		if(capability == Capabilities.ADVANCED_REDSTONE_HANDLER_CAPABILITY){
			return (T) redstoneHandler;
		}
		return super.getCapability(capability, facing);
	}

	private class RedstoneHandler implements IAdvancedRedstoneHandler{

		@Override
		public double getOutput(boolean read){
			if(!read || !world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL) || insulator == null){
				return 0;
			}
			double holder = (temp + 273) / (insulator.getLimit() + 273);
			holder *= 15D;

			return holder;
		}
	}

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				if(insulator == HeatInsulators.ICE){
					temp = -10;
				}else{
					temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
				}
				markDirty();
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
			if(temp != tempIn){
				temp = tempIn;
				markDirty();
			}
		}

		@Override
		public void addHeat(double heat){
			init();
			if(heat != 0){
				temp += heat;
				markDirty();
			}
		}
	}
}
