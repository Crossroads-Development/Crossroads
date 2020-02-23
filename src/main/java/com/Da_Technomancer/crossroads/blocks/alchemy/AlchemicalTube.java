package com.Da_Technomancer.crossroads.blocks.alchemy;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.API.Capabilities;
import com.Da_Technomancer.crossroads.API.alchemy.EnumContainerType;
import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.API.alchemy.IChemicalHandler;
import com.Da_Technomancer.crossroads.API.templates.ConduitBlock;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.alchemy.AlchemicalTubeTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.state.Property;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;

public class AlchemicalTube extends ConduitBlock<EnumTransferMode>{

	private static final double SIZE = 5D / 16D;
	protected static final VoxelShape[] SHAPES = generateShapes(SIZE);

	protected final boolean crystal;

	public AlchemicalTube(boolean crystal){
		this(crystal, (crystal ? "crystal_" : "") + "alch_tube");
	}

	protected AlchemicalTube(boolean crystal, String name){
		super(Properties.create(Material.GLASS).hardnessAndResistance(0.5F).sound(SoundType.GLASS));
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
	protected boolean evaluate(EnumTransferMode value, BlockState state, @Nullable TileEntity te){
		return value.isConnection();
	}

	@Override
	protected boolean hasMatch(IWorld world, BlockPos pos, Direction side, EnumTransferMode connectMode, @Nullable TileEntity thisTE, @Nullable TileEntity neighTE){
		//Check for a neighbor w/ an alchemy reagent handler of a compatible channel
		LazyOptional<IChemicalHandler> otherOpt;
		return neighTE != null && (otherOpt = neighTE.getCapability(Capabilities.CHEMICAL_CAPABILITY, side.getOpposite())).isPresent() && otherOpt.orElseThrow(NoSuchFieldError::new).getChannel(side.getOpposite()).connectsWith(crystal ? EnumContainerType.CRYSTAL : EnumContainerType.GLASS);
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
		return EnumTransferMode.INPUT;
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new AlchemicalTubeTileEntity(!crystal);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BlockRenderLayer getRenderLayer(){
		return BlockRenderLayer.TRANSLUCENT;
	}


	@Override
	protected EnumTransferMode getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE){
		BlockState neighState = world.getBlockState(pos.offset(side));
		//If adjacent to another pipe, set the initial mode based on the other pipe for continuous flow
		if(neighState.getBlock() instanceof AlchemicalTube){
			EnumTransferMode otherMode = neighState.get(getSideProp()[side.getOpposite().getIndex()]);
			if(otherMode == EnumTransferMode.OUTPUT){
				return EnumTransferMode.INPUT;
			}else if(otherMode == EnumTransferMode.INPUT){
				return EnumTransferMode.OUTPUT;
			}
		}
		return getDefaultValue();
	}

	@Override
	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, EnumTransferMode newVal, @Nullable TileEntity te){
		super.onAdjusted(world, pos, newState, facing, newVal, te);

		BlockState neighState = world.getBlockState(pos.offset(facing));
		if(neighState.getBlock() instanceof AlchemicalTube){
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
			((AlchemicalTube) neighState.getBlock()).forceMode(world, pos.offset(facing), neighState, facing.getOpposite(), otherMode);
		}
	}
}
