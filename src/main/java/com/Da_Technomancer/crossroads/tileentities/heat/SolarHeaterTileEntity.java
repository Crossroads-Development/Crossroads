package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class SolarHeaterTileEntity extends ModuleTE{

	@ObjectHolder("solar_heater")
	private static TileEntityType<SolarHeaterTileEntity> type = null;

	public static final double RATE = 5;
	public static final double CAP = 250;

	private boolean running = false;

	public SolarHeaterTileEntity(){
		super(type);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void tick(){
		super.tick();
		if(world.isRemote){
			return;
		}

		//Every 30 seconds, check if we still have sky view and cache the result
		if(world.getGameTime() % 600 == 0){
			running = world.canBlockSeeSky(pos);
		}

		//This machine can share heat with other Solar Heaters in the same line, but only other Solar Heaters. Otherwise, a heat cable is needed like normal
		TileEntity adjTE = world.getTileEntity(pos.offset(Direction.getFacingFromAxis(Direction.AxisDirection.NEGATIVE, world.getBlockState(pos).get(CRProperties.HORIZ_AXIS))));
		if(adjTE instanceof SolarHeaterTileEntity){
			SolarHeaterTileEntity otherTE = (SolarHeaterTileEntity) adjTE;
			temp += otherTE.temp;
			temp /= 2;
			otherTE.temp = temp;
			markDirty();
			otherTE.markDirty();
		}

		if(running && temp < CAP && world.isDaytime() && !world.isRaining()){
			temp = Math.min(CAP, temp + RATE);
			markDirty();
		}
	}

	@Override
	public void read(CompoundNBT nbt){
		super.read(nbt);
		running = nbt.getBoolean("running");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt){
		super.write(nbt);
		nbt.putBoolean("running", running);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing.getAxis() == world.getBlockState(pos).get(CRProperties.HORIZ_AXIS))){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(capability, facing);
	}
}
