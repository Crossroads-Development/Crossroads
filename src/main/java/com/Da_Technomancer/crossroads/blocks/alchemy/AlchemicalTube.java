package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlchemicalTubeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

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
		setRegistryName(name);
		CRBlocks.blockAddQue(this);
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
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new AlchemicalTubeTileEntity(!crystal);
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.TRANSLUCENT;
//	}

	@Override
	protected EnumTransferMode getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE){
		//If adjacent to another pipe, set the initial mode based on the other pipe for continuous flow
		if(neighTE instanceof AlchemicalTubeTileEntity){
			EnumTransferMode otherMode = ((AlchemicalTubeTileEntity) neighTE).getModes()[side.getOpposite().getIndex()];
			if(otherMode == EnumTransferMode.OUTPUT){
				return EnumTransferMode.INPUT;
			}else if(otherMode == EnumTransferMode.INPUT){
				return EnumTransferMode.OUTPUT;
			}
		}
		return EnumTransferMode.INPUT;
	}

	@Override
	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable IConduitTE<EnumTransferMode> te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		TileEntity neighTE = world.getTileEntity(pos.offset(facing));
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

			((AlchemicalTubeTileEntity) neighTE).setData(facing.getOpposite().getIndex(), newVal.isConnection(), otherMode);
		}
	}
}
