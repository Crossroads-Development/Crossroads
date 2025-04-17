package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CircuitUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneCapable;
import com.Da_Technomancer.essentials.api.redstone.IRedstoneHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;


public class HeatLimiterRedstoneTileEntity extends HeatLimiterBasicTileEntity implements IRedstoneCapable{

	public static final BlockEntityType<HeatLimiterRedstoneTileEntity> TYPE = CRTileEntity.createType(HeatLimiterRedstoneTileEntity::new, CRBlocks.heatLimiterRedstone);

	public CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private final IRedstoneHandler redstoneHandler = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	public HeatLimiterRedstoneTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public double getSetting(){
		return CircuitUtil.combineRedsSources(redsHandler);
	}

	@Override
	protected void saveAdditional(CompoundTag nbt, HolderLookup.Provider pRegistries){
		super.saveAdditional(nbt, pRegistries);
		redsHandler.write(nbt);

	}

	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries){
		super.loadAdditional(nbt, registries);
		redsHandler.read(nbt);
	}

	@Nullable
	@Override
	public IRedstoneHandler getRedstoneHandler(Direction direction){
		return redstoneHandler;
	}
}