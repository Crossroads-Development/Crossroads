package com.Da_Technomancer.crossroads.tileentities.heat;

import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.templates.ModuleTE;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatReservoirTileEntity extends ModuleTE{

	@ObjectHolder("heat_reservoir")
	public static BlockEntityType<HeatReservoirTileEntity> TYPE = null;

	public HeatReservoirTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected boolean useHeat(){
		return true;
	}

	@Override
	protected HeatHandler createHeatHandler(){
		return new MassiveHeatHandler();
	}

	public CompoundTag getDropNBT(){
		CompoundTag nbt = new CompoundTag();
		heatHandler.init();
		nbt.putDouble("temp", temp);
		return nbt;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing){
		if(capability == Capabilities.HEAT_CAPABILITY){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(capability, facing);
	}

	private class MassiveHeatHandler extends HeatHandler{

		@Override
		public void addHeat(double heat){
			init();
			temp = Math.max(HeatUtil.ABSOLUTE_ZERO, temp + heat * 0.005D);
			setChanged();
		}
	}
}
