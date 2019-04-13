package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.IInfoTE;
import com.Da_Technomancer.crossroads.API.beams.BeamManager;
import com.Da_Technomancer.crossroads.API.beams.BeamUnit;
import com.Da_Technomancer.crossroads.API.packets.ModPackets;
import com.Da_Technomancer.crossroads.API.packets.SendIntToClient;
import com.Da_Technomancer.crossroads.API.technomancy.EntropySavedData;
import com.Da_Technomancer.crossroads.API.technomancy.FluxUtil;
import com.Da_Technomancer.crossroads.API.templates.BeamRenderTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;

public class BeaconHarnessTileEntity extends BeamRenderTE implements IInfoTE{

	public static final int FLUX = 4;
	public float angle = 0;//Used for rendering. Client side only

	private boolean running;
	private int cycles;

	@Override
	public void addInfo(ArrayList<String> chat, EntityPlayer player, @Nullable EnumFacing side, float hitX, float hitY, float hitZ){
		chat.add("Temporal Entropy: " + EntropySavedData.getEntropy(world) + "%");
	}

	@Override
	public void update(){
		super.update();

		if(world.isRemote || world.getTotalWorldTime() % BeamManager.BEAM_TIME != 0){
			return;
		}

		if(EntropySavedData.getSeverity(world).getRank() >= EntropySavedData.Severity.DESTRUCTIVE.getRank()){
			FluxUtil.overloadFlux(world, pos);
		}
	}

	private boolean invalid(Color col, boolean colorSafe, @Nullable BeamUnit last){
		if(!colorSafe){
			if(last == null || last.getVoid() != 0 || (col.getRed() != 0 && last.getEnergy() != 0) || (col.getGreen() != 0 && last.getPotential() != 0) || (col.getBlue() != 0 && last.getStability() != 0)){
				return true;
			}
		}

		return world.getBlockState(pos.offset(EnumFacing.DOWN, 2)).getBlock() != Blocks.BEACON || !world.isAirBlock(pos.offset(EnumFacing.DOWN, 1));
	}

	public void trigger(){
		if(!running && !invalid(null, true, null)){
			running = true;
			ModPackets.network.sendToAllAround(new SendIntToClient((byte) 0, BeamManager.toPacket(new BeamUnit(1, 1, 1, 0), 2), pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
		}
	}

	@Override
	public NBTTagCompound getUpdateTag(){
		NBTTagCompound nbt = super.getUpdateTag();
		nbt.setBoolean("run", running);
		return nbt;
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		super.writeToNBT(nbt);
		nbt.setBoolean("run", running);
		nbt.setInteger("cycle", cycles);
		return nbt;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		running = nbt.getBoolean("run");
		cycles = nbt.getInteger("cycle");
	}

	@Override
	protected void doEmit(BeamUnit toEmit){
		if(running){
			++cycles;
			cycles %= 120;
			Color col = Color.getHSBColor(((float) cycles) / 120F, 1, 1);
			if(invalid(col, cycles < 0 || cycles % 40 < 8, toEmit)){
				running = false;
				ModPackets.network.sendToAllAround(new SendIntToClient((byte) 0, 0, pos), new TargetPoint(world.provider.getDimension(), pos.getX(), pos.getY(), pos.getZ(), 512));
				cycles = -9;

				if(beamer[1].emit(null, world)){
					refreshBeam(1);
				}
				return;
			}
			if(cycles >= 0){
				BeamUnit out = new BeamUnit(col.getRed(), col.getGreen(), col.getBlue(), 0);
				out = out.mult(512D / ((double) out.getPower()), false);

				beamer[1].emit(out, world);
				refreshBeam(1);//Assume the beam changed as the color constantly cycles
				prevMag[1] = out;
				EntropySavedData.addEntropy(world, FLUX * BeamManager.BEAM_TIME);
				markDirty();
			}
		}
	}

	@Override
	protected boolean[] inputSides(){
		return new boolean[] {false, false, true, true, true, true};
	}

	@Override
	protected boolean[] outputSides(){
		return new boolean[] {false, true, false, false, false, false};
	}
}
