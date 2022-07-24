package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.api.CRProperties;
import com.Da_Technomancer.crossroads.api.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.api.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.api.ITickableTileEntity;
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

public class AlchemicalTube extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 5D / 16D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);

	protected final boolean crystal;

	public AlchemicalTube(boolean crystal){
		this(crystal, (crystal ? "crystal_" : "") + "alch_tube");
	}

	protected AlchemicalTube(boolean crystal, String name){
		super(CRBlocks.getGlassProperty());
		this.crystal = crystal;
		CRBlocks.toRegister.put(name, this);
		CRBlocks.blockAddQue(name, this);
	}

	@Override
	protected Property<EnumTransferMode>[] getSideProp(){
		return CRProperties.CONDUIT_SIDES_SINGLE;
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
			case NONE:
				return EnumTransferMode.INPUT;
			case INPUT:
				return EnumTransferMode.OUTPUT;
			case OUTPUT:
			default:
				return EnumTransferMode.NONE;
		}
	}

	@Override
	protected double getSize(){
		return SIZE;
	}

	@Override
	protected EnumTransferMode getDefaultValue(){
		return EnumTransferMode.NONE;
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state){
		return new AlchemicalTubeTileEntity(pos, state, !crystal);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> type){
		return ITickableTileEntity.createTicker(type, AlchemicalTubeTileEntity.TYPE);
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	@Override
	protected EnumTransferMode getValueForPlacement(Level world, BlockPos pos, Direction side, @Nullable BlockEntity neighTE){
		//If adjacent to another pipe, set the initial mode based on the other pipe for continuous flow
		if(neighTE instanceof AlchemicalTubeTileEntity){
			EnumTransferMode otherMode = ((AlchemicalTubeTileEntity) neighTE).getModes()[side.getOpposite().get3DDataValue()];
			if(otherMode == EnumTransferMode.OUTPUT){
				return EnumTransferMode.INPUT;
			}else if(otherMode == EnumTransferMode.INPUT){
				return EnumTransferMode.OUTPUT;
			}
		}
		return EnumTransferMode.INPUT;
	}

	@Override
	protected void onAdjusted(Level world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		BlockEntity neighTE = world.getBlockEntity(pos.relative(facing));
		//Check the neighbor is another conduit with the same channel
		if(neighTE instanceof AlchemicalTubeTileEntity && ((AlchemicalTube) neighTE.getBlockState().getBlock()).crystal == crystal){
			//Adjust the neighboring pipe alongside this one
			EnumTransferMode otherMode;
			switch(newVal){
				case INPUT:
					otherMode = EnumTransferMode.OUTPUT;
					break;
				case OUTPUT:
					otherMode = EnumTransferMode.INPUT;
					break;
				case NONE:
				default:
					otherMode = EnumTransferMode.NONE;
					break;
			}

			((AlchemicalTubeTileEntity) neighTE).setData(facing.getOpposite().get3DDataValue(), newVal.isConnection(), otherMode);
		}
	}
}
