package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;

public class ArcaneReflectorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	public int redstone = 0;
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		//TODO
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = null;
		return out;
	}
	
	@Override
	public void refresh(){
		//TODO
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		//TODO
		NBTTagCompound nbt = super.getUpdateTag();
		return nbt;
	}
	
	@Override
	public void receiveInt(String context, int message){
		//TODO
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		//TODO
		super.writeToNBT(nbt);
		nbt.setInteger("reds", redstone);
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		//TODO
		super.readFromNBT(nbt);
		redstone = nbt.getInteger("reds");
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != worldObj.getBlockState(pos).getValue(Properties.FACING)){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side != worldObj.getBlockState(pos).getValue(Properties.FACING)){
			return (T) magicHandler;
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			//TODO
		}
	}
} 
