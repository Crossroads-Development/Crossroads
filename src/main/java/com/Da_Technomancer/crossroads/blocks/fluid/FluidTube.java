package com.Da_Technomancer.crossroads.blocks.fluid;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.fluid.FluidTubeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class FluidTube extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 5D / 16D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);

	public FluidTube(){
		this("fluid_tube");
	}

	protected FluidTube(String name){
		super(Properties.create(Material.IRON).sound(SoundType.METAL).hardnessAndResistance(2));
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
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new FluidTubeTileEntity();
	}

	@Override
	protected EnumTransferMode getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE){
		//If adjacent to another pipe, set the initial mode based on the other pipe for continuous flow
		if(neighTE instanceof FluidTubeTileEntity){
			EnumTransferMode otherMode = ((FluidTubeTileEntity) neighTE).getModes()[side.getOpposite().getIndex()];
			switch(otherMode){
				case INPUT:
					return EnumTransferMode.OUTPUT;
				case OUTPUT:
					return EnumTransferMode.INPUT;
				case BOTH:
					return EnumTransferMode.BOTH;
				case NONE:
					return EnumTransferMode.NONE;
			}
		}
		return EnumTransferMode.INPUT;
	}

	@Override
	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		TileEntity neighTE = world.getTileEntity(pos.offset(facing));
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
			((FluidTubeTileEntity) neighTE).setData(facing.getOpposite().getIndex(), otherMode.isConnection(), otherMode);
		}
	}
}
