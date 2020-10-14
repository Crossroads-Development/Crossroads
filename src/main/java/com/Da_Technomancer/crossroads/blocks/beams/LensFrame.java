package com.Da_Technomancer.crossroads.blocks.beams;

import com.Da_Technomancer.crossroads.blocks.CRBlocks;
import com.Da_Technomancer.crossroads.tileentities.beams.LensFrameTileEntity;
import com.Da_Technomancer.essentials.ESConfig;
import com.Da_Technomancer.essentials.blocks.ESProperties;
import com.Da_Technomancer.essentials.blocks.redstone.IReadable;
import com.Da_Technomancer.essentials.blocks.redstone.RedstoneUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class LensFrame extends ContainerBlock implements IReadable{

	private static final VoxelShape[] SHAPE = new VoxelShape[3];

	static{
		SHAPE[0] = makeCuboidShape(6, 0, 0, 10, 16, 16);
		SHAPE[1] = makeCuboidShape(0, 6, 0, 16, 10, 16);
		SHAPE[2] = makeCuboidShape(0, 0, 6, 16, 16, 10);
	}

	public LensFrame(){
		super(CRBlocks.ROCK_PROPERTY);
		String name = "lens_frame";
		setRegistryName(name);
		CRBlocks.toRegister.add(this);
		CRBlocks.blockAddQue(this);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context){
		return SHAPE[state.get(ESProperties.AXIS).ordinal()];
	}

	@Override
	public TileEntity createNewTileEntity(IBlockReader worldIn){
		return new LensFrameTileEntity();
	}

	@Override
	public BlockRenderType getRenderType(BlockState state){
		return BlockRenderType.MODEL;
	}

//	@Override
//	@OnlyIn(Dist.CLIENT)
//	public BlockRenderLayer getRenderLayer(){
//		return BlockRenderLayer.CUTOUT;
//	}

	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context){
		return getDefaultState().with(ESProperties.AXIS, context.getNearestLookingDirection().getAxis());
	}

	@Override
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder){
		builder.add(ESProperties.AXIS);
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity playerIn, Hand hand, BlockRayTraceResult hit){
		if(!worldIn.isRemote){
			ItemStack stack = playerIn.getHeldItem(hand);

			if(ESConfig.isWrench(stack)){
				worldIn.setBlockState(pos, state.func_235896_a_(ESProperties.AXIS));
			}else{
				TileEntity te = worldIn.getTileEntity(pos);
				if(!(te instanceof LensFrameTileEntity)){
					return ActionResultType.SUCCESS;
				}
				LensFrameTileEntity lens = (LensFrameTileEntity) te;
				ItemStack held = lens.getItem();
				if(!held.isEmpty()){
					if(!playerIn.inventory.addItemStackToInventory(held)){
						ItemEntity dropped = playerIn.dropItem(held, false);
						if(dropped != null){
							dropped.setNoPickupDelay();
							dropped.setOwnerId(playerIn.getUniqueID());
						}
					}
					lens.setContents(0);
				}else if(!stack.isEmpty()){
					int id = lens.getIDFromItem(stack);
					if(id != 0){
						lens.setContents(id);
						stack.shrink(1);
					}
				}
			}
		}
		return ActionResultType.SUCCESS;
	}

	@Override
	public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving){
		TileEntity te = world.getTileEntity(pos);
		if(newState.getBlock() != this && te instanceof LensFrameTileEntity){
			InventoryHelper.spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), ((LensFrameTileEntity) te).getItem());
		}
		super.onReplaced(state, world, pos, newState, isMoving);
	}

	@Override
	public boolean hasComparatorInputOverride(BlockState state){
		return true;
	}

	@Override
	public int getComparatorInputOverride(BlockState blockState, World world, BlockPos pos){
		return RedstoneUtil.clampToVanilla(read(world, pos, blockState));
	}

	@Override
	public float read(World world, BlockPos pos, BlockState blockState){
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof LensFrameTileEntity){
			return ((LensFrameTileEntity) te).getRedstone();
		}
		return 0;
	}
}
