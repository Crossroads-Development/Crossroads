package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

public class SolarHeaterTileEntity extends ModuleTE{

	public static final BlockEntityType<SolarHeaterTileEntity> TYPE = CRTileEntity.createType(SolarHeaterTileEntity::new, CRBlocks.solarHeater);

	public static final double CAP = 325;

	private boolean newlyPlaced = true;//Used to immediately generate the cache to reduce the latency for the player with a new heater
	private boolean running = false;

	public SolarHeaterTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	public void serverTick(){
		super.serverTick();

		//Every 30 seconds, check if we still have sky view and cache the result
		if(newlyPlaced || level.getGameTime() % 600 == 0){
			running = level.canSeeSkyFromBelowWater(worldPosition);
			newlyPlaced = false;
		}

		//This machine can share heat with other Solar Heaters in the same line, but only other Solar Heaters. Otherwise, a heat cable is needed like normal
		BlockEntity adjTE = level.getBlockEntity(worldPosition.relative(Direction.get(Direction.AxisDirection.NEGATIVE, level.getBlockState(worldPosition).getValue(CRProperties.HORIZ_AXIS))));
		if(adjTE instanceof SolarHeaterTileEntity otherTE){
			temp += otherTE.temp;
			temp /= 2;
			otherTE.temp = temp;
			setChanged();
			otherTE.setChanged();
		}

		if(running && temp < CAP && level.isDay() && !level.isRaining()){
			temp = Math.min(CAP, temp + CRConfig.solarRate.get());
			setChanged();
		}
	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		running = nbt.getBoolean("running");
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		nbt.putBoolean("running", running);
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
