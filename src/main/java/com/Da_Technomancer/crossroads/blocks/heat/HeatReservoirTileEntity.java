package com.Da_Technomancer.crossroads.blocks.heat;

import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;


public class HeatReservoirTileEntity extends ModuleTE{

	public static final BlockEntityType<HeatReservoirTileEntity> TYPE = CRTileEntity.createType(HeatReservoirTileEntity::new, CRBlocks.heatReservoir);

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

	@Override
	@Nullable
	public IHeatHandler getHeatHandler(Direction dir){
		return heatHandler;
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
