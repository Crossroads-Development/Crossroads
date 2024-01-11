package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.Capabilities;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentHolderTE;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
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

public class CoolingCoilTileEntity extends ReagentHolderTE{

	public static final BlockEntityType<CoolingCoilTileEntity> TYPE = CRTileEntity.createType(CoolingCoilTileEntity::new, CRBlocks.coolingCoilGlass, CRBlocks.coolingCoilCrystal);

	public CoolingCoilTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	public CoolingCoilTileEntity(BlockPos pos, BlockState state, boolean glass){
		super(TYPE, pos, state, glass);
	}

	@Override
	public double correctTemp(){
		//Shares heat between internal cable & contents
		double temp = getBiomeTemp();
		contents.setTemp(temp);
		return temp;
	}

	public void rotate(){
		chemOpt.invalidate();
		chemOpt = LazyOptional.of(() -> handler);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side){
		if(cap == Capabilities.CHEMICAL_CAPABILITY && (side == null || side.getAxis() == getBlockState().getValue(CRProperties.HORIZ_FACING).getAxis())){
			return (LazyOptional<T>) chemOpt;
		}
		return super.getCapability(cap, side);
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
	public Pair<Vector3f, Vector3f>[] getRenderVolumes(){
		return new Pair[0];//No-op
	}
}
