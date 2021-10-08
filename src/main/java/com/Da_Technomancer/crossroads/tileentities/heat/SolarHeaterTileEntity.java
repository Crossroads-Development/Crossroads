package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class SolarHeaterTileEntity extends ModuleTE{

	@ObjectHolder("solar_heater")
	private static BlockEntityType<SolarHeaterTileEntity> type = null;

	public static final double RATE = 5;
	public static final double CAP = 325;

	private boolean newlyPlaced = true;//Used to immediately generate the cache to reduce the latency for the player with a new heater
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
		if(level.isClientSide){
			return;
		}

		//Every 30 seconds, check if we still have sky view and cache the result
		if(newlyPlaced || level.getGameTime() % 600 == 0){
			running = level.canSeeSkyFromBelowWater(worldPosition);
			newlyPlaced = false;
		}

		//This machine can share heat with other Solar Heaters in the same line, but only other Solar Heaters. Otherwise, a heat cable is needed like normal
		BlockEntity adjTE = level.getBlockEntity(worldPosition.relative(Direction.get(Direction.AxisDirection.NEGATIVE, level.getBlockState(worldPosition).getValue(CRProperties.HORIZ_AXIS))));
		if(adjTE instanceof SolarHeaterTileEntity){
			SolarHeaterTileEntity otherTE = (SolarHeaterTileEntity) adjTE;
			temp += otherTE.temp;
			temp /= 2;
			otherTE.temp = temp;
			setChanged();
			otherTE.setChanged();
		}

		if(running && temp < CAP && level.isDay() && !level.isRaining()){
			temp = Math.min(CAP, temp + RATE);
			setChanged();
		}
	}

	@Override
	public void load(BlockState state, CompoundTag nbt){
		super.load(state, nbt);
		running = nbt.getBoolean("running");
	}

	@Override
	public CompoundTag save(CompoundTag nbt){
		super.save(nbt);
		nbt.putBoolean("running", running);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY && (facing == null || facing.getAxis() == level.getBlockState(worldPosition).getValue(CRProperties.HORIZ_AXIS))){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(capability, facing);
	}
}
