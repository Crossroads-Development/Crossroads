package com.Da_Technomancer.crossroads.blocks.technomancy;

import com.Da_Technomancer.crossroads.CRConfig;
import com.Da_Technomancer.crossroads.api.rotary.IAxleCapable;
import com.Da_Technomancer.crossroads.api.rotary.IAxleHandler;
import com.Da_Technomancer.crossroads.api.templates.ModuleTE;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.blocks.CRTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class LodestoneTurbineTileEntity extends ModuleTE implements IAxleCapable{

	public static final BlockEntityType<LodestoneTurbineTileEntity> TYPE = CRTileEntity.createType(LodestoneTurbineTileEntity::new, CRBlocks.lodestoneTurbine);

	public static final double INERTIA = 300;
	public static final double MAX_SPEED = 10;

	public LodestoneTurbineTileEntity(BlockPos pos, BlockState state){
		super(TYPE, pos, state);
	}

	@Override
	protected double getMoInertia(){
		return INERTIA;
	}

	@Override
	public void serverTick(){
		super.serverTick();
		if(axleHandler.axis != null && validDimension() && axleHandler.getSpeed() < MAX_SPEED){
			axleHandler.addEnergy(CRConfig.lodestoneTurbinePower.get(), true);
			setChanged();
		}
	}

	private boolean validDimension(){
		return !level.dimensionType().natural();//See clock item property for reference
	}

	@Override
	@Nullable
	public IAxleHandler getAxleHandler(Direction dir){
		if(dir == Direction.UP){
			return axleHandler;
		}
		return null;
	}
}
