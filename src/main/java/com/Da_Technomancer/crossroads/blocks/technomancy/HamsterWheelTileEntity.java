package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.CRCapabilities;
import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

public class HamsterWheelTileEntity extends BlockEntity implements ITickableTileEntity{

	public static final BlockEntityType<HamsterWheelTileEntity> TYPE = CRTileEntity.createType(HamsterWheelTileEntity::new, CRBlocks.hamsterWheel);

	public float angle = 0;
	public float nextAngle = 0;

	public HamsterWheelTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void tick(){
		Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
		IAxleHandler axle = level.getCapability(CRCapabilities.AXLE_CAPABILITY, worldPosition.relative(facing), facing.getOpposite());
		if(axle != null){
			if(level.isClientSide){
				angle = axle.getAngle(0);
				nextAngle = axle.getAngle(1F);
				return;
			}
			axle.addEnergy(CRConfig.hamsterPower.get() * RotaryUtil.getCCWSign(facing), true);
		}else if(level.isClientSide){
			nextAngle = angle;
		}
	}
}
