package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatedTubeTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("heated_tube")
	public static BlockEntityType<HeatedTubeTileEntity> TYPE = null;

	public HeatedTubeTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public HeatedTubeTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = HeatUtil.convertBiomeTemp(level, worldPosition);
		}
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = level.getBlockState(worldPosition).getValue(CRProperties.HORIZ_FACING);
		output[outSide.get3DDataValue()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().get3DDataValue()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void setRemoved(){
		super.setRemoved();
		heatOpt.invalidate();
	}

	private final LazyOptional<IHeatHandler> heatOpt = LazyOptional.of(HeatHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == getBlockState().getValue(CRProperties.HORIZ_FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		if(cap == Capabilities.HEAT_CAPABILITY && (side == null || side.getAxis() == Direction.Axis.Y)){
			return (LazyOptional<T>) heatOpt;
		}
		return super.getCapability(cap, side);
	}

	private class HeatHandler implements IHeatHandler{

		@Override
		public double getTemp(){
			initHeat();
			return cableTemp;
		}

		@Override
		public void setTemp(double tempIn){
			init = true;
			cableTemp = Math.max(HeatUtil.ABSOLUTE_ZERO, tempIn);
			//Shares heat between internal cable & contents
			dirtyReag = true;
			setChanged();
		}

		@Override
		public void addHeat(double tempChange){
			initHeat();
			cableTemp = Math.max(HeatUtil.ABSOLUTE_ZERO, cableTemp + tempChange);
			//Shares heat between internal cable & contents
			dirtyReag = true;
			setChanged();
		}
	}
}
