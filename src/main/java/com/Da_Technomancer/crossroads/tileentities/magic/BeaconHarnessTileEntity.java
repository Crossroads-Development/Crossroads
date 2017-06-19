package com.Da_Technomancer.crossroads.tileentities.magic;

import java.awt.Color;

import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.Triple;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.magic.BeamManager;
import com.Da_Technomancer.crossroads.API.magic.BeamRenderTE;
import com.Da_Technomancer.crossroads.API.magic.IMagicHandler;
import com.Da_Technomancer.crossroads.API.magic.MagicUnit;
import com.Da_Technomancer.crossroads.API.magic.MagicUnitStorage;
import com.Da_Technomancer.crossroads.API.packets.IIntReceiver;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class BeaconHarnessTileEntity extends BeamRenderTE implements ITickable, IIntReceiver{

	private Triple<Color, Integer, Integer> outBeam;
	private Triple<Color, Integer, Integer> inBeam;
	public float[] renderOld = new float[8];
	public float[] renderNew = new float[8];
	public boolean renderSet = false;
	
	@Override
	public void refresh(){
		if(beamer != null){
			beamer.emit(null);
		}
	}
	
	@Override
	@Nullable
	public MagicUnit[] getLastFullSent(){
		return beamer == null || beamer.getLastFullSent() == null ? null : new MagicUnit[] {beamer.getLastFullSent()};
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Triple<Color, Integer, Integer>[] getBeam(){
		Triple<Color, Integer, Integer>[] out = new Triple[2];
		out[0] = inBeam;
		out[1] = outBeam;
		return out;
	}

	private boolean running;
	private int cycles;
	
	private boolean invalid(Color col, boolean colorSafe){
		if(!colorSafe){
			MagicUnit last = magStor.getOutput();
			if(last == null || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
				return true;
			}
		}
		
		if(world.getBlockState(pos.offset(EnumFacing.DOWN, 2)).getBlock() == Blocks.BEACON && world.isAirBlock(pos.offset(EnumFacing.DOWN, 1))){
			return false;
		}
		
		return true;
	}
	
	public void trigger(){
		if(!running && !invalid(null, true)){
			running = true;
			ModPackets.network.sendToAllAround(new SendIntToClient(1, 1, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}
	
	private boolean primed;
	
	@Override
	public void update(){
		if(world.isRemote){
			return;
		}

		if(beamer == null){
			beamer = new BeamManager(EnumFacing.UP, pos, world);
		}

		if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 0 && primed){
			primed = false;
			markDirty();
			if(running){
				++cycles;
				cycles %= 120;
				Color col = Color.getHSBColor(((float) cycles) / 120F, 1, 1);
				if(invalid(col, cycles < 0 || cycles % 40 < 8)){
					running = false;
					ModPackets.network.sendToAllAround(new SendIntToClient(1, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					cycles = -9;
					
					if(beamer.emit(null)){
						ModPackets.network.sendToAllAround(new SendIntToClient(0, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
					
					magStor.clear();
					return;
				}
				magStor.clear();
				if(cycles >= 0){
					MagicUnit out = new MagicUnit(col.getRed(), col.getGreen(), col.getBlue(), 0);
					out = out.mult(512D / ((double) out.getPower()), false);
					
					if(beamer.emit(out)){
						ModPackets.network.sendToAllAround(new SendIntToClient(0, ((beamer.getDist() - 1) << 24) + (beamer.getLastSent().getRGB().getRGB() & 16777215), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
					}
				}
			}
		}else if(world.getTotalWorldTime() % IMagicHandler.BEAM_TIME == 1){
			magStor.addMagic(magIn.getOutput());
			magIn.clear();
			primed = true;
			markDirty();
		}
	}

	private BeamManager beamer;

	@Override
	public void receiveInt(int identifier, int message, @Nullable EntityPlayerMP player){
		if(identifier == 0){
			 outBeam = BeamManager.getTriple(message);
		}else if(identifier == 1){
			inBeam = message == 1 ? Triple.of(Color.WHITE, 2, 1) : null;
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("runC", running);
		return nbt;
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("run", running);
		nbt.setInteger("cycle", cycles);
		if(magStor != null){
			magStor.writeToNBT("stor", nbt);
		}
		if(magIn != null){
			magIn.writeToNBT("in", nbt);
		}
		nbt.setBoolean("primed", primed);
		return nbt;
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		running = nbt.getBoolean("run");
		cycles = nbt.getInteger("cycle");
		magStor = MagicUnitStorage.readFromNBT("stor", nbt);
		magIn = MagicUnitStorage.readFromNBT("in", nbt);
		inBeam = nbt.getBoolean("runC") ? Triple.of(Color.WHITE, 2, 1) : null;
		primed = nbt.getBoolean("primed");
	}
	
	private final IMagicHandler magicHandler = new MagicHandler();
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.MAGIC_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return (T) magicHandler;
		}

		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing){
		if(capability == Capabilities.MAGIC_HANDLER_CAPABILITY && (facing == null || facing.getAxis() != EnumFacing.Axis.Y)){
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	private MagicUnitStorage magStor = new MagicUnitStorage();
	private MagicUnitStorage magIn = new MagicUnitStorage();
	
	private class MagicHandler implements IMagicHandler{

		@Override
		public void setMagic(MagicUnit mag){
			magIn.addMagic(mag);
		}
	}
}
