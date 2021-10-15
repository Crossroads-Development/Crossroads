package com.Da_Technomancer.crossroads.tileentities.technomancy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.API.rotary.RotaryUtil;
import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.Crossroads;

import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ObjectHolder;

@ObjectHolder(Crossroads.MODID)
public class HamsterWheelTileEntity extends BlockEntity implements ITickableTileEntity{

	@ObjectHolder("hamster_wheel")
	public static BlockEntityType<HamsterWheelTileEntity> TYPE = null;

	public float angle = 0;
	public float nextAngle = 0;

	public HamsterWheelTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	public void tick(){
		Direction facing = getBlockState().getValue(CRProperties.HORIZ_FACING);
		BlockEntity te = level.getBlockEntity(worldPosition.relative(facing));
		LazyOptional<IAxleHandler> axleOpt;
		if(te != null && (axleOpt = te.getCapability(Capabilities.AXLE_CAPABILITY, facing.getOpposite())).isPresent()){
			IAxleHandler axle = axleOpt.orElseThrow(NullPointerException::new);
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
