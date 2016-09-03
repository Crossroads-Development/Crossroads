package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.Properties;
import com.Da_Technomancer.crossroads.API.effects.IEffect;
import com.Da_Technomancer.crossroads.API.enums.MagicElements;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class CrystallinePrismTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private int[] reach = new int[3];
	private int[] size = new int[3];
	private int[] stored = new int[3];
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite().getIndex()] = reach[0] == 0 ? null : Triple.of(Color.RED, reach[0], size[0]);
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = reach[1] == 0 ? null : Triple.of(Color.GREEN, reach[1], size[1]);
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getIndex()] = reach[2] == 0 ? null : Triple.of(Color.BLUE, reach[2], size[2]);
		return out;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		
		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(stored[0] != 0 || stored[1] != 0 || stored[2] != 0){
				emit(new MagicUnit(stored[0], 0, 0, 0), worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y).getOpposite(), 0);
				emit(new MagicUnit(0, stored[1], 0, 0), worldObj.getBlockState(pos).getValue(Properties.FACING), 1);
				emit(new MagicUnit(0, 0, stored[2], 0), worldObj.getBlockState(pos).getValue(Properties.FACING).rotateAround(Axis.Y), 2);
				stored[0] = 0;
				stored[1] = 0;
				stored[2] = 0;
			}else{
				wipeBeam();
			}
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setIntArray("reach", reach);
		nbt.setIntArray("size", new int[] {size[0] + 1, size[1] + 1, size[2] + 1});
		return nbt;
	}

	private void wipeBeam(){
		for(int i = 0; i < 3; i++){
			reach[i] = 0;
			size[i] = 0;
			ModPackets.network.sendToAllAround(new SendIntToClient("beam", i, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	private void emit(MagicUnit mag, EnumFacing dir, int index){
		if(mag == null || mag.getRGB() == null){
			return;
		}
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(worldObj.getTileEntity(pos.offset(dir, i)) != null && worldObj.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(siz != size[index] || i != reach[index]){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", index + ((i - 1) << 2) + (siz << 6), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size[index] = siz;
					reach[index] = i;
				}
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
				return;
			}

			if(i == IMagicHandler.MAX_DISTANCE || (worldObj.getBlockState(pos.offset(dir, i)) != null && !worldObj.getBlockState(pos.offset(dir, i)).getBlock().isAir(worldObj.getBlockState(pos.offset(dir, i)), worldObj, pos.offset(dir, i)))){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(siz != size[index] || i != reach[index]){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", index + ((i - 1) << 2) + (siz << 6), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size[index] = siz;
					reach[index] = i;
				}
				IEffect e = MagicElements.getElement(mag).getMixEffect(mag.getRGB());
				if(e != null){
					e.doEffect(worldObj, pos.offset(dir, i));
				}
				return;
			}
		}
	}

	@Override
	public void receiveInt(String context, int message){
		if(context.equals("beam")){
			if(message - (message & 3) == 0){
				reach[message] = 0;
				size[message] = 0;
			}else{
				int index = message & 3;
				reach[index] = ((message >> 2) & 15) + 1;
				size[index] = (message >> 6) + 1;
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setInteger("ener", stored[0]);
		nbt.setInteger("pot", stored[1]);
		nbt.setInteger("stab", stored[2]);
		
		nbt.setIntArray("reach", reach);
		nbt.setIntArray("size", size);
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stored[0] = nbt.getInteger("ener");
		stored[1] = nbt.getInteger("pot");
		stored[2] = nbt.getInteger("stab");
		
		reach = nbt.getIntArray("reach");
		size = nbt.getIntArray("size");
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == worldObj.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
			return true;
		}
		
		return super.hasCapability(cap, side);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing side){
		if(cap == Capabilities.MAGIC_HANDLER_CAPABILITY && side == worldObj.getBlockState(pos).getValue(Properties.FACING).getOpposite()){
			return (T) magicHandler;
		}
		
		return super.getCapability(cap, side);
	}
	
	private class MagicHandler implements IMagicHandler{
		
		@Override
		public void setMagic(MagicUnit mag){
			stored[0] += mag.getEnergy();
			stored[1] += mag.getPotential();
			stored[2] += mag.getStability();
		}
	}
} 
