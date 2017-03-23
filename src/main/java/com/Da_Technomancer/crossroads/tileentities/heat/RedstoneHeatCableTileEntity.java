package com.Da_Technomancer.crossroads.tileentities.heat;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.ModConfig;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IAdvancedRedstoneHandler;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.enums.HeatInsulators;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class RedstoneHeatCableTileEntity extends TileEntity implements ITickable{

	private HeatConductors conductor;
	private HeatInsulators insulator;

	private boolean init = false;
	// Temp as in temperature, not as in temporary
	private double temp = 0;
	private int ticksExisted;

	public RedstoneHeatCableTileEntity(){
		this(HeatConductors.COPPER, HeatInsulators.WOOL);
	}

	public RedstoneHeatCableTileEntity(HeatConductors conductor, HeatInsulators insulator){
		super();
		this.conductor = conductor;
		this.insulator = insulator;
	}

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
	}
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		ticksExisted++;

		if(!init){
			if(insulator == HeatInsulators.ICE){
				temp = -10;
			}else{
				temp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(pos);
			}
			init = true;
		}

		if(ticksExisted % 10 == 0){
			transHeat(conductor.getRate());
			runLoss(insulator.getRate());
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

	public void transHeat(double rate){
		if(!world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL)){
			return;
		}
		double reservePool = temp * rate;
		temp -= reservePool;
		int members = 1;

		for(EnumFacing side : EnumFacing.values()){
			if(world.getTileEntity(pos.offset(side)) != null && world.getTileEntity(pos.offset(side)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				IHeatHandler handler = world.getTileEntity(pos.offset(side)).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite());
				reservePool += handler.getTemp() * rate;
				handler.addHeat(-(handler.getTemp() * rate));
				members++;
			}
		}

		reservePool /= members;

		for(EnumFacing side : EnumFacing.values()){
			if(world.getTileEntity(pos.offset(side)) != null && world.getTileEntity(pos.offset(side)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				world.getTileEntity(pos.offset(side)).getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite()).addHeat(reservePool);
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
		if(!world.isRemote){
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

		double newTemp = temp + (rate * (EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getFloatTemperature(getPos())));
		newTemp /= (rate + 1);
		temp = newTemp;
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
		public double getOutput(){
			if(!world.getBlockState(pos).getValue(Properties.REDSTONE_BOOL) || insulator == null){
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
				world.updateComparatorOutputLevel(pos, null);
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
			world.updateComparatorOutputLevel(pos, null);
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
			world.updateComparatorOutputLevel(pos, null);
		}

	}
}
