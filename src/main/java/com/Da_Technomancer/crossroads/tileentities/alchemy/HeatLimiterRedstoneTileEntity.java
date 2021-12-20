package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterRedstoneTileEntity extends HeatLimiterBasicTileEntity{

	@ObjectHolder("heat_limiter")
	public static BlockEntityType<HeatLimiterRedstoneTileEntity> TYPE = null;

	public HeatLimiterRedstoneTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public double getSetting(){
		return CircuitUtil.combineRedsSources(redsHandler);
	}

	@Override
	public void saveAdditional(CompoundTag nbt){
		super.saveAdditional(nbt);
		redsHandler.write(nbt);

	}

	@Override
	public void load(CompoundTag nbt){
		super.load(nbt);
		redsHandler.read(nbt);
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		redsOpt.invalidate();
	}

	public CircuitUtil.InputCircHandler redsHandler = new CircuitUtil.InputCircHandler();
	private final LazyOptional<IRedstoneHandler> redsOpt = CircuitUtil.makeBaseCircuitOptional(this, redsHandler, 0);

	@Override
	@SuppressWarnings("unchecked")
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction dir){
		if(cap == RedstoneUtil.REDSTONE_CAPABILITY){
			return (LazyOptional<T>) redsOpt;
		}
		return super.getCapability(cap, dir);
	}
}