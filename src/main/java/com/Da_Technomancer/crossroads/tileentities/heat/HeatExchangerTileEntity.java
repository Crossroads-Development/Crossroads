package com.Da_Technomancer.crossroads.tileentities.heat;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.enums.HeatConductors;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;

import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class HeatExchangerTileEntity extends TileEntity implements ITickable{

	private double temp = 0;
	private boolean init = false;
	private int ticksExisted = 0;
	private boolean insul;

	public HeatExchangerTileEntity(){
		this(false);
	}

	public HeatExchangerTileEntity(boolean insul){
		this.insul = insul;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		++ticksExisted;

		if(!init){
			temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
			init = true;
		}

		if(ticksExisted % 10 == 0){
			transHeat(HeatConductors.COPPER.getRate());
			if(!insul){
				runLoss(.1D);
			}
			markDirty();
		}

		if(RecipeHolder.envirHeatSource.containsKey(worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock())){
			Triple<IBlockState, Double, Double> trip = RecipeHolder.envirHeatSource.get(worldObj.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock());
			if((trip.getMiddle() < 0 && trip.getRight() < temp) || (trip.getMiddle() >= 0 && trip.getRight() > temp)){
				worldObj.setBlockState(pos.offset(EnumFacing.DOWN), trip.getLeft() == null ? Blocks.AIR.getDefaultState() : trip.getLeft(), 3);
				handler.addHeat(trip.getMiddle());
			}
		}
	}

	public void transHeat(double rate){

		double reservePool = temp * rate;
		temp -= reservePool;
		int members = 1;

		for(EnumFacing side : EnumFacing.values()){
			if(side != EnumFacing.DOWN && worldObj.getTileEntity(pos.offset(side)) != null && worldObj.getTileEntity(pos.offset(side)).hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
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

	private void runLoss(double rate){
		if(rate == 0){
			return;
		}

		double newTemp = temp + (rate * (EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos())));
		newTemp /= (rate + 1);
		temp = newTemp;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		insul = nbt.getBoolean("insul");
		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("insul", insul);
		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.DOWN){
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	private IHeatHandler handler = new HeatHandler();

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing){
		if(capability == Capabilities.HEAT_HANDLER_CAPABILITY && facing != EnumFacing.DOWN){
			return (T) handler;
		}

		return super.getCapability(capability, facing);
	}

	private class HeatHandler implements IHeatHandler{
		private void init(){
			if(!init){
				temp = EnergyConverters.BIOME_TEMP_MULT * getWorld().getBiomeForCoordsBody(pos).getFloatTemperature(getPos());
				init = true;
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
			;
			temp = tempIn;
		}

		@Override
		public void addHeat(double heat){
			init();
			temp += heat;
		}
	}
}
