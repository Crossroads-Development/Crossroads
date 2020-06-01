package com.Da_Technomancer.crossroads.API.templates;

import com.Da_Technomancer.crossroads.API.alchemy.EnumTransferMode;
import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.essentials.ESConfig;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

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
			defaultState = defaultState.with(sideProp[i], getDefaultValue());
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

	/**
	 * Gets the default value for the blockstate- NOT for the TE
	 * @return Default value to place with for mode
	 */
	protected abstract T getDefaultValue();

	/**
	 * Gets the initial value for connection mode on a side
	 * This is the value given to the TE, not to the blockstate
	 * The TE and blockstate do not exist when this is called
	 * @param world The World
	 * @param pos The position where this will be
	 * @param side The side this is for
	 * @param neighTE The neighboring TE on that side
	 * @return What the initial connection value should be
	 */
	protected abstract T getValueForPlacement(World world, BlockPos pos, Direction side, @Nullable TileEntity neighTE);

	/**
	 * Gets the properties used for the conduit connections, in order of Direction indices
	 * @return A size 6 array of properties
	 */
	protected abstract Property<T>[] getSideProp();

	protected abstract VoxelShape[] getShapes();

	/**
	 * Interprets a connection property value as connected or not
	 * Don't check hasMatch (assume true), but may check another property (such as redstone)
	 * @param value The value of connection mode to evaluate with
	 * @param state The blockstate
	 * @param te This te
	 * @return Whether the mode should be considered connected
	 */
	protected abstract boolean evaluate(T value, BlockState state, @Nullable IConduitTE<T> te);

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(getSideProp());
	}

	/**
	 * Determines what the next connection mode should be
	 * Used for wrench adjusting
	 * @param prev The previous mode
	 * @return The next connection mode on this side
	 */
	protected abstract T cycleMode(T prev);

	@Override
	@SuppressWarnings("unchecked")
	public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos pos, BlockPos facingPos){
		if(worldIn.isRemote()){
			return stateIn;
		}
		TileEntity te = worldIn.getTileEntity(pos);
		try{
			if(te instanceof IConduitTE){
				int side = facing.getIndex();
				IConduitTE<T> cTE = (IConduitTE<T>) te;
				T mode = cTE.getModes()[side];
				boolean hasMatch = cTE.hasMatch(side, cTE.getModes()[side]);
				cTE.getHasMatch()[side] = hasMatch;
				cTE.getTE().markDirty();
				return stateIn.with(getSideProp()[side], hasMatch ? mode : getDefaultValue());
			}
		}catch(ClassCastException ignored){

		}
		return stateIn;
	}

	@Override
	public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving){
		if(oldState.getBlock() == state.getBlock() || worldIn.isRemote){
			return;
		}

		//We want to allow conduits to choose their starting states based on surroundings
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof IConduitTE){
			IConduitTE<T> cte = (IConduitTE<T>) te;
			for(int i = 0; i < 6; i++){
				Direction side = Direction.byIndex(i);
				T mode = getValueForPlacement(worldIn, pos, side, worldIn.getTileEntity(pos.offset(side)));
				cte.setData(i, cte.hasMatch(i, mode), mode);
			}
		}
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		int index = 0;
		TileEntity te = worldIn.getTileEntity(pos);
		if(te instanceof IConduitTE){
			IConduitTE<T> cte = (IConduitTE<T>) te;
			for(int i = 0; i < 6; i++){
				index |= evaluate(state.get(getSideProp()[i]), state, cte) ? 1 << i : 0;
			}
			return getShapes()[index];
		}
		return getShapes()[0];//Core shape
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		//Handle wrenching
		if(playerIn != null && hand != null){
			ItemStack held = playerIn.getHeldItem(hand);
			if(held.isEmpty()){
				return ActionResultType.PASS;
			}
			TileEntity te = worldIn.getTileEntity(pos);
			if(ESConfig.isWrench(held) && te instanceof IConduitTE){
				if(worldIn.isRemote){
					return ActionResultType.SUCCESS;
				}

				final double SIZE = getSize();
				IConduitTE<T> cte = (IConduitTE<T>) te;
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

//				Property<T> prop = getSideProp()[face];
				T newVal = cycleMode(cte.getModes()[face]);
				cte.setData(face, cte.hasMatch(face, newVal), newVal);
				onAdjusted(worldIn, pos, state, Direction.byIndex(face), newVal, cte);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.PASS;
	}

	protected void onAdjusted(World world, BlockPos pos, BlockState newState, Direction facing, T newVal, @Nullable IConduitTE<T> te){
//		//Turns out vanilla behaviour already calls this
//		if(te != null){
//			te.updateContainingBlockInfo();
//		}
	}

	public interface IConduitTE<T extends Comparable<T>>{

		static EnumTransferMode[] genModeArray(EnumTransferMode defaul){
			EnumTransferMode[] out = new EnumTransferMode[6];
			Arrays.fill(out, defaul);
			return out;
		}

		@Nonnull
		default TileEntity getTE(){
			return (TileEntity) this;
		}

		/**
		 * Result must be mutable and write back to the TE
		 * @return The hasMatch array, size 6
		 */
		@Nonnull
		boolean[] getHasMatch();

		/**
		 * Result must be mutable and write back to the TE
		 * @return The modes array, size 6
		 */
		@Nonnull
		T[] getModes();

		@Nonnull
		T deserialize(String name);

		/**
		 * Whether this block could connect to a neighbor
		 * This method should check the actual world, instead of relying on the cached hasMatch value
		 * @param side The index of the side to check
		 * @param mode The mode to evaluate with respect to.
		 * @return Whether this block is allowed to connect, regardless of the state of this block
		 */
		boolean hasMatch(int side, T mode);

		default void setData(int side, boolean newMatch, @Nonnull T mode){
			boolean[] matches = getHasMatch();
			T[] modes = getModes();
			if(modes[side] == mode && newMatch == matches[side]){
				return;//No change
			}

			TileEntity te = getTE();
			BlockState prevState = te.getBlockState();
			ConduitBlock<T> block = (ConduitBlock<T>) prevState.getBlock();
			//Store previous value
			T defaul = block.getDefaultValue();
			T prev = matches[side] ? modes[side] : defaul;
			//Update values
			matches[side] = newMatch;
			modes[side] = mode;
			te.markDirty();
			//Check for updating blockstate in world
			T curr = matches[side] ? modes[side] : defaul;
			if(!curr.equals(prev)){
				//Update the state in world without block update
				te.getWorld().setBlockState(te.getPos(), prevState.with(block.getSideProp()[side], curr), 2);
			}
		}

		static EnumTransferMode deserializeEnumMode(String name){
			return EnumTransferMode.fromString(name);
		}

		static <T extends Comparable<T>> void writeConduitNBT(CompoundNBT nbt, IConduitTE<T> te){
			boolean[] hasMatch = te.getHasMatch();
			T[] modes = te.getModes();
			for(int i = 0; i < 6; i++){
				nbt.putBoolean(i + "_match", hasMatch[i]);
				nbt.putString(i + "_mode", modes[i].toString());
			}
		}

		static <T extends Comparable<T>> void readConduitNBT(CompoundNBT nbt, IConduitTE<T> te){
			boolean[] hasMatch = te.getHasMatch();
			T[] modes = te.getModes();
			for(int i = 0; i < 6; i++){
				hasMatch[i] = nbt.getBoolean(i + "_match");
				modes[i] = te.deserialize(nbt.getString(i + "_mode"));
			}
		}
	}
}
