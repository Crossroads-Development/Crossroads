package com.Da_Technomancer.crossroads.tileentities.alchemy;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.EnergyConverters;
import com.Da_Technomancer.crossroads.API.IInfoDevice;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.alchemy.AbstractAlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.ReagentStack;
import com.Da_Technomancer.crossroads.API.technomancy.EnumGoggleLenses;
import com.Da_Technomancer.crossroads.items.ModItems;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class CoolingCoilTileEntity extends AbstractAlchemyCarrierTE{

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
	public void addInfo(ArrayList<String> chat, IInfoDevice device, EntityPlayer player, @Nullable EnumFacing side){
		if(device == ModItems.omnimeter || device == EnumGoggleLenses.DIAMOND){
			chat.add("Temp: " + cableTemp + "°C");
			if(amount == 0){
				chat.add("No reagents");
			}
			for(ReagentStack reag : contents){
				if(reag != null){
					chat.add(reag.toString());
				}
			}
		}
	}

	public CoolingCoilTileEntity(){
		super();
	}

	public CoolingCoilTileEntity(boolean glass){
		super(glass);
	}

	@Override
	public double correctTemp(){
		//Shares heat between internal cable & contents
		heat = (cableTemp + 273D) * amount;
		return cableTemp;
	}

	@Override
	public void update(){
		if(world.isRemote){
			return;
		}
		if(!init){
			cableTemp = EnergyConverters.BIOME_TEMP_MULT * world.getBiomeForCoordsBody(pos).getTemperature(pos);
			init = true;
		}
		super.update();
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
		return super.hasCapability(cap, side);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.CHEMICAL_HANDLER_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING).getAxis())){
			return (T) handler;
		}
		return super.getCapability(cap, side);
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		EnumFacing outSide = world.getBlockState(pos).getValue(Properties.HORIZONTAL_FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}
}
