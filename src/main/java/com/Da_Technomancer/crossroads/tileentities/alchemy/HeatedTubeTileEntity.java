package com.Da_Technomancer.crossroads.tileentities.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.heat.HeatUtil;
import com.Da_Technomancer.crossroads.API.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.Crossroads;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HeatedTubeTileEntity extends AlchemyCarrierTE{

	@ObjectHolder("heated_tube")
	private static TileEntityType<HeatedTubeTileEntity> type = null;

	public HeatedTubeTileEntity(){
		super(type);
	}

	public HeatedTubeTileEntity(boolean glass){
		super(type, glass);
	}

	@Override
	protected boolean useCableHeat(){
		return true;
	}

	@Override
	protected void initHeat(){
		if(!init){
			init = true;
			cableTemp = HeatUtil.convertBiomeTemp(world, pos);
		}
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = world.getBlockState(pos).get(CRProperties.HORIZ_FACING);
		output[outSide.getIndex()] = EnumTransferMode.OUTPUT;
		output[outSide.getOpposite().getIndex()] = EnumTransferMode.INPUT;
		return output;
	}

	@Override
	public void remove(){
		super.remove();
		heatOpt.invalidate();
	}

	private final LazyOptional<IHeatHandler> heatOpt = LazyOptional.of(HeatHandler::new);

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == world.getBlockState(pos).get(CRProperties.HORIZ_FACING).getAxis())){
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
			cableTemp = tempIn;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}

		@Override
		public void addHeat(double tempChange){
			initHeat();
			cableTemp += tempChange;
			//Shares heat between internal cable & contents
			dirtyReag = true;
			markDirty();
		}
	}
}
