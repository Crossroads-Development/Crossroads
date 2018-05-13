package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.MiscOp;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.items.crafting.RecipeHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;

public class HeatExchangerTileEntity extends TileEntity implements ITickable, IInfoTE{

	private double temp = 0;
	private boolean init = false;
	private boolean insul;
	private double bufferTemp = 0;

	public HeatExchangerTileEntity(){
		this(false);
	}

	public HeatExchangerTileEntity(boolean insul){
		super();
		this.insul = insul;
	}

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, EnumFacing side){
		chat.add("Temp: " + MiscOp.betterRound(handler.getTemp(), 3) + "°C");
		chat.add("Buffered heat: " + MiscOp.betterRound(bufferTemp, 3) + "°C");
		chat.add("Biome Temp: " + EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos)) + "°C");
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(!init){
			temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		if(RecipeHolder.envirHeatSource.containsKey(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock())){
			Triple<IBlockState, Double, Double> trip = RecipeHolder.envirHeatSource.get(world.getBlockState(pos.offset(EnumFacing.DOWN)).getBlock()).getRight();
			if((trip.getMiddle() < 0 && trip.getRight() < temp + bufferTemp) || (trip.getMiddle() >= 0 && trip.getRight() > temp + bufferTemp)){
				world.setBlockState(pos.offset(EnumFacing.DOWN), trip.getLeft(), 3);
				bufferTemp += trip.getMiddle();
				markDirty();
			}
		}

		if(bufferTemp != 0){
			double internalTransfer = Math.min(25D, Math.abs(bufferTemp)) * Math.signum(bufferTemp);
			temp += internalTransfer;
			bufferTemp -= internalTransfer;
			markDirty();
		}


		double prevTemp = temp;
		transHeat();
		if(!insul){
			runLoss(10D);
		}
		if(temp != prevTemp){
			markDirty();
		}
	}

	private void transHeat(){

		double reservePool = temp;
		temp -= reservePool;
		int members = 1;

		for(EnumFacing side : EnumFacing.values()){
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(side != EnumFacing.DOWN && te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				IHeatHandler handler = te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite());
				reservePool += handler.getTemp();
				handler.addHeat(-handler.getTemp());
				members++;
			}
		}

		reservePool /= members;

		for(EnumFacing side : EnumFacing.values()){
			TileEntity te = world.getTileEntity(pos.offset(side));
			if(te != null && te.hasCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite())){
				te.getCapability(Capabilities.HEAT_HANDLER_CAPABILITY, side.getOpposite()).addHeat(reservePool);
			}
		}
		temp += reservePool;
	}

	private void runLoss(double rate){
		double biomeTemp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
		temp += Math.min(rate, Math.abs(temp - biomeTemp)) * Math.signum(biomeTemp - temp);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);

		insul = nbt.getBoolean("insul");
		init = nbt.getBoolean("init");
		temp = nbt.getDouble("temp");
		bufferTemp = nbt.getDouble("buffer");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);

		nbt.setBoolean("insul", insul);
		nbt.setBoolean("init", this.init);
		nbt.setDouble("temp", this.temp);
		nbt.setDouble("buffer", bufferTemp);
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
				temp = EnergyConverters.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
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
