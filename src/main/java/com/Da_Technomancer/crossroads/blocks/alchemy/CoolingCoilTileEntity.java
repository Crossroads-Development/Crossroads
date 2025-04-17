package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalCapable;
import com.Da_Technomancer.crossroads.api.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.api.alchemy.ReagentHolderTE;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import org.apache.commons.lang3.tuple.Pair;


import org.joml.Vector3f;

import javax.annotation.Nullable;

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
		if(level != null){
			level.invalidateCapabilities(getBlockPos());
		}
	}

	@Override
	@Nullable
	public IChemicalHandler getChemicalHandler(Direction dir){
		if(dir == null || dir.getAxis() == getBlockState().getValue(CRProperties.HORIZ_FACING).getAxis()){
			return chemHandler;
		}
		return null;
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
