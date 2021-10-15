package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;
import com.Da_Technomancer.essentials.tileentities.ITickableTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class FluidTube extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 5D / 16D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);

	public FluidTube(){
		this("fluid_tube");
	}

	protected FluidTube(String name){
		super(CRBlocks.getMetalProperty());
		setRegistryName(name);
		CRBlocks.blockAddQue(this);
	}

	@Override
	protected EnumTransferMode getDefaultValue(){
		return EnumTransferMode.NONE;
	}

	@Override
	protected double getSize(){
		return SIZE;
	}

	@Override
	protected Property<EnumTransferMode>[] getSideProp(){
		return CRProperties.CONDUIT_SIDES_FULL;
	}

	@Override
	protected VoxelShape[] getShapes(){
		return SHAPES;
	}

	@Override
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable IConduitTE<EnumTransferMode> te){
		return value.isConnection();
	}

	@Override
	protected EnumTransferMode cycleMode(EnumTransferMode prev){
		switch(prev){
			case INPUT:
				return EnumTransferMode.OUTPUT;
			case OUTPUT:
				return EnumTransferMode.BOTH;
			case BOTH:
				return EnumTransferMode.NONE;
			case NONE:
			default:
				return EnumTransferMode.INPUT;
		}
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new FluidTubeTileEntity(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, FluidTubeTileEntity.TYPE);
	}

	@Override
	protected EnumTransferMode getValueForPlacement(Level world, BlockPos pos, Direction side, @Nullable BlockEntity neighTE){
		//If adjacent to another pipe, set the initial mode based on the other pipe for continuous flow
		if(neighTE instanceof FluidTubeTileEntity){
			EnumTransferMode otherMode = ((FluidTubeTileEntity) neighTE).getModes()[side.getOpposite().get3DDataValue()];
			return switch(otherMode){
				case INPUT -> EnumTransferMode.OUTPUT;
				case OUTPUT -> EnumTransferMode.INPUT;
				case BOTH -> EnumTransferMode.BOTH;
				case NONE -> EnumTransferMode.NONE;
			};
		}
		return EnumTransferMode.INPUT;
	}

	@Override
	protected void onAdjusted(Level world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		BlockEntity neighTE = world.getBlockEntity(pos.relative(facing));
		if(neighTE instanceof FluidTubeTileEntity){
			//Adjust the neighboring pipe alongside this one
			EnumTransferMode otherMode;
			switch(newVal){
				case INPUT:
					otherMode = EnumTransferMode.OUTPUT;
					break;
				case OUTPUT:
					otherMode = EnumTransferMode.INPUT;
					break;
				case BOTH:
					otherMode = EnumTransferMode.BOTH;
					break;
				case NONE:
				default:
					otherMode = EnumTransferMode.NONE;
					break;
			}
			((FluidTubeTileEntity) neighTE).setData(facing.getOpposite().get3DDataValue(), otherMode.isConnection(), otherMode);
		}
	}
}
