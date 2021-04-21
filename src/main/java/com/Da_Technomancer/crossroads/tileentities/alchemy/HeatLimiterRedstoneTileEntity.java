package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CircuitUtil;
import com.Da_Technomancer.crossroads.Crossroads;
import com.Da_Technomancer.essentials.blocks.redstone.IRedstoneHandler;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatLimiterRedstoneTileEntity extends HeatLimiterBasicTileEntity{

	@ObjectHolder("heat_limiter")
	private static TileEntityType<HeatLimiterRedstoneTileEntity> type = null;

	public HeatLimiterRedstoneTileEntity(){
		super(type);
	}

	@Override
	public double getSetting(){
		return CircuitUtil.combineRedsSources(redsHandler);
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt){
		super.save(nbt);
		redsHandler.write(nbt);

		return nbt;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt){
		super.load(state, nbt);
		redsHandler.read(state, nbt);
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