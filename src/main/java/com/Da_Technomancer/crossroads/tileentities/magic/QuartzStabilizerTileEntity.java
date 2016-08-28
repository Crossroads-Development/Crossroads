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
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class QuartzStabilizerTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private boolean large;
	private int[] stored = new int[4];
	private final int[] LIMIT = new int[] {20, 60};
	private final int[] RATE = new int[] {3, 9};
	private int ticksExisted;
	
	private Color col = null;
	private int reach = 0;
	private int size = 0;
	
	public QuartzStabilizerTileEntity(){
		
	}	
	public QuartzStabilizerTileEntity(boolean large){
		this.large = large;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[6];
		out[worldObj.getBlockState(pos).getValue(Properties.FACING).getIndex()] = col == null ? null : Triple.of(col, reach, size);
		return out;
	}

	@Override
	public void update(){
		if(worldObj.isRemote){
			return;
		}
		++ticksExisted;
		
		if(ticksExisted % IMagicHandler.BEAM_TIME == 0){
			if(stored[0] != 0 || stored[1] != 0 || stored[2] != 0 || stored[3] != 0){
				MagicUnit mag = MagicUnit.getClosestMatch(new MagicUnit(stored[0], stored[1], stored[2], stored[3]).getRGB(), RATE[large ? 1 : 0]);
				double mult = ((double) RATE[large ? 1 : 0]) / ((double) mag.getPower());
				mag = new MagicUnit(Math.min((int) Math.round(((double) mag.getEnergy()) * mult), stored[0]), Math.min((int) Math.round(((double) mag.getPotential()) * mult), stored[1]), Math.min((int) Math.round(((double) mag.getStability()) * mult), stored[2]), Math.min((int) Math.round(((double) mag.getVoid()) * mult), stored[3]));
				stored[0] -= mag.getEnergy();
				stored[1] -= mag.getPotential();
				stored[2] -= mag.getStability();
				stored[3] -= mag.getVoid();

				emit(mag, worldObj.getBlockState(pos).getValue(Properties.FACING));
			}else{
				wipeBeam();
			}
		}
	}
	
	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size + 1);
		return nbt;
	}
	
	private void wipeBeam(){
		if(col != null || reach != 0 || size != 0){
			col = null;
			reach = 0;
			size = 0;
			ModPackets.network.sendToAllAround(new SendIntToClient("beam", 0, pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
	
	private void emit(MagicUnit mag, EnumFacing dir){
		if(mag == null || mag.getRGB() == null){
			return;
		}
		for(int i = 1; i <= IMagicHandler.MAX_DISTANCE; i++){
			if(worldObj.getTileEntity(pos.offset(dir, i)) != null && worldObj.getTileEntity(pos.offset(dir, i)).hasCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite())){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
				}
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).recieveMagic(mag);
				return;
			}
			
			if(i == IMagicHandler.MAX_DISTANCE || (worldObj.getBlockState(pos.offset(dir, i)) != null && !worldObj.getBlockState(pos.offset(dir, i)).getBlock().isAir(worldObj.getBlockState(pos.offset(dir, i)), worldObj, pos.offset(dir, i)))){
				int siz = Math.min((int) Math.sqrt(mag.getPower()) - 1, 7);
				if(col == null || mag.getRGB().getRGB() != col.getRGB() || siz != size || i != reach){
					ModPackets.network.sendToAllAround(new SendIntToClient("beam", ((i - 1) << 24) + (mag.getRGB().getRGB() & 16777215) + (siz << 28), pos), new TargetPoint(worldObj.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					size = siz;
					col = mag.getRGB();
					reach = i;
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
			if(message == 0){
				reach = 0;
				size = 0;
				col = null;
			}else{
				int i = message & 16777215;
				reach = ((message >> 24) & 15) + 1;
				size = (message >> 28) + 1;
				col = Color.decode(Integer.toString(i));
			}
		}
	}

	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("large", large);
		nbt.setInteger("ener", stored[0]);
		nbt.setInteger("pot", stored[1]);
		nbt.setInteger("stab", stored[2]);
		nbt.setInteger("void", stored[3]);
		
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size);
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		large = nbt.getBoolean("large");
		stored[0] = nbt.getInteger("ener");
		stored[1] = nbt.getInteger("pot");
		stored[2] = nbt.getInteger("stab");
		stored[3] = nbt.getInteger("void");
		
		col = nbt.hasKey("col") ? Color.decode(Integer.toString(nbt.getInteger("col"))) : null;
		reach = nbt.getInteger("reach");
		size = nbt.getInteger("size");
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
		public void recieveMagic(MagicUnit mag){
			double mult = Math.min(1, ((double) (LIMIT[large ? 1 : 0] - (stored[0] + stored[1] + stored[2] + stored[3]))) / (double) mag.getPower());
			stored[0] += Math.round(mult * mag.getEnergy());
			stored[1] += Math.round(mult * mag.getPotential());
			stored[2] += Math.round(mult * mag.getStability());
			stored[3] += Math.round(mult * mag.getVoid());
		}
	}
} 
