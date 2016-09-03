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

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ArcaneReflectorTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private int[] stored = new int[4];
	
	private Color col = null;
	private int reach = 0;
	private int size = 0;
	public int redstone = 0;
	
	@Override
	public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newState){
		return (oldState.getBlock() != newState.getBlock());
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
		
		if(worldObj.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0){
			if(stored[0] != 0 || stored[1] != 0 || stored[2] != 0 || stored[3] != 0){
				MagicUnit mag = new MagicUnit(stored[0], stored[1], stored[2], stored[3]);
				stored[0] = 0;
				stored[1] = 0;
				stored[2] = 0;
				stored[3] = 0;

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
				worldObj.getTileEntity(pos.offset(dir, i)).getCapability(Capabilities.MAGIC_HANDLER_CAPABILITY, dir.getOpposite()).setMagic(mag);
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
		nbt.setInteger("ener", stored[0]);
		nbt.setInteger("pot", stored[1]);
		nbt.setInteger("stab", stored[2]);
		nbt.setInteger("void", stored[3]);
		nbt.setInteger("reds", redstone);
		
		if(col != null){
			nbt.setInteger("col", col.getRGB() & 16777215);
		}
		nbt.setInteger("reach", reach);
		nbt.setInteger("size", size);
		
		return nbt;
	}
	
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		stored[0] = nbt.getInteger("ener");
		stored[1] = nbt.getInteger("pot");
		stored[2] = nbt.getInteger("stab");
		stored[3] = nbt.getInteger("void");
		redstone = nbt.getInteger("reds");
		
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
		public void setMagic(MagicUnit mag){
			stored[0] += mag.getEnergy();
			stored[1] += mag.getPotential();
			stored[2] += mag.getStability();
			stored[3] += mag.getVoid();
		}

		/*TODO
		@Override
		public MagicUnit canPass(MagicUnit mag){
			double i = ((double) (15 - redstone)) / 15D;
			MagicUnit magA = new MagicUnit((int) Math.round(i * mag.getEnergy()), (int) Math.round(i * mag.getPotential()), (int) Math.round(i * mag.getStability()), (int) Math.round(i * mag.getVoid()));
			if(magA.getPower() <= 0){
				return null;
			}
			recieveMagic(new MagicUnit(mag.getEnergy() - magA.getEnergy(), mag.getPotential() - magA.getPotential(), mag.getStability() - magA.getStability(), mag.getVoid() - magA.getVoid()));
			return magA;
		}
		*/
	}
} 
