package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.CRProperties;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class ConduitBlock<T extends Comparable<T>> extends ContainerBlock{

	/**
	 * Generates an array of 64 possible shapes for this conduit based on size.
	 * CACHE THE RESULT!
	 * @param size The size of this conduit
	 * @return All possible shapes, indexed by each direction having it's associated bit (by getIndex) 1 or 0
	 */
	protected static VoxelShape[] generateShapes(double size){
		VoxelShape[] shapes = new VoxelShape[64];
		final double size16 = 16 * size;
		final double size16N = 16D - size16;
		//There are 64 (2^6) possible states for this block, and each one has a different shape
		//This... is gonna take a while
		VoxelShape core = makeCuboidShape(size16, size16, size16, size16N, size16N, size16N);
		VoxelShape[] pieces = new VoxelShape[6];
		pieces[0] = makeCuboidShape(size16, 0, size16, size16N, size16, size16N);
		pieces[1] = makeCuboidShape(size16, 16, size16, size16N, size16N, size16N);
		pieces[2] = makeCuboidShape(size16, size16, 0, size16N, size16N, size16);
		pieces[3] = makeCuboidShape(size16, size16, 16, size16N, size16N, size16N);
		pieces[4] = makeCuboidShape(0, size16, size16, size16, size16N, size16N);
		pieces[5] = makeCuboidShape(16, size16, size16, size16N, size16N, size16N);
		for(int i = 0; i < 64; i++){
			VoxelShape comp = core;
			for(int j = 0; j < 6; j++){
				if((i & (1 << j)) != 0){
					comp = VoxelShapes.or(comp, pieces[j]);
				}
			}
			shapes[i] = comp;
		}
		return shapes;
	}

	protected ConduitBlock(Properties builder){
		super(builder);
		BlockState defaultState = getDefaultState();
		Property<T>[] sideProp = getSideProp();
		for(int i = 0; i < 6; i++){
			defaultState = defaultState.with(sideProp[i], getDefaultValue()).with(CRProperties.HAS_MATCH_SIDES[i], false);
		}
		setDefaultState(defaultState);
		CRBlocks.toRegister.add(this);
	}

	/**
	 * Gets the "size" of this structure. Size is the distance from the bottom of the blockspace to the bottom of the conduit center-section
	 * A higher size means longer, thinner conduits
	 * @return The size (0, .5)
	 */
	protected abstract double getSize();

	protected abstract T getDefaultValue();

	/**
	 * Gets the initial value for connection mode on a side
	 * The TE and blockstate do not exist when this is called
	 * @param world The World
	 * @param pos The position where this will be
	 * @param side The side this is for
	 * @param neighTE The neighboring TE on that side
	 * @return What the initial connection value should be
	 */
	protected T getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE){
		return getDefaultValue();
	}

	/**
	 * Gets the properties used for the conduit connections, in order of Direction indices
	 * @return A size 6 array of properties
	 */
	protected abstract Property<T>[] getSideProp();

	protected abstract VoxelShape[] getShapes();

	/**
	 * Interprets a connection property value as connected or not
	 * Don't check HAS_MATCH_SIDES (assume true), but may check another property
	 * @param value The value of connection mode to evaluate
	 * @param state The blockstate
	 * @param te This te
	 * @return Whether it should be considered connected
	 */
	protected abstract boolean evaluate(T value, BlockState state, @Nullable TileEntity te);

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(getSideProp()).add(CRProperties.HAS_MATCH_SIDES);
	}

	/**
	 * Whether this block could connect to a neighbor
	 * @param world The world
	 * @param pos The position of this block
	 * @param side The side to check
	 * @param connectMode Connect mode on this side
	 * @param thisTE This tile entity. May be null, especially if this is still being placed
	 * @param neighTE The neighboring TE. May be null.
	 * @return Whether this block is allowed to connect, regardless of the state of this block
	 */
	protected abstract boolean hasMatch(IWorld world, BlockPos pos, Direction side, T connectMode, @Nullable TileEntity thisTE, @Nullable TileEntity neighTE);

	/**
	 * Determines what the next connection mode should be
	 * @param prev The previous mode
	 * @return The next connection mode on this side
	 */
	protected abstract T cycleMode(T prev);

	@Override
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos pos, BlockPos facingPos){
		TileEntity te = worldIn.getTileEntity(pos);
		if(te != null){
			te.updateContainingBlockInfo();
		}
		return stateIn.with(CRProperties.HAS_MATCH_SIDES[facing.getIndex()], hasMatch(worldIn, pos, facing, stateIn.get(getSideProp()[facing.getIndex()]), te, worldIn.getTileEntity(facingPos)));
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		BlockState newState = getDefaultState();
		for(int i = 0; i < 6; i++){
			TileEntity neighTE = context.getWorld().getTileEntity(context.getPos().offset(Direction.byIndex(i)));
			T startVal = getValueForPlacement(context.getWorld(), context.getPos(), Direction.byIndex(i), neighTE);
			newState = newState.with(getSideProp()[i], startVal);
			newState = newState.with(CRProperties.HAS_MATCH_SIDES[i], hasMatch(context.getWorld(), context.getPos(), Direction.byIndex(i), startVal, null, neighTE));
		}
		return newState;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		int index = 0;
		TileEntity te = worldIn.getTileEntity(pos);
		for(int i = 0; i < 6; i++){
			index |= state.get(CRProperties.HAS_MATCH_SIDES[i]) && evaluate(state.get(getSideProp()[i]), state, te) ? 1 << i : 0;
		}
		return getShapes()[index];
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public boolean onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		//Handle wrenching
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return false;
			}
			if(ESConfig.isWrench(held)){
				TileEntity te = worldIn.getTileEntity(pos);
				final double SIZE = getSize();
				int face;
				final double margin = 0.005D;
				Vec3d hitVec = hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ());
				if(hitVec.y < SIZE - margin){
					face = 0;//Down
				}else if(hitVec.y - margin > 1F - (float) SIZE){
					face = 1;//Up
				}else if(hitVec.x < (float) SIZE - margin){
					face = 4;//West
				}else if(hitVec.x - margin > 1F - (float) SIZE){
					face = 5;//East
				}else if(hitVec.z < (float) SIZE - margin){
					face = 2;//North
				}else if(hitVec.z - margin > 1F - (float) SIZE){
					face = 3;//South
				}else{
					face = hit.getFace().getIndex();
				}

				Property<T> prop = getSideProp()[face];
				T newVal = cycleMode(state.get(prop));
				Direction side = Direction.byIndex(face);
				TileEntity neighTE = worldIn.getTileEntity(pos.offset(side));
				state = state.with(prop, newVal);
				state = state.with(CRProperties.HAS_MATCH_SIDES[face], hasMatch(worldIn, pos, side, newVal, te, neighTE));
				worldIn.setBlockState(pos, state);
				onAdjusted(worldIn, pos, state, side, newVal, te);
				return true;
			}
		}
		return false;
	}

	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, T newVal, @Nullable TileEntity te){
//		//Turns out vanilla behaviour already calls this
//		if(te != null){
//			te.updateContainingBlockInfo();
//		}
	}

	public void forceMode(World world, BlockPos pos, BlockState prevState, Direction facing, T newVal){
		prevState = prevState.with(getSideProp()[facing.getIndex()], newVal);
		prevState = prevState.with(CRProperties.HAS_MATCH_SIDES[facing.getIndex()], hasMatch(world, pos, facing, newVal, world.getTileEntity(pos), world.getTileEntity(pos.offset(facing))));
		world.setBlockState(pos, prevState);
		TileEntity te = world.getTileEntity(pos);
		if(te != null){
			te.updateContainingBlockInfo();
		}
	}
}
