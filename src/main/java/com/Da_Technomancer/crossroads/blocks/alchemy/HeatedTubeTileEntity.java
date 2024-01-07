package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.AlchemyCarrierTE;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.heat.HeatUtil;
import com.Da_Technomancer.crossroads.api.heat.IHeatHandler;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.apache.commons.lang3.tuple.Pair;
import org.joml.Vector3f;

public class HeatedTubeTileEntity extends AlchemyCarrierTE{

	public static final BlockEntityType<HeatedTubeTileEntity> TYPE = CRTileEntity.createType(HeatedTubeTileEntity::new, CRBlocks.heatedTubeGlass, CRBlocks.heatedTubeCrystal);

	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_X = new Pair[] {Pair.of(new Vector3f(0, 7/16F, 7/16F), new Vector3f(1, 1F-7/16F, 1F-7/16F))};
	private static final Pair<Vector3f, Vector3f>[] RENDER_SHAPE_Z = new Pair[] {Pair.of(new Vector3f(7/16F, 7/16F, 0), new Vector3f(1F-7/16F, 1F-7/16F, 1))};

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
			cableTemp = getBiomeTemp();
		}
	}

	@Override
	protected EnumTransferMode[] getModes(){
		EnumTransferMode[] output = {EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE, EnumTransferMode.NONE};
		Direction outSide = getBlockState().getValue(CRProperties.HORIZ_FACING);
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

	@Override
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return getBlockState().getValue(CRProperties.HORIZ_FACING).getAxis() == Direction.Axis.X ? RENDER_SHAPE_X : RENDER_SHAPE_Z;
	}
}
