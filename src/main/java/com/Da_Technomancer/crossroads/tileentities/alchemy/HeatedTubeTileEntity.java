package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.MiscUtil;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class HeatedTubeTileEntity extends AlchemyCarrierTE{

	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return oldState.getBlock() != newState.getBlock();
	}

	private double cableTemp = 0;
	private boolean init = false;

	/**
	 * @param chat Add info to this list, 1 line per entry. 
	 * @param device The device type calling this method. 
	 * @param player The player using the info device.
	 * @param side The viewed EnumFacing (only used by goggles).
	 */
	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side){
		chat.add("Temp: " + MiscUtil.betterRound(cableTemp, 3) + "°C");
		if(amount == 0){
			chat.add("No reagents");
		}
		for(ReagentStack reag : contents){
			if(reag != null){
				chat.add(reag.toString());
			}
		}
	}

	public HeatedTubeTileEntity(){
		super();
	}

	public HeatedTubeTileEntity(boolean glass){
		super(glass);
	}

	@Override
	protected double correctTemp(){
		//Shares heat between internal cable & contents
		cableTemp = amount <= 0 ? cableTemp : (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
		heat = (cableTemp + 273D) * amount;
		return cableTemp;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			init = true;
		}

		super.update();
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		EnumFacing outSide = world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		cableTemp = nbt.getDouble("temp");
		init = nbt.getBoolean("init");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setDouble("temp", cableTemp);
		nbt.setBoolean("init", init);
		return nbt;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING).getAxis())){
			return true;
		}
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == EnumFacing.Axis.Y)){
			return true;
		}
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING).getAxis())){
			return (T) handler;
		}
		if(cap == Capabilities.HEAT_HANDLER_CAPABILITY && (side == null || side.getAxis() == EnumFacing.Axis.Y)){
			return (T) heatHandler;
		}
		return super.getCapability(cap, side);
	}

	private final HeatHandler heatHandler = new HeatHandler();

	private class HeatHandler implements IHeatHandler{

		private void init(){
			if(!init){
				init = true;
				cableTemp = HeatUtil.convertBiomeTemp(world.getBiomeForCoordsBody(pos).getTemperature(pos));
			}
		}

		@Override
		public double getTemp(){
			init();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			init();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			if(amount != 0){
				cableTemp = (cableTemp + EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount * ((heat / amount) - 273D)) / (EnergyConverters.ALCHEMY_TEMP_CONVERSION * amount + 1D);
				heat = (cableTemp + 273D) * amount;
				dirtyReag = true;
			}
			markDirty();
		}
	}
}
